package com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.DiagPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.URL;

public class SelectFromDbPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(SelectFromDbPanel.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml", "webapp/web-context.xml");

	private SelectFromDbPanelController controller;

    public SelectFromDbPanel() {


        controller = (SelectFromDbPanelController) SpringLoaderFXML.load(APP_CONTEXT,SelectFromDbPanelController.class, "SelectFromDbPanel.fxml");
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
