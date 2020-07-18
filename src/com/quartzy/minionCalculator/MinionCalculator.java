package com.quartzy.minionCalculator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.quartzy.Boost;
import com.quartzy.HypixelUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class MinionCalculator implements Initializable{
    
    public JFXButton minionPlus;
    public JFXButton minionMinus;
    public Text minionCountTxt;
    public Pane pane;
    public Text coinsPerDay;
    public Text coinsPerHour;
    public JFXComboBox minionType;
    public Text invalidMinion;
    public ScrollPane scrollPane;
    public Text coinsPerHour1;
    public Text coinsPerDay1;
    private HBox minionDetails;
    
    private int minionCount = 1;
    
    private List<VBox> textFields = new ArrayList<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        minionDetails = new HBox();
        minionDetails.setAlignment(Pos.CENTER);
        minionDetails.setSpacing(6);
        scrollPane.setContent(minionDetails);
        invalidMinion.setVisible(false);
        minionPlus.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                if(minionCount!=24){
                    minionCount++;
                    refreshTextBoxes();
                }
            }
        });
        minionMinus.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                if(minionCount!=1){
                    minionCount--;
                    refreshTextBoxes();
                }
            }
        });
        
        minionType.getItems().addAll(HypixelUtil.getInstance().getAllMinionNames());
    
        refreshTextBoxes();
    }
    
    private void refreshTextBoxes(){
        minionCountTxt.setText(minionCount + "");
        if(textFields.size()>minionCount){
            while(textFields.size()!=minionCount){
                minionDetails.getChildren().remove(textFields.remove(textFields.size()-1));
            }
        }else if(textFields.size()<minionCount){
            while(textFields.size()!=minionCount){
                VBox vBox = constructMinionData();
                minionDetails.getChildren().add(vBox);
                textFields.add(vBox);
            }
        }
    }
    
    //Code from: https://www.javatpoint.com/java-program-to-capitalize-each-word-in-string
    private String capitalizeWord(String str){
        String words[]=str.split("\\s");
        String capitalizeWord="";
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";
        }
        return capitalizeWord.trim();
    }
    
    private VBox constructMinionData(){
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        Text levelTxt = new Text("Level: ");
        JFXTextField levelTextF = new JFXTextField();
        Text fuelTxt = new Text("Fuel: ");
        JFXComboBox<String> fuel = new JFXComboBox<>();
        Text upgrade1Txt = new Text("Upgrade 1:");
        JFXComboBox<String> upgrade1 = new JFXComboBox<>();
        Text upgrade2Txt = new Text("Upgrade 2:");
        JFXComboBox<String> upgrade2 = new JFXComboBox<>();
        for(Boost value : Boost.values()){
            if(value.upgrade){
                upgrade1.getItems().add(capitalizeWord(value.name().replace("_", "").toLowerCase()));
                upgrade2.getItems().add(capitalizeWord(value.name().replace("_", "").toLowerCase()));
                continue;
            }
            fuel.getItems().add(capitalizeWord(value.name().replace("_", " ").toLowerCase()));
        }
        fuel.setValue("None");
        upgrade1.getItems().add("None");
        upgrade2.getItems().add("None");
        upgrade1.setValue("None");
        upgrade2.setValue("None");
        JFXButton applyToAllBtn = new JFXButton("Apply to all");
        applyToAllBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                JFXButton source = (JFXButton) event.getSource();
                VBox parent = (VBox) source.getParent();
                JFXTextField level = (JFXTextField) parent.getChildren().get(1);
                JFXComboBox<String> fuel = (JFXComboBox<String>) parent.getChildren().get(3);
                JFXComboBox<String> upgrade1 = (JFXComboBox<String>) parent.getChildren().get(5);
                JFXComboBox<String> upgrade2 = (JFXComboBox<String>) parent.getChildren().get(7);
                for(VBox vbox : textFields){
                    JFXTextField level1 = (JFXTextField) vbox.getChildren().get(1);
                    JFXComboBox<String> fuel1 = (JFXComboBox<String>) vbox.getChildren().get(3);
                    JFXComboBox<String> upgrade11 = (JFXComboBox<String>) vbox.getChildren().get(5);
                    JFXComboBox<String> upgrade21 = (JFXComboBox<String>) vbox.getChildren().get(7);
                    level1.setText(level.getText());
                    fuel1.setValue(fuel.getValue());
                    upgrade11.setValue(upgrade1.getValue());
                    upgrade21.setValue(upgrade2.getValue());
                }
            }
        });
        vBox.setId(UUID.randomUUID().toString());
        vBox.getChildren().addAll(levelTxt, levelTextF, fuelTxt, fuel, upgrade1Txt, upgrade1, upgrade2Txt, upgrade2, applyToAllBtn);
        return vBox;
    }
    
    public void calculate(ActionEvent actionEvent){
        invalidMinion.setVisible(false);
        HypixelUtil util = HypixelUtil.getInstance();
        String selectedMinion = (String) minionType.getValue();
        if(selectedMinion==null){
            invalidMinion.setVisible(true);
            return;
        }
        double totalCoinsPerHour = 0;
        double totalCoinsPerDay = 0;
        double totalCoinsPerHour1 = 0;
        double totalCoinsPerDay1 = 0;
        for(VBox vbox : textFields){
            String text = ((JFXTextField) vbox.getChildren().get(1)).getText();
            if(text==null){
                invalidMinion.setVisible(true);
                return;
            }
            try{
                int tier = Integer.parseInt(text);
    
                Boost fuel = Boost.valueOf(((JFXComboBox<String>) vbox.getChildren().get(3)).getValue().toUpperCase().replace(" ", "_"));
                Boost upgrade1 = Boost.valueOf(((JFXComboBox<String>) vbox.getChildren().get(5)).getValue().toUpperCase().replace(" ", "_"));
                Boost upgrade2 = Boost.valueOf(((JFXComboBox<String>) vbox.getChildren().get(7)).getValue().toUpperCase().replace(" ", "_"));
    
                double coinsPerHourMerchant = util.getCoinsPerHourMerchant(selectedMinion, tier, fuel, upgrade1, upgrade2);
                double coinsPerHourBazaar = util.getCoinsPerHourBazaar(selectedMinion, tier, fuel, upgrade1, upgrade2);
                totalCoinsPerHour+=coinsPerHourMerchant;
                totalCoinsPerDay+=coinsPerHourMerchant*24;
                totalCoinsPerHour1+=coinsPerHourBazaar;
                totalCoinsPerDay1+=coinsPerHourBazaar*24;
            }catch(NumberFormatException ignore){
                invalidMinion.setVisible(true);
                return;
            }
        }
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        coinsPerHour.setText("Coins per hour: " + df.format(totalCoinsPerHour));
        coinsPerDay.setText("Coins per day: " + df.format(totalCoinsPerDay));
        coinsPerDay1.setText("Coins per day bazaar: " + df.format(totalCoinsPerDay1));
        coinsPerHour1.setText("Coins per hour bazaar: " + df.format(totalCoinsPerHour1));
    }
}
