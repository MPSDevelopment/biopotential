package com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
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

	private SelectFromDbPanelController controller;

    public SelectFromDbPanel() {

//        controller = (SelectFromDbPanelController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,SelectFromDbPanelController.class, "SelectFromDbPanel.fxml");
        FXMLLoader loader = new FXMLLoader();
        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("SelectFromDbPanel.fxml"));
            controller = loader.getController();
            controller.setView(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pane panel = controller.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }


    public void setPrimaryStage(Stage primaryStage) {
        controller.updatePanel(primaryStage);
        primaryStage.show();
    }
}
