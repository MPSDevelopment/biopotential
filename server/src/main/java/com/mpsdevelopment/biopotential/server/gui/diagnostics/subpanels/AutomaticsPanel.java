package com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;

public class AutomaticsPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(AutomaticsPanel.class);

	private AutomaticsPanelController controller;

    public AutomaticsPanel(File file) {
        FXMLLoader loader = new FXMLLoader();
//        controller = (AutomaticsPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AutomaticsPanelController.class, "AutomaticsPanel.fxml");
        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("AutomaticsPanel.fxml"));
            controller = loader.getController();
            controller.setView(pane);
            controller.setFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = controller.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public AutomaticsPanelController getController() {
        return controller;
    }

    public void setPrimaryStage(Stage primaryStage) {
        controller.updatePanel(primaryStage);
        primaryStage.show();
    }
}
