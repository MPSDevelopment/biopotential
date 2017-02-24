package com.mpsdevelopment.biopotential.server.gui.analysis;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class HumanPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(HumanPanel.class);

	private HumanPanelController humanPanelController;

    public HumanPanel(Map<Pattern, AnalysisSummary> diseases) {

//        humanPanelController = (AnalysisPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AnalysisPanelController.class, "AnalysisPanel.fxml");
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("HumanPanel.fxml"));
            humanPanelController = loader.getController();
            humanPanelController.setView(pane);
            humanPanelController.setDiseases(diseases);
            humanPanelController.init();


        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = humanPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        humanPanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
