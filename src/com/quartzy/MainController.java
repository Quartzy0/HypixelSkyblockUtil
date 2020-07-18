package com.quartzy;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable{
    
    public Tab tab1;
    public Tab tab2;
    public Tab tab3;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        new HypixelUtil(HypixelUtil.key);
        try{
            Pane pane = FXMLLoader.load(getClass().getResource("/com/quartzy/bestMinionTab/bestMinionTab.fxml"));
            tab1.setContent(pane);
        } catch(IOException e){
            e.printStackTrace();
        }
        try{
            Pane pane = FXMLLoader.load(getClass().getResource("/com/quartzy/bestBazaarMinionTab/bestBazaarMinionTab.fxml"));
            tab2.setContent(pane);
        } catch(IOException e){
            e.printStackTrace();
        }
        try{
            Pane pane = FXMLLoader.load(getClass().getResource("/com/quartzy/minionCalculator/minionCalculator.fxml"));
            tab3.setContent(pane);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
