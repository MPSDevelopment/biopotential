package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.plasticine.commons.ClasspathResourceManager;
import com.mpsdevelopment.plasticine.commons.LogbackConfigureLoader;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by AurusD on 28-Jul-16.
 */
public class BioApplication extends Application {

    private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();

    private static final Logger LOGGER = LoggerUtil.getLogger(BioApplication.class);

    public static void main(String[] args) {
        LogbackConfigureLoader.initializeLogging(resourceManager, "logback.xml", "jul.properties");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Ноев Ковчег");
        Scene scene = new Scene(new BioPanel(stage));
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}
