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
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Map;

public class CorrectorsPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(CorrectorsPanel.class);

	private CorrectorsPanelController controller;

    public CorrectorsPanel(Map<Pattern, AnalysisSummary> allHealings) {
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("CorrectorsPanel.fxml"));
            controller = loader.getController();
            controller.setView(pane);
            controller.setHealingsMap(allHealings);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        controller = (CorrectorsPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,CorrectorsPanelController.class, "CorrectorsPanel.fxml");
        Pane panel = controller.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        controller.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
