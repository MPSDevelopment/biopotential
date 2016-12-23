package com.mpsdevelopment.biopotential.server.gui.startPanel;


import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.AutomaticsPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class StartPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(StartPanel.class);

	private StartPanelController startPanelController;

    public StartPanel() {
        FXMLLoader loader = new FXMLLoader();
//        startPanelController = (AutomaticsPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AutomaticsPanelController.class, "AutomaticsPanel.fxml");
        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("StartPanel.fxml"));
            startPanelController = loader.getController();
            startPanelController.setView(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = startPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public StartPanelController getStartPanelController() {
        return startPanelController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        startPanelController.updatePanel(primaryStage);
        primaryStage.show();
    }
}
