package com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels;


import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class AutomaticsPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(AutomaticsPanel.class);

	private AutomaticsPanelController automaticsPanelController;

    public AutomaticsPanel(File file, String gender) {
        FXMLLoader loader = new FXMLLoader();
//        automaticsPanelController = (AutomaticsPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AutomaticsPanelController.class, "AutomaticsPanel.fxml");
        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("AutomaticsPanel.fxml"));
            automaticsPanelController = loader.getController();
            automaticsPanelController.setView(pane);
            automaticsPanelController.setFile(file);
            automaticsPanelController.setGender(gender);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = automaticsPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public AutomaticsPanelController getAutomaticsPanelController() {
        return automaticsPanelController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        automaticsPanelController.updatePanel(primaryStage);
        primaryStage.show();
    }
}
