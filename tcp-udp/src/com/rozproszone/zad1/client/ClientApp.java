package com.rozproszone.zad1.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("clientAppWindow.fxml"));
        Parent root = loader.load();
        ClientAppController controller = loader.getController();

        Scene scene = new Scene(root, 300, 275);
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                controller.exit();
            }
        });

        stage.show();
    }
}
