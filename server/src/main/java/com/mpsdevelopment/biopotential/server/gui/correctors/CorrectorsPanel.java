package com.mpsdevelopment.biopotential.server.gui.correctors;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.gui.analysis.AnalysisPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CorrectorsPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(CorrectorsPanel.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml", "webapp/web-context.xml");

	private CorrectorsPanelController controller;

    public CorrectorsPanel() {


        controller = (CorrectorsPanelController) SpringLoaderFXML.load(APP_CONTEXT,CorrectorsPanelController.class, "CorrectorsPanel.fxml");
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
