package com.mpsdevelopment.biopotential.server.gui.authentication;

import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AuthPanelController {

    private static final Logger LOGGER = LoggerUtil.getLogger(AuthPanelController.class);

    private BioHttpClient deviceBioHttpClient;

    private User user;

    @FXML
    private TextField loginField;
    private final StringProperty login = new SimpleStringProperty();
    @FXML
    private TextField passField;
    private final StringProperty password = new SimpleStringProperty();

    @FXML
    private Button enterButton;

    public AuthPanelController() {

    }

    @FXML
    public void initialize() throws NoSuchMethodException {

        User user = new User();
        deviceBioHttpClient = BioApplication.APP_CONTEXT.getBean(BioHttpClient.class);
        // #2 Use property bindings
        Bindings.bindBidirectional(loginField.textProperty(), login);
        Bindings.bindBidirectional(passField.textProperty(), password);

        // #1 Use listeners to listen change of textproperty of textfield
        /*loginField.textProperty().addListener(new ChangeListener<String>() {
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
        });*/

        enterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                user.setLogin(login.getValue());
                user.setPassword(password.getValue());

                LOGGER.info("Login - %s",user.getLogin());
                LOGGER.info("Password - %s",user.getPassword());

                String body = JsonUtils.getJson(user);
                LOGGER.info("User - %s",body);
                deviceBioHttpClient.executePostRequest("/api/command/login",body);
            }
        });
    }

}
