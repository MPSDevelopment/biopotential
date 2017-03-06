package com.mpsdevelopment.biopotential.server.gui.analysis;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MyPreloader extends Preloader {
    private static Stage preloaderStage;
    private static ProgressBar progressBar;
    private static Label label;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;
        ImageView splash = new ImageView(new Image("splash.png"));
        progressBar = new ProgressBar();
        label = new Label("Start");
        VBox loading = new VBox(20);
        loading.setMaxWidth(Region.USE_PREF_SIZE);
        loading.setMaxHeight(Region.USE_PREF_SIZE);
        loading.getChildren().add(splash);
        loading.getChildren().add(progressBar);
        loading.getChildren().add(label);
        BorderPane root = new BorderPane(loading);
        Scene scene = new Scene(root);

        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == Type.BEFORE_START) {
//            preloaderStage.hide();
        }
    }

    public static void close() {
        preloaderStage.hide();
    }

}