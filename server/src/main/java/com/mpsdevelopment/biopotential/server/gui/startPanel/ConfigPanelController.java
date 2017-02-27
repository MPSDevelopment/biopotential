package com.mpsdevelopment.biopotential.server.gui.startPanel;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.DiagPanel;
import com.mpsdevelopment.biopotential.server.settings.ConfigSettings;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(ConfigPanelController.class);

    @FXML
    private TextField editField1;

    @FXML
    private Label label1;

    @FXML
    private TextField lT1;

    @FXML
    private TextField threshold1;

    @FXML
    private TextField editField2;

    @FXML
    private Label label2;

    @FXML
    private TextField lT2;

    @FXML
    private TextField threshold2;

    @FXML
    private TextField deTextField;

    @FXML
    private TextField enTextField;

    @FXML
    private TextField gaTextField;

    @FXML
    private TextField imTextField;

    @FXML
    private TextField neTextField;

    @FXML
    private TextField orTextField;

    @FXML
    private TextField spTextField;

    @FXML
    private TextField stTextField;

    @FXML
    private TextField urTextField;

    @FXML
    private TextField viTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    @FXML
    private Button saveButton1;

    @FXML
    private Button saveButton2;


    private Stage primaryStage;
    private ConfigSettings configSettings;

    public ConfigPanelController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        configSettings = JettyServer.WEB_CONTEXT.getBean(ConfigSettings.class);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();

            }
        });

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });

        saveButton1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String system1 = editField1.getText();
                if (system1 != null) {
                    label1.setText(system1);
                }
                String system2 = editField2.getText();
                if (system2 == "") {
                    label2.setText(system2);
                }

                configSettings.setSystemName1(system1);
                configSettings.setLiteral1(lT1.getText());
                configSettings.setThreshold1(threshold1.getText());
                configSettings.setSystemName2(system2);
                configSettings.setLiteral2(lT2.getText());
                configSettings.setThreshold2(threshold2.getText());
                String json = JsonUtils.getJson(configSettings);
                JsonUtils.writeJsonToFile(json,"config/config.json");
            }
        });
    }



    public void updatePanel(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                close();
            }
        });
    }

    public void close() {
        LOGGER.info("  CLOSE  REQUEST");

        EventBus.unsubscribe(this);

        primaryStage.close();
    }

}
