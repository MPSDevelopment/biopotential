package com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutomaticsPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(AutomaticsPanel.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml", "webapp/web-context.xml");

	private AutomaticsPanelController controller;

    public AutomaticsPanel() {


        controller = (AutomaticsPanelController) SpringLoaderFXML.load(APP_CONTEXT,AutomaticsPanelController.class, "AutomaticsPanel.fxml");
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
