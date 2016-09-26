package com.mpsdevelopment.biopotential.server.gui.analysis;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.AutomaticsPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AnalysisPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(AnalysisPanel.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml", "webapp/web-context.xml");

	private AnalysisPanelController controller;

    public AnalysisPanel() {


        controller = (AnalysisPanelController) SpringLoaderFXML.load(APP_CONTEXT,AnalysisPanelController.class, "AnalysisPanel.fxml");
        Pane panel = controller.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();
//        controller.subscribe();
//        controller.setScenario(scenario);


    }


    public void setPrimaryStage(Stage primaryStage) {
        controller.updatePanel(primaryStage);
        primaryStage.show();
    }
}
