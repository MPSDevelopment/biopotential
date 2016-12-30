package com.mpsdevelopment.biopotential.server.gui.startPanel;


import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfigPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(ConfigPanel.class);

	private ConfigPanelController configPanelController;

    public ConfigPanel() {
        FXMLLoader loader = new FXMLLoader();
//        configPanelController = (AutomaticsPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AutomaticsPanelController.class, "AutomaticsPanel.fxml");
        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("ConfigPanel.fxml"));
            configPanelController = loader.getController();
            configPanelController.setView(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = configPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public ConfigPanelController getConfigPanelController() {
        return configPanelController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        configPanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
