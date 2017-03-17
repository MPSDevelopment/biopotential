package com.mpsdevelopment.biopotential.server.gui.analysis.humanPanels;


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

public class FemalePanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(FemalePanel.class);

	private MalePanelController malePanelController;

    public FemalePanel(Map<Pattern, AnalysisSummary> diseases) {

//        humanPanelController = (AnalysisPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AnalysisPanelController.class, "AnalysisPanel.fxml");
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("MalePanel.fxml"));
            malePanelController = loader.getController();
            malePanelController.setView(pane);
            malePanelController.setDiseases(diseases);
            malePanelController.displaySectionsOnBody();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = malePanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();
    }

    public void setPrimaryStage(Stage primaryStage) {
        malePanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
