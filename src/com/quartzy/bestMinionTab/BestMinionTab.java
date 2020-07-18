package com.quartzy.bestMinionTab;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.quartzy.HypixelUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ResourceBundle;

public class BestMinionTab implements Initializable{
    
    public TableColumn coinsColumn;
    public TableColumn minionColumn;
    public TableView table;
    public TableColumn coinsDayColumn;
    public Pane pane;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        PriorityQueue<MinionData> queue = new PriorityQueue<>();
        String[] allMinionNames = HypixelUtil.getInstance().getAllMinionNames();
        for(int i = 0; i < allMinionNames.length; i++){
            double coinsPerHourMerchant = HypixelUtil.getInstance().getCoinsPerHourMerchant(allMinionNames[i], 11);
            queue.add(new MinionData(coinsPerHourMerchant, allMinionNames[i]));
        }
        MinionData[] minionData = queue.toArray(new MinionData[queue.size()]);
        coinsColumn.setCellValueFactory(new PropertyValueFactory<String, MinionData>("displayCPH"));
        coinsDayColumn.setCellValueFactory(new PropertyValueFactory<String, MinionData>("displayCPD"));
        minionColumn.setCellValueFactory(new PropertyValueFactory<String, MinionData>("name"));
        table.getItems().addAll(minionData);
        
        
    }
    
    
}
