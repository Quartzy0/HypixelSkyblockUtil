package com.quartzy.bestBazaarMinionTab;

import com.quartzy.HypixelUtil;
import com.quartzy.bestMinionTab.MinionData;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.hypixel.api.reply.skyblock.BazaarReply;

import java.net.URL;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ResourceBundle;

public class BestBazaarMinionTab implements Initializable{
    public TableColumn coinsColumn;
    public TableColumn minionColumn;
    public TableView table;
    public TableColumn coinsDayColumn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        PriorityQueue<MinionData> queue = new PriorityQueue<>();
        String[] allMinionNames = HypixelUtil.getInstance().getAllMinionNames();
        for(int i = 0; i < allMinionNames.length; i++){
            double coinsPerHourMerchant = HypixelUtil.getInstance().getCoinsPerHourBazaar(allMinionNames[i], 11);
            queue.add(new MinionData(coinsPerHourMerchant, allMinionNames[i]));
        }
        MinionData[] minionData = queue.toArray(new MinionData[queue.size()]);
        coinsColumn.setCellValueFactory(new PropertyValueFactory<String, MinionData>("displayCPH"));
        coinsDayColumn.setCellValueFactory(new PropertyValueFactory<String, MinionData>("displayCPD"));
        minionColumn.setCellValueFactory(new PropertyValueFactory<String, MinionData>("name"));
        table.getItems().addAll(minionData);
    }
}
