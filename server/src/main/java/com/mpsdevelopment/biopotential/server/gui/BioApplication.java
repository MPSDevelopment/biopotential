package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.gui.analysis.MyPreloader;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.DiagPanel;
import com.mpsdevelopment.biopotential.server.gui.startPanel.StartPanel;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;

import com.mpsdevelopment.helicopter.license.LicenseConstants;
import com.mpsdevelopment.helicopter.license.LicenseCreator;
import com.mpsdevelopment.helicopter.license.LicenseDialogUtils;
import com.mpsdevelopment.plasticine.commons.ClasspathResourceManager;
import com.mpsdevelopment.plasticine.commons.LogbackConfigureLoader;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BioApplication extends Application {

    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml");

    private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();

    private static final Logger LOGGER = LoggerUtil.getLogger(BioApplication.class);

    private static final LicenseCreator LICENSE_CREATOR = new LicenseCreator();

    public static void main(String[] args) {

        JettyServer server = JettyServer.getInstance();
        try {
            server.start();
            LOGGER.info("Server started");
            /*if (!LICENSE_CREATOR.checkLicense(LicenseConstants.RELEASE_DATE)) {
                LicenseDialogUtils.showLicenseException();
                System.exit(0);
            }*/

            LauncherImpl.launchApplication(BioApplication.class, MyPreloader.class, args);

            LogbackConfigureLoader.initializeLogging(resourceManager, "logback.xml", "jul.properties");
            System.setErr(LoggerUtil.getRedirectedToLoggerErrPrintStream(System.err));
            System.setOut(LoggerUtil.getRedirectedToLoggerOutPrintStream(System.out));

            launch(args);
            server.join();

        } catch (Exception e) {
            LOGGER.error("Failed to start server. = %s", e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        addMainPanel();
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
        /*StartPanel startPanel =  new StartPanel();
        LOGGER.info("Start panel");
        Stage mainPanelStage = StageUtils.createStage(null, startPanel, new StageSettings().setClazz(DiagPanel.class).setHeight(265d).setWidth(400d).setHeightPanel(265d).setWidthPanel(400d)
                .setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
        startPanel.setPrimaryStage(mainPanelStage);*/

        DiagPanel diagPanel = new DiagPanel();
        LOGGER.info(" Start Diag panel");
        Stage mainPanelStage = StageUtils.createStage(null, diagPanel, new StageSettings().setClazz(DiagPanel.class).setHeight(740d).setWidth(1034d).setHeightPanel(727d).setWidthPanel(1034d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
        diagPanel.setPrimaryStage(mainPanelStage);
        MyPreloader.close();

        mainPanelStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
                stop();
            }
        });

    }
}
