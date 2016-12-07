package com.mpsdevelopment.biopotential.server.gui.converter;


import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.AutomaticsPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ConverterPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(ConverterPanel.class);

	private ConverterPanelController converterPanelController;

    public ConverterPanel() {
        FXMLLoader loader = new FXMLLoader();
//        automaticsPanelController = (AutomaticsPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AutomaticsPanelController.class, "AutomaticsPanel.fxml");
        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("ConverterPanel.fxml"));
            converterPanelController = loader.getController();
            converterPanelController.setView(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = converterPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public ConverterPanelController getConverterPanelController() {
        return converterPanelController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        converterPanelController.updatePanel(primaryStage);
        primaryStage.show();
    }
}
