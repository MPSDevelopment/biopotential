package com.mpsdevelopment.biopotential.server.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModalWindow {
    public static void makepopup(Stage primaryStage, String message) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        VBox dialogVbox = new VBox(20);
        Text text = new Text(message);
        dialogVbox.getChildren().add(text);
        text.setTextAlignment(TextAlignment.CENTER);
        dialogVbox.setAlignment(Pos.CENTER);
        Button button = new Button("Ok");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        });
        button.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(button);
        button.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVbox, 200, 100);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
