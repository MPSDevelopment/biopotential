package com.mpsdevelopment.biopotential.server.gui.analysis.barChart;


import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class BarChartPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(BarChartPanel.class);

	private BarChartPanelController barChartPanelController;

    public BarChartPanel(Map<String,Double> mapMax, Map<String,Double> mapPo) {

//        barChartPanelController = (AnalysisPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AnalysisPanelController.class, "AnalysisPanel.fxml");
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("BarChartPanel.fxml"));
            barChartPanelController = loader.getController();
            barChartPanelController.setView(pane);
            barChartPanelController.setMapMax(mapMax);
            barChartPanelController.setMapPo(mapPo);
            barChartPanelController.displayBar();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = barChartPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        barChartPanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
