package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.biopotential.server.gui.converter.ConverterPanel;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.ClasspathResourceManager;
import com.mpsdevelopment.plasticine.commons.LogbackConfigureLoader;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Optional;

public class ConverterApplication extends Application{

    private static final Logger LOGGER = LoggerUtil.getLogger(BioApplication.class);
    private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/convert-context.xml");

    public static void main(String[] args) {
		LogbackConfigureLoader.initializeLogging(resourceManager, "logback_converter.xml", "jul.properties");
		System.setErr(LoggerUtil.getRedirectedToLoggerErrPrintStream(System.err));
		System.setOut(LoggerUtil.getRedirectedToLoggerOutPrintStream(System.out));
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        addMainPanel();
    }

    @Override
    public void stop() {

        System.exit(0);
    }

    private void addMainPanel() {
        ConverterPanel converterPanel = new ConverterPanel();
        LOGGER.info(" Start Converter application");
        Stage mainPanelStage = StageUtils.createStage(null, converterPanel, new StageSettings().setPanelTitle("Конвертор базы").setClazz(ConverterPanel.class).setHeight(260d).setWidth(370d).setHeightPanel(250d).setWidthPanel(370d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
        converterPanel.setPrimaryStage(mainPanelStage);

        mainPanelStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                // consume event
                event.consume();

                // show close dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Close Confirmation");
                alert.setHeaderText("Вы действительно хотите закрыть приложение?");
                alert.initOwner(mainPanelStage);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    Platform.exit();
                }
            }
        });

    }
}
