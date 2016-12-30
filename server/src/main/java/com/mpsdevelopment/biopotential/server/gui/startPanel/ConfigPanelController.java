package com.mpsdevelopment.biopotential.server.gui.startPanel;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.DiagPanel;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(ConfigPanelController.class);

    @FXML
    private TextField alTextField;

    @FXML
    private TextField caTextField;

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


    private Stage primaryStage;

    public ConfigPanelController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        alTextField.setText("0");
        caTextField.setText("0");
        deTextField.setText("0");
        enTextField.setText("0");
        gaTextField.setText("0");
        imTextField.setText("0");
        neTextField.setText("0");
        orTextField.setText("0");
        spTextField.setText("0");
        stTextField.setText("0");
        urTextField.setText("0");
        viTextField.setText("0");


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
