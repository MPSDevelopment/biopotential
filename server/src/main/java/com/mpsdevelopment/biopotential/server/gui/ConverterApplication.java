package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.biopotential.server.gui.converter.ConverterPanel;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class ConverterApplication extends Application{

    private static final Logger LOGGER = LoggerUtil.getLogger(BioApplication.class);
    public static XmlWebApplicationContext WEB_CONTEXT;
    public static final String SPRING_CONTEXT_FILENAME = "web-context.xml";

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        addMainPanel();

    }

    private void addMainPanel() {
        WEB_CONTEXT = new XmlWebApplicationContext();
        WEB_CONTEXT.setConfigLocations(SPRING_CONTEXT_FILENAME);
        WEB_CONTEXT.setParent(BioApplication.APP_CONTEXT);
        ConverterPanel converterPanel = new ConverterPanel();
        LOGGER.info(" Start Converter application");
        Stage mainPanelStage = StageUtils.createStage(null, converterPanel, new StageSettings().setPanelTitle("Конвертор базы").setClazz(converterPanel.getClass()).setHeight(260d).setWidth(370d).setHeightPanel(250d).setWidthPanel(370d)/*.setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY())*/);
        converterPanel.setPrimaryStage(mainPanelStage);

        mainPanelStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
            }
        });

    }
}
