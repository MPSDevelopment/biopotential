package com.mpsdevelopment.biopotential.server.gui.diagnostics;

import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DiagPanel extends Pane {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiagPanel.class);

    private DiagPanelController diagPanelController;

    public DiagPanel() {
        FXMLLoader loader = new FXMLLoader();
        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("DiagPanel.fxml"));
            diagPanelController = BioApplication.APP_CONTEXT.getBean(DiagPanelController.class);
//            diagPanelController = loader.getController();
            diagPanelController.setView(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = diagPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        diagPanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();

    }
}
