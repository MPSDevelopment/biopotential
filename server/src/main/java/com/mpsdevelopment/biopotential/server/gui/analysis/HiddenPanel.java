package com.mpsdevelopment.biopotential.server.gui.analysis;


import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HiddenPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(HiddenPanel.class);

	private HiddenPanelController hiddenPanelController;

    public HiddenPanel(ObservableList<DataTable> data) {

//        hiddenPanelController = (AnalysisPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AnalysisPanelController.class, "AnalysisPanel.fxml");
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("HiddenPanel.fxml"));
            hiddenPanelController = loader.getController();
            hiddenPanelController.setView(pane);
            /*hiddenPanelController.setDegree1(degree1);
            hiddenPanelController.setDegree2(degree2);*/
            hiddenPanelController.makeCurrentAnalyze(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = hiddenPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        hiddenPanelController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
