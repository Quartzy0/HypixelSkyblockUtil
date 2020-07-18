package com.quartzy;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.hypixel.api.HypixelAPI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RequestKey implements Initializable{
    public JFXTextField apiKeyTextBox;
    public Hyperlink hypixelApiLink;
    public JFXButton okBtn;
    public Text error;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        error.setVisible(false);
        hypixelApiLink.setOnAction(event -> {
            if(Desktop.isDesktopSupported()){
                hypixelApiLink.setVisited(true);
                try{
                    Desktop.getDesktop().browse(new URL("https://github.com/HypixelDev/PublicAPI").toURI());
                } catch(IOException | URISyntaxException e){
                    e.printStackTrace();
                }
            }
        });
        okBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                try{
                    UUID apiToken = UUID.fromString(apiKeyTextBox.getText());
                    JsonParser parser = new JsonParser();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("https://api.hypixel.net/key?key=" + apiToken.toString()).build();
                    try(Response response = client.newCall(request).execute()){
                        JsonObject asJsonObject = parser.parse(response.body().string()).getAsJsonObject();
                        if(asJsonObject.getAsJsonPrimitive("success").getAsBoolean()){
                            File configFile = new File(System.getProperty("user.home") + File.separator + ".skyblockutils");
                            if(!configFile.exists()){
                                try{
                                    configFile.createNewFile();
                                } catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                            Properties properties = new Properties();
                            try{
                                properties.load(new FileInputStream(configFile));
                            } catch(IOException e){
                                e.printStackTrace();
                            }
                            properties.setProperty("apiKey", apiKeyTextBox.getText());
                            try{
                                properties.store(new FileOutputStream(configFile), "");
                            } catch(IOException e){
                                e.printStackTrace();
                            }
                            HypixelUtil.key = UUID.fromString(apiKeyTextBox.getText());
                            openMainScene();
                            Stage stage = (Stage) hypixelApiLink.getScene().getWindow();
                            stage.close();
                        }
                    }
                }catch(IllegalArgumentException | IOException ignored){
                    error.setVisible(true);
                }
            }
        });
        
        validateKey(false);
    }
    
    private void validateKey(boolean err){
        File configFile = new File(System.getProperty("user.home") + File.separator + ".skyblockutils");
        if(!configFile.exists()){
            try{
                configFile.createNewFile();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        Properties properties = new Properties();
        try{
            properties.load(new FileInputStream(configFile));
        } catch(IOException e){
            e.printStackTrace();
        }
        String apiKey = properties.getProperty("apiKey");
        if(apiKey!=null){
            try{
                UUID apiToken = UUID.fromString(apiKey);
                JsonParser parser = new JsonParser();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.hypixel.net/key?key=" + apiToken.toString()).build();
                try (Response response = client.newCall(request).execute()) {
                    JsonObject asJsonObject = parser.parse(response.body().string()).getAsJsonObject();
                    if(asJsonObject.getAsJsonPrimitive("success").getAsBoolean()){
                        HypixelUtil.key = apiToken;
                        openMainScene();
                        new Thread(){
                            @Override
                            public void run(){
                                try{
                                    Thread.sleep(100);
                                } catch(InterruptedException e){
                                    e.printStackTrace();
                                }
                                Platform.runLater(new Runnable(){
                                    @Override
                                    public void run(){
                                        Stage stage = (Stage) hypixelApiLink.getScene().getWindow();
                                        stage.close();
                                    }
                                });
                            }
                        }.start();
                    }
                }
            }catch(IllegalArgumentException | IOException ignored){
                if(err)error.setVisible(true);
            }
        }else {
            if(err)error.setVisible(true);
        }
    }
    
    private void openMainScene(){
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/com/quartzy/mainScene.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(scene, 1000, 900));
            stage.setTitle("Skyblock utils");
            stage.setOnHiding(new EventHandler<WindowEvent>(){
                @Override
                public void handle(WindowEvent event){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(HypixelUtil.getInstance()!=null && HypixelUtil.getInstance().getAPI()!=null){
                                HypixelUtil.getInstance().getAPI().shutdown();
                            }
                            System.exit(0);
                        }
                    });
                }
            });
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
