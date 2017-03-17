package com.mpsdevelopment.biopotential.server.gui.analysis.humanPanels;


import com.mpsdevelopment.biopotential.server.db.pojo.HumanPoints;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DiseasePanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(DiseasePanel.class);

	private DiseasePanelController diseasePanelController;

    public DiseasePanel(List<HumanPoints> points) {

//        diseasePanelController = (AnalysisPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AnalysisPanelController.class, "AnalysisPanel.fxml");
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("DiseasePanel.fxml"));
            diseasePanelController = loader.getController();
            diseasePanelController.setView(pane);
            diseasePanelController.setList(points);
            diseasePanelController.init();


        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = diseasePanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        diseasePanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
