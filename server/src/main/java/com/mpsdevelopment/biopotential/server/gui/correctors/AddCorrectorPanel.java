package com.mpsdevelopment.biopotential.server.gui.correctors;


import com.mpsdevelopment.biopotential.server.SpringLoaderFXML;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.AutomaticsPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AddCorrectorPanel extends Pane {

	private static final Logger LOGGER = LoggerUtil.getLogger(AddCorrectorPanel.class);

	private AddCorrectorController controller;

    public AddCorrectorPanel() {

        controller = (AddCorrectorController) SpringLoaderFXML.load(BioApplication.APP_CONTEXT,AddCorrectorController.class, "AddCorrectorPanel.fxml");
        Pane panel = controller.getView();
        getChildren().add(panel);
        panel.getStyleClass().clear();


    }


    public void setPrimaryStage(Stage primaryStage) {
        controller.updatePanel(primaryStage);
        primaryStage.show();
    }
}
