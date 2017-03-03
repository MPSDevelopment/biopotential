package com.mpsdevelopment.biopotential.server.gui.analysis;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
public class AnalysisPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(AnalysisPanel.class);

	private AnalysisPanelController analysisPanelController;

    public AnalysisPanel(File file, String degree1,String degree2, String gender) throws IOException {

//        analysisPanelController = BioApplication.APP_CONTEXT.getBean(AnalysisPanelController.class);
        analysisPanelController = (AnalysisPanelController) SpringLoaderFXML.load(AnalysisPanelController.class,"AnalysisPanel.fxml");

//        Pane pane = loader.load(this.getClass().getResourceAsStream("AnalysisPanel.fxml"));
//        analysisPanelController = loader.getController();
//        analysisPanelController.setView(pane);
        analysisPanelController.setDegree1(degree1);
        analysisPanelController.setDegree2(degree2);
        analysisPanelController.setGender(gender);
        analysisPanelController.makeCurrentAnalyze(file);

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
