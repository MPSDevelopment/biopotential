package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class BioPanel extends Group {

    private static final Logger LOGGER = LoggerUtil.getLogger(BioPanel.class);

    public BioPanel(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL resource = getClass().getResource("BioPanel.fxml");
        if(resource == null)
            LOGGER.info("Cant find *.fxml");
        Pane panel;
        primaryStage.setResizable(false);

        try {
            panel = (Pane) fxmlLoader.load(resource.openStream());
            BioPanelController controller = fxmlLoader.getController();
            getChildren().add(panel);

        } catch (IOException e) {
            LOGGER.debug("Cannot load *.fxml Reason: %s", e.getMessage());
            LOGGER.printStackTrace(e);
        }

    }
}
