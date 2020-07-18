package com.quartzy;

import com.google.gson.*;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.KeyReply;
import net.hypixel.api.reply.skyblock.BazaarReply;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HypixelUtil{
    
    private static HypixelUtil instance;
    private Map<String, BazaarReply.Product> products;
    
    private UUID apiToken;
    private HypixelAPI API;
    
    private JsonObject merchantSellValues;
    private JsonObject minionData;
    
    private JsonArray resources_50_perc_drop_chance;
    private JsonArray resources_90_perc_drop_chance;
    private JsonArray resources_10_perc_drop_chance;
    private JsonArray resources_dropping_4_items;
    private JsonObject resources_dropping_variable_items;
    private JsonObject minion_categories;
    
    private static HypixelAPI tempAPI;
    
    public static UUID key;
    
    public static CompletableFuture<KeyReply> validateKey(UUID apiToken){
        tempAPI = new HypixelAPI(apiToken);
        return tempAPI.getKey();
    }
    
    public static void killTemp(){
        tempAPI.shutdown();
        tempAPI = null;
    }
    
    public HypixelUtil(UUID apiToken){
        instance = this;
        this.apiToken = apiToken;
        this.API = new HypixelAPI(this.apiToken);
        API.getBazaar().whenComplete((bazaarReply, throwable) -> {
            if(throwable!=null){
                throwable.printStackTrace();
                System.exit(-1);
            }
            products = bazaarReply.getProducts();
        });
        createKeys();
        getAllPrices();
        addMinion("revenant", new double[]{29, 29, 26, 26, 23, 23, 19, 19, 14.5, 14.5, 10}, new String[]{"rotten-flesh", "diamond"}, "Combat");
        addMinion("tarantula", new double[]{29, 29, 26, 26, 23, 23, 19, 19, 14.5, 14.5, 10}, new String[]{"string", "spider-eye", "iron-ingot"}, "Combat");
    }
    
    private void addMinion(String name, double[] timings, String[] resources, String category){
        JsonObject value = new JsonObject();
        JsonArray value1 = new JsonArray();
        for(double timing : timings){
            value1.add(new JsonPrimitive(timing));
        }
        value.add("timings", value1);
        JsonArray resources1 = new JsonArray();
        for(String resource : resources){
            resources1.add(new JsonPrimitive(resource));
        }
        value.add("resources", resources1);
        minionData.add(name, value);
        minion_categories.getAsJsonArray(category).add(new JsonPrimitive(name));
    }
    
    private void getAllPrices(){
        JsonParser parser = new JsonParser();
        JsonElement minionData = null;
        JsonElement npcSellData = null;
        OkHttpClient httpClient = new OkHttpClient();
        Request minionDataRequest = new Request.Builder().url("https://minioniser.cf/constants15-06-20.2.js").build();
        try (Response response = httpClient.newCall(minionDataRequest).execute()) {
            String rawData = response.body().string();
            minionData = parser.parse(getJSVariable("minions", rawData, false));
            npcSellData = parser.parse(getJSVariable("sell_prices", rawData, false));
            resources_10_perc_drop_chance = parser.parse(getJSVariable("resources_10_perc_drop_chance", rawData, true)).getAsJsonArray();
            resources_50_perc_drop_chance = parser.parse(getJSVariable("resources_50_perc_drop_chance", rawData, true)).getAsJsonArray();
            minion_categories = parser.parse(getJSVariable("minion_categories", rawData, false)).getAsJsonObject();
            resources_90_perc_drop_chance = parser.parse(getJSVariable("resources_90_perc_drop_chance", rawData, true)).getAsJsonArray();
            resources_dropping_4_items = parser.parse(getJSVariable("resources_dropping_4_items", rawData, true)).getAsJsonArray();
            resources_dropping_variable_items = parser.parse(getJSVariable("resources_dropping_variable_items", rawData, false)).getAsJsonObject();
        } catch(IOException e){
            e.printStackTrace();
        }
        
        if(minionData!=null){
            this.minionData = minionData.getAsJsonObject();
        }
        if(npcSellData!=null){
            this.merchantSellValues = npcSellData.getAsJsonObject();
        }
    }
    
    private String getJSVariable(String varName, String raw, boolean array){
        if(array){
            String strPrt1 = "let " + varName + " = [";
            int fromIndex = raw.indexOf(strPrt1) + strPrt1.length();
            String initialVar = raw.substring(fromIndex, raw.indexOf("];\n", fromIndex));
            if(initialVar.endsWith(",")){
                initialVar = initialVar.substring(0, initialVar.length()-1);
            }
            initialVar = "[" + initialVar + "]";
            return initialVar;
        }else {
            String strPrt1 = "let " + varName + " = {";
            int fromIndex = raw.indexOf(strPrt1) + strPrt1.length();
            String initialVar = raw.substring(fromIndex, raw.indexOf("};\n", fromIndex));
            if(initialVar.endsWith(",")){
                initialVar = initialVar.substring(0, initialVar.length()-1);
            }
            if(initialVar.endsWith(",\n")){
                initialVar = initialVar.substring(0, initialVar.length()-2);
            }
            initialVar = "{" + initialVar + "}";
            return initialVar;
        }
    }
    
    public String[] getAllMinionNames(){
        Set<Map.Entry<String, JsonElement>> entries = minionData.entrySet();
        String[] minions = new String[entries.size()];
        int i = 0;
        for(Map.Entry<String, JsonElement> entry : entries){
            minions[i] = entry.getKey();
            i++;
        }
        return minions;
    }
    
    public double getCoinsPerHourMerchant(String minionName, int level){
        return getCoinsPerHourMerchant(minionName, level, Boost.NONE, Boost.NONE, Boost.NONE);
    }
    
    public double getCoinsPerHourMerchant(String minionName, int level, Boost boost, Boost upgrade1, Boost upgrade2){
        JsonObject minion = minionData.getAsJsonObject(minionName);
        double delay = minion.getAsJsonArray("timings").get(level-1).getAsDouble() * (1-(boost.speedBoost+upgrade1.speedBoost+upgrade2.speedBoost));
        if(boost==Boost.CATALYST){
            delay = (minion.getAsJsonArray("timings").get(level-1).getAsDouble()/3) * (1-(upgrade1.speedBoost+upgrade2.speedBoost));
        }
        for(Map.Entry<String, JsonElement> entry : minion_categories.entrySet()){
            if(entry.getValue().getAsJsonArray().contains(new JsonPrimitive(minionName))){
                if(!entry.getKey().equalsIgnoreCase("Farming - Mobs") && !entry.getKey().equalsIgnoreCase("Combat")){
                    delay = delay * 2;
                }
            }
        }
        double itemsCollected = 3600/delay;
        JsonArray resources = minion.getAsJsonArray("resources");
        
        double coins = 0;
        for(JsonElement resource : resources){
            coins += merchantSellValues.getAsJsonPrimitive(resource.getAsString()).getAsDouble() * (getResourceChance(resource, minionName) * itemsCollected);
        }
        
        return coins;
    }
    
    public double getCoinsPerHourBazaar(String minionName, int level){
        return getCoinsPerHourMerchant(minionName, level, Boost.NONE, Boost.NONE, Boost.NONE);
    }
    
    public double getCoinsPerHourBazaar(String minionName, int level, Boost boost, Boost upgrade1, Boost upgrade2){
        while(products==null){ }
        JsonObject minion = minionData.getAsJsonObject(minionName);
        double delay = minion.getAsJsonArray("timings").get(level-1).getAsDouble() * (1-(boost.speedBoost+upgrade1.speedBoost+upgrade2.speedBoost));
        if(boost==Boost.CATALYST){
            delay = (minion.getAsJsonArray("timings").get(level-1).getAsDouble()/3) * (1-(upgrade1.speedBoost+upgrade2.speedBoost));
        }
        for(Map.Entry<String, JsonElement> entry : minion_categories.entrySet()){
            if(entry.getValue().getAsJsonArray().contains(new JsonPrimitive(minionName))){
                if(!entry.getKey().equalsIgnoreCase("Farming - Mobs") && !entry.getKey().equalsIgnoreCase("Combat")){
                    delay = delay * 2;
                }
            }
        }
        double itemsCollected = 3600/delay;
        JsonArray resources = minion.getAsJsonArray("resources");
    
        
        double coins = 0;
        for(JsonElement resource : resources){
            double trueItems = (getResourceChance(resource, minionName) * itemsCollected);
            String key = keys.get(resource.getAsString());
            if(key==null){
                coins += merchantSellValues.getAsJsonPrimitive(resource.getAsString()).getAsDouble() * trueItems;
                continue;
            }
            BazaarReply.Product product = products.get(key);
            if(product==null){
                coins += merchantSellValues.getAsJsonPrimitive(resource.getAsString()).getAsDouble() * trueItems;
                continue;
            }
            double sellPrice = product.getQuickStatus().getSellPrice();
            coins += sellPrice * trueItems;
        }
        
        return coins;
    }
    
    private double getResourceChance(JsonElement resourceName, String minionName){
            if(resources_10_perc_drop_chance.contains(resourceName)){
                return 0.1;
            }
            if(resources_50_perc_drop_chance.contains(resourceName)){
                return 0.5;
            }
            if(resources_90_perc_drop_chance.contains(resourceName)){
                return 0.9;
            }
            if(resources_dropping_4_items.contains(resourceName)){
                return 4;
            }
            if(resources_dropping_variable_items.get(resourceName.getAsString())!=null){
                JsonObject asJsonObject = resources_dropping_variable_items.getAsJsonObject(resourceName.getAsString());
                return (asJsonObject.getAsJsonPrimitive("min").getAsDouble()+asJsonObject.getAsJsonPrimitive("max").getAsDouble())/2;
            }
            switch(resourceName.getAsString()){
                case "rabbits-foot":
                case "rabbit-hide":
                    return 0.1;
                case "spider-eye":
                    if(minionName.equalsIgnoreCase("tarantula"))return 1;
                    return 0.5;
                case "raw_fish":
                    return 0.5;
                case "pufferfush":
                    return 0.12;
                case "raw-salmon":
                    return 0.25;
                case "clownfish":
                    return 0.04;
                case "sponge":
                case "prismarine-shard":
                case "prismarine-crystals":
                    return 0.03;
                case "poisonous-potato":
                    return 0.02;
                case "string":
                    if(minionName.equalsIgnoreCase("tarantula"))return 3.16;
                    break;
                case "iron-ingot":
                    if(minionName.equalsIgnoreCase("tarantula"))return 0.2;
            }
            return 1;
    }
    
    public JsonObject getMerchantSellValues(){
        return merchantSellValues;
    }
    
    public JsonObject getMinionData(){
        return minionData;
    }
    
    public UUID getApiToken(){
        return apiToken;
    }
    
    public HypixelAPI getAPI(){
        return API;
    }
    
    public static HypixelUtil getInstance(){
        return instance;
    }
    
    private HashMap<String, String> keys;
    
    private void createKeys(){
        keys = new HashMap<>();
        keys.put("wheat", "WHEAT");
        keys.put("seeds", "SEEDS");
        keys.put("potato", "POTATO_ITEM");
        keys.put("carrot", "CARROT_ITEM");
        keys.put("melon", "MELON");
        keys.put("pumpkin", "PUMPKIN");
        keys.put("red-mushroom", "RED_MUSHROOM");
        keys.put("brown-mushroom", "BROWN_MUSHROOM");
        keys.put("cactus", "CACTUS");
        keys.put("cocoa-beans", "INK_SACK:3");
        keys.put("sugar-cane", "SUGAR_CANE");
        keys.put("raw-beef", "RAW_BEEF");
        keys.put("leather", "LEATHER");
        keys.put("raw-porkchop", "PORK");
        keys.put("raw-chicken", "RAW_CHICKEN");
        keys.put("feather", "FEATHER");
        keys.put("raw-mutton", "MUTTON");
        keys.put("raw-rabbit", "RABBIT");
        keys.put("rabbit-hide", "RABBIT_HIDE");
        keys.put("rabbits-foot", "RABBIT_FOOT");
        keys.put("cobblestone", "COBBLESTONE");
        keys.put("coal", "COAL");
        keys.put("iron-ingot", "IRON_INGOT");
        keys.put("gold-ingot", "GOLD_INGOT");
        keys.put("emerald", "EMERALD");
        keys.put("diamond", "DIAMOND");
        keys.put("lapis-lazuli", "INK_SACK:4");
        keys.put("redstone-dust", "REDSTONE");
        keys.put("nether-quartz", "QUARTZ");
        keys.put("end-stone", "ENDER_STONE");
        keys.put("glowstone-dust", "GLOWSTONE_DUST");
        keys.put("obsidian", "OBSIDIAN");
        keys.put("gravel", "GRAVEL");
        keys.put("flint", "FLINT");
        keys.put("sand", "SAND");
        keys.put("clay", "CLAY_BALL");
        keys.put("ice", "ICE");
        keys.put("snowball", "SNOW_BALL");
        keys.put("rotten-flesh", "ROTTEN_FLESH");
        keys.put("bone", "BONE");
        keys.put("string", "STRING");
        keys.put("spider-eye", "SPIDER_EYE");
        keys.put("ender-pearl", "ENDER_PEARL");
        keys.put("gunpowder", "GUNPOWDER");
        keys.put("blaze-rod", "BLAZE_ROD");
        keys.put("ghast-tear", "GHAST_TEAR");
        keys.put("slime-ball", "SLIME_BALL");
        keys.put("magma-cream", "MAGMA_CREAM");
        keys.put("raw-fish", "RAW_FISH");
        keys.put("pufferfish", "PUFFERFISH");
        keys.put("raw-salmon", "RAW_SALMON");
        keys.put("clownfish", "CLOWNFISH");
        keys.put("sponge", "SPONGE");
        keys.put("prismarine-shard", "PRISMARINE_SHARD");
        keys.put("prismarine-crystals", "PRISMARINE_CRYSTALS");
        keys.put("oak-wood", "LOG");
        keys.put("acacia-wood", "LOG_2");
        keys.put("spruce-wood", "LOG:1");
        keys.put("dark-oak-wood", "LOG_2:1");
        keys.put("birch-wood", "LOG:2");
        keys.put("jungle-wood", "LOG:3");
        keys.put("nether-wart", "NETHER_STALK");
    }
    
    public HashMap<String, String> getKeys(){
        return keys;
    }
}
