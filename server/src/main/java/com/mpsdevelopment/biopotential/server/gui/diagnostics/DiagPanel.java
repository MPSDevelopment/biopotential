package com.mpsdevelopment.biopotential.server.gui.diagnostics;

import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.SelectFromDbPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.URL;

public class DiagPanel extends Pane {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiagPanel.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml", "webapp/web-context.xml");


    private DiagPanelController diagPanelController;

    public DiagPanel(/*Stage primaryStage*/) {

        diagPanelController = (DiagPanelController) SpringLoaderFXML.load(APP_CONTEXT,DiagPanelController.class, "DiagPanel.fxml");
        Pane panel = diagPanelController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        diagPanelController.updatePanel(primaryStage);
        primaryStage.show();
    }
}
