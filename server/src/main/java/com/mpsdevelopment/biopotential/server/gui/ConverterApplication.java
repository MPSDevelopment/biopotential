package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.gui.converter.ConverterPanel;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class ConverterApplication extends Application{

    private static final Logger LOGGER = LoggerUtil.getLogger(BioApplication.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/web-context.xml");

    public static void main(String[] args) {

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
        Stage mainPanelStage = StageUtils.createStage(null, converterPanel, new StageSettings().setPanelTitle("Конвертор базы").setClazz(converterPanel.getClass()).setHeight(260d).setWidth(370d).setHeightPanel(250d).setWidthPanel(370d)/*.setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY())*/);
        converterPanel.setPrimaryStage(mainPanelStage);

        mainPanelStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
            }
        });

    }
}
