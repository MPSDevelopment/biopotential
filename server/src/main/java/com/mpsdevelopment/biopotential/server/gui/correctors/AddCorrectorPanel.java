package com.mpsdevelopment.biopotential.server.gui.correctors;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.AutomaticsPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class AddCorrectorPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(AddCorrectorPanel.class);

	private AddCorrectorController addCorrectorController;

    public AddCorrectorPanel() {

//        addCorrectorController = (AddCorrectorController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AddCorrectorController.class, "AddCorrectorPanel.fxml");
        FXMLLoader loader = new FXMLLoader();

        try {
            Pane pane = loader.load(this.getClass().getResourceAsStream("AddCorrectorPanel.fxml"));
            addCorrectorController = loader.getController();
            addCorrectorController.setView(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Pane panel = addCorrectorController.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();

    }

    public void setPrimaryStage(Stage primaryStage) {
        addCorrectorController.updatePanel(primaryStage);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }
}
