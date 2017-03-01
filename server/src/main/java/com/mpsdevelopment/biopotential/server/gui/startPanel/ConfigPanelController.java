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
    private TextField editField3;

    @FXML
    private Label label3;

    @FXML
    private TextField lT3;

    @FXML
    private TextField threshold3;

    @FXML
    private TextField editField4;

    @FXML
    private Label label4;

    @FXML
    private TextField lT4;

    @FXML
    private TextField threshold4;

    @FXML
    private TextField editField5;

    @FXML
    private Label label5;

    @FXML
    private TextField lT5;

    @FXML
    private TextField threshold5;

    @FXML
    private TextField editField6;

    @FXML
    private Label label6;

    @FXML
    private TextField lT6;

    @FXML
    private TextField threshold6;

    @FXML
    private TextField editField7;

    @FXML
    private Label label7;

    @FXML
    private TextField lT7;

    @FXML
    private TextField threshold7;

    @FXML
    private TextField editField8;

    @FXML
    private Label label8;

    @FXML
    private TextField lT8;

    @FXML
    private TextField threshold8;

    @FXML
    private TextField editField9;

    @FXML
    private Label label9;

    @FXML
    private TextField lT9;

    @FXML
    private TextField threshold9;

    @FXML
    private TextField editField10;

    @FXML
    private Label label10;

    @FXML
    private TextField lT10;

    @FXML
    private TextField threshold10;

    @FXML
    private TextField editField11;

    @FXML
    private Label label11;

    @FXML
    private TextField lT11;

    @FXML
    private TextField threshold11;

    @FXML
    private TextField editField12;

    @FXML
    private Label label12;

    @FXML
    private TextField lT12;

    @FXML
    private TextField threshold12;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    @FXML
    private Button saveButton;

    private Stage primaryStage;
    private ConfigSettings configSettings;

    public ConfigPanelController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        configSettings = JettyServer.WEB_CONTEXT.getBean(ConfigSettings.class);
        label1.setText(configSettings.getSystemName1());
        label2.setText(configSettings.getSystemName2());
        label3.setText(configSettings.getSystemName3());
        label4.setText(configSettings.getSystemName4());
        label5.setText(configSettings.getSystemName5());
        label6.setText(configSettings.getSystemName6());
        label7.setText(configSettings.getSystemName7());
        label8.setText(configSettings.getSystemName8());
        label9.setText(configSettings.getSystemName9());
        label10.setText(configSettings.getSystemName10());
        label11.setText(configSettings.getSystemName11());
        label12.setText(configSettings.getSystemName12());

        lT1.setText(configSettings.getLiteral1());
        lT2.setText(configSettings.getLiteral2());
        lT3.setText(configSettings.getLiteral3());
        lT4.setText(configSettings.getLiteral4());
        lT5.setText(configSettings.getLiteral5());
        lT6.setText(configSettings.getLiteral6());
        lT7.setText(configSettings.getLiteral7());
        lT8.setText(configSettings.getLiteral8());
        lT9.setText(configSettings.getLiteral9());
        lT10.setText(configSettings.getLiteral10());
        lT11.setText(configSettings.getLiteral11());
        lT12.setText(configSettings.getLiteral12());


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

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!editField1.getText().trim().equals("")) {
                    label1.setText(editField1.getText());
                    configSettings.setSystemName1(editField1.getText());
                }
                if (!lT1.getText().trim().equals("")) {
                    configSettings.setLiteral1(lT1.getText());
                }
                if (!threshold1.getText().trim().equals("")) {
                    configSettings.setThreshold1(threshold1.getText());
                }

                if (!editField2.getText().trim().equals("")) {
                    label2.setText(editField2.getText());
                    configSettings.setSystemName2(editField2.getText());
                }
                if (!lT2.getText().trim().equals("")) {
                    configSettings.setLiteral2(lT2.getText());
                }
                if (!threshold2.getText().trim().equals("")) {
                    configSettings.setThreshold2(threshold2.getText());
                }

                if (!editField3.getText().trim().equals("")) {
                    label3.setText(editField3.getText());
                    configSettings.setSystemName3(editField3.getText());
                }
                if (!lT3.getText().trim().equals("")) {
                    configSettings.setLiteral3(lT3.getText());
                }
                if (!threshold3.getText().trim().equals("")) {
                    configSettings.setThreshold3(threshold3.getText());
                }

                if (!editField4.getText().trim().equals("")) {
                    label4.setText(editField4.getText());
                    configSettings.setSystemName4(editField4.getText());
                }
                if (!lT4.getText().trim().equals("")) {
                    configSettings.setLiteral4(lT4.getText());
                }
                if (!threshold4.getText().trim().equals("")) {
                    configSettings.setThreshold4(threshold4.getText());
                }

                if (!editField5.getText().trim().equals("")) {
                    label5.setText(editField5.getText());
                    configSettings.setSystemName5(editField5.getText());
                }
                if (!lT5.getText().trim().equals("")) {
                    configSettings.setLiteral5(lT5.getText());
                }
                if (!threshold5.getText().trim().equals("")) {
                    configSettings.setThreshold5(threshold5.getText());
                }

                if (!editField6.getText().trim().equals("")) {
                    label6.setText(editField6.getText());
                    configSettings.setSystemName6(editField6.getText());
                }
                if (!lT6.getText().trim().equals("")) {
                    configSettings.setLiteral6(lT6.getText());
                }
                if (!threshold6.getText().trim().equals("")) {
                    configSettings.setThreshold6(threshold6.getText());
                }

                if (!editField7.getText().trim().equals("")) {
                    label7.setText(editField7.getText());
                    configSettings.setSystemName7(editField7.getText());
                }
                if (!lT7.getText().trim().equals("")) {
                    configSettings.setLiteral7(lT7.getText());
                }
                if (!threshold7.getText().trim().equals("")) {
                    configSettings.setThreshold7(threshold7.getText());
                }

                if (!editField8.getText().trim().equals("")) {
                    label8.setText(editField8.getText());
                    configSettings.setSystemName8(editField8.getText());
                }
                if (!lT8.getText().trim().equals("")) {
                    configSettings.setLiteral8(lT8.getText());
                }
                if (!threshold8.getText().trim().equals("")) {
                    configSettings.setThreshold8(threshold8.getText());
                }

                if (!editField9.getText().trim().equals("")) {
                    label9.setText(editField9.getText());
                    configSettings.setSystemName9(editField9.getText());
                }
                if (!lT9.getText().trim().equals("")) {
                    configSettings.setLiteral9(lT9.getText());
                }
                if (!threshold9.getText().trim().equals("")) {
                    configSettings.setThreshold9(threshold9.getText());
                }

                if (!editField10.getText().trim().equals("")) {
                    label10.setText(editField10.getText());
                    configSettings.setSystemName10(editField10.getText());
                }
                if (!lT10.getText().trim().equals("")) {
                    configSettings.setLiteral10(lT10.getText());
                }
                if (!threshold10.getText().trim().equals("")) {
                    configSettings.setThreshold10(threshold10.getText());
                }

                if (!editField11.getText().trim().equals("")) {
                    label11.setText(editField11.getText());
                    configSettings.setSystemName11(editField11.getText());
                }
                if (!lT11.getText().trim().equals("")) {
                    configSettings.setLiteral11(lT11.getText());
                }
                if (!threshold11.getText().trim().equals("")) {
                    configSettings.setThreshold11(threshold11.getText());
                }

                if (!editField12.getText().trim().equals("")) {
                    label12.setText(editField12.getText());
                    configSettings.setSystemName12(editField12.getText());
                }
                if (!lT12.getText().trim().equals("")) {
                    configSettings.setLiteral12(lT12.getText());
                }
                if (!threshold12.getText().trim().equals("")) {
                    configSettings.setThreshold12(threshold12.getText());
                }

                String json = JsonUtils.getJson(configSettings);
                JsonUtils.writeJsonToFile(json,"config/config.json");

                clearEditFields();
            }
        });

    }

    private void clearEditFields() {
        editField1.setText("");
        editField2.setText("");
        editField3.setText("");
        editField4.setText("");
        editField5.setText("");
        editField6.setText("");
        editField7.setText("");
        editField8.setText("");
        editField9.setText("");
        editField10.setText("");
        editField11.setText("");
        editField12.setText("");
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
