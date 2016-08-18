package com.mpsdevelopment.biopotential.server.gui.authentication;

import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AuthPanelController {

    private static final Logger LOGGER = LoggerUtil.getLogger(AuthPanelController.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml");

    private BioHttpClient deviceBioHttpClient;

    private User user;

    /*StringProperty logString = new SimpleStringProperty();
    StringProperty passString = new SimpleStringProperty();*/


    @FXML
    private TextField loginField;

    @FXML
    private TextField passField;

    @FXML
    private Button enterButton;

    public AuthPanelController() {

    }

    @FXML
    public void initialize() throws NoSuchMethodException {

        User user = new User();
        deviceBioHttpClient = APP_CONTEXT.getBean(BioHttpClient.class);
        loginField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                user.setLogin(newValue);

            }
        });

        passField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                user.setPassword(newValue);
            }
        });

        enterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                /*logString.bind(loginField.textProperty());
                passString.bind(passField.textProperty());

                logString.addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        user.setLogin(newValue);

                    }
                });*/

                String body = JsonUtils.getJson(user);
                LOGGER.info("User - %s",body);
                deviceBioHttpClient.executePostRequest("/api/command/login",body);
            }
        });
    }

}
