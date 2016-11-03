package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.DiagPanel;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.ClasspathResourceManager;
import com.mpsdevelopment.plasticine.commons.LogbackConfigureLoader;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BioApplication extends Application {

    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml", "webapp/web-context.xml");

    private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();

    private static final Logger LOGGER = LoggerUtil.getLogger(BioApplication.class);

    private DiagPanel diagPanel;

    public static void main(String[] args) {

        JettyServer server = JettyServer.getInstance();
        try {
            server.start();
            LOGGER.info("Server started");

            LogbackConfigureLoader.initializeLogging(resourceManager, "logback.xml", "jul.properties");
            launch(args);

            server.join();

        } catch (Exception e) {
            LOGGER.error("Failed to start server. = %s", e);
        }
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
        try {
            JettyServer.getInstance().stop();
            LOGGER.info("Server stopped");
        } catch (Exception e) {
            LOGGER.error("Failed to stop server. = %s", e);
        }
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
