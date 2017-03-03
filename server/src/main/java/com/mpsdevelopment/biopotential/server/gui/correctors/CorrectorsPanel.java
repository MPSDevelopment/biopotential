package com.mpsdevelopment.biopotential.server.gui.correctors;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.gui.analysis.AnalysisPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class CorrectorsPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(CorrectorsPanel.class);

	private CorrectorsPanelController correctorsPanelController;

    public CorrectorsPanel(Map<Pattern, AnalysisSummary> allHealings) {
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("CorrectorsPanel.fxml"));
//            correctorsPanelController = BioApplication.APP_CONTEXT.getBean(CorrectorsPanelController.class);
//            correctorsPanelController = loader.getController();
            correctorsPanelController = (CorrectorsPanelController) SpringLoaderFXML.load(CorrectorsPanelController.class,"CorrectorsPanel.fxml");

//            correctorsPanelController.setView(pane);
//            correctorsPanelController.setHealingsMap(allHealings);
//            correctorsPanelController.getPattersFromHealingsMap();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Pane panel = correctorsPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        correctorsPanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
