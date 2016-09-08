package com.mpsdevelopment.biopotential.server.gui;

import com.mps.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.gui.authentication.AuthPanel;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.DiagPanel;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.DiagPanelController;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.ClasspathResourceManager;
import com.mpsdevelopment.plasticine.commons.LogbackConfigureLoader;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Bean;

public class BioApplication extends Application {

    private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();

    private static final Logger LOGGER = LoggerUtil.getLogger(BioApplication.class);

    private DiagPanel diagPanel;

    public static void main(String[] args) {
        LogbackConfigureLoader.initializeLogging(resourceManager, "logback.xml", "jul.properties");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Ноев Ковчег");
//        Scene scene = new Scene(new DiagPanel(stage));
//        Scene scene = new Scene(addMainPanel());
        addMainPanel();
        /*stage.setScene(scene);
        stage.show();*/
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    private void addMainPanel() {
        diagPanel = new DiagPanel();
        LOGGER.info(" Start Diag panel");
        Stage mainPanelStage = StageUtils.createStage(null, diagPanel, new StageSettings().setClazz(DiagPanel.class).setHeight(740d).setWidth(1034d).setHeightPanel(727d).setWidthPanel(1034d));
        diagPanel.setPrimaryStage(mainPanelStage);

        mainPanelStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
                stop();
            }
        });

    }
}
