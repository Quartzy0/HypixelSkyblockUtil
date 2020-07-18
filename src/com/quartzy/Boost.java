package com.quartzy;

public enum Boost{
    NONE(0), COAL(0.05), ENCHANTED_BREAD(0.05), BLOCK_OF_COAL(0.05), ENCHANTED_COAL(0.1), ENCHANTED_CHARCOAL(0.2), SOLAR_PANEL(0.25), ENCHANTED_LAVA_BUCKET(0.25), HAMSTER_WHEEL(0.5), CATALYST(3), FOUL_FLESH(0.9), MINION_EXPANDER(0.05, true), FLYCATCHER(0.2, true);
    
    public final double speedBoost;
    public final boolean upgrade;
    
    Boost(double speedBoost){
        this.speedBoost = speedBoost;
        this.upgrade = false;
    }
    
    Boost(double speedBoost, boolean upgrade){
        this.speedBoost = speedBoost;
        this.upgrade = upgrade;
    }
}
