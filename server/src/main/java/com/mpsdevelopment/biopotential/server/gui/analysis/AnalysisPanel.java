package com.mpsdevelopment.biopotential.server.gui.analysis;


import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class AnalysisPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(AnalysisPanel.class);

	private AnalysisPanelController analysisPanelController;

    public AnalysisPanel(File file, String degree) {

//        analysisPanelController = (AnalysisPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AnalysisPanelController.class, "AnalysisPanel.fxml");
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("AnalysisPanel.fxml"));
            analysisPanelController = loader.getController();
            analysisPanelController.setView(pane);
            analysisPanelController.setDegree(degree);
            analysisPanelController.makeCurrentAnalyze(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = analysisPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        analysisPanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
