package com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SelectFromDbPanelController extends AbstractController {

//    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml", "webapp/web-context.xml");
    StackPane tablePane = new StackPane();
    private final static int dataSize = 100;
    private final static int rowsPerPage = 10;
    private User[] users;
    ProgressIndicator progressIndicator = new ProgressIndicator();
    private static final Logger LOGGER = LoggerUtil.getLogger(SelectFromDbPanelController.class);
    private ObservableList<User> usersData = FXCollections.observableArrayList();

    @Autowired
    private BioHttpClient deviceBioHttpClient;

    @FXML
    private StackPane stackpane;

    @FXML
    private TableView<User> tableUsers;

    @FXML
    private Button selectUserButton;



    public SelectFromDbPanelController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        deviceBioHttpClient = APP_CONTEXT.getBean(BioHttpClient.class);
        selectUserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                LOGGER.info("  ACTION SAVE METEO");

                    /*EventBus.publishEvent(new User());
                    close();*/
            }
        });

        getUser();
        // заполняем таблицу данными
        tableUsers.setItems(usersData);

        Pagination pagination = new Pagination((dataSize / rowsPerPage + 1), 0);
        progressIndicator.setMaxSize(200, 200);

        // wrap table and progress indicator into a stackpane, progress indicator is on top of table
        tablePane.getChildren().add(tableUsers);
        tablePane.getChildren().add(progressIndicator);

        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                progressIndicator.setVisible(true);

                usersData.clear();

                // long running background task
                new Thread() {
                    public void run() {
                        try {
                            int fromIndex = pageIndex * rowsPerPage;
                            int toIndex = Math.min(fromIndex + rowsPerPage, dataSize);

                            List<User> loadedList = loadData(fromIndex, toIndex);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    usersData.setAll(loadedList);
                                }
                            });

                        } finally {

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    progressIndicator.setVisible(false);
                                }
                            });

                        }
                    }
                }.start();

                return tablePane;
            }
        });

        BorderPane borderPane = new BorderPane(pagination);
        stackpane.getChildren().add(borderPane);

    }

    @FXML
    private void onTableClick(MouseEvent event) {
        System.out.println("Click");

   /*     User selectedId = tableUsers.getSelectionModel().getSelectedItem();
        String formDate = null;
        if (selectedId != null) {

            surnameField.setText(selectedId.getSurname());
            nameField.setText(selectedId.getName());
            patronymicField.setText(selectedId.getPatronymic());
            telField.setText(selectedId.getTel());
            emailField.setText(selectedId.getEmail());
           *//* bornField.setText(selectedId.getBornPlace());
            dateField.setText(String.valueOf(selectedId.getBornDate().getDate()));
            if (selectedId.getBornDate().getMonth()< 10) {
                formDate = "0" + String.valueOf(selectedId.getBornDate().getMonth());
            }
            monthField.setText(formDate);
            yearField.setText(String.valueOf(selectedId.getBornDate().getYear()+1900));
            if(selectedId.getGender().equals(User.Gender.Мужчина)) {
                manRadioButton.setSelected(true);
            }
            else womanRadioButton.setSelected(true);*//*
        }*/
    }

    private List<User> loadData(int fromIndex, int toIndex) {
        List<User> list = new ArrayList<>();
        try {
            for (int i = fromIndex; i < users.length; i++) {
                list.add(users[i]);
//                list.add(new User(i, "foo " + i, "bar " + i));
            }
            Thread.sleep(500);
        } catch( Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void getUser() {
        String json = deviceBioHttpClient.executeGetRequest("/api/users/all");
        users = JsonUtils.fromJson(User[].class, json);
        usersData.clear();
        for (User unit : users) {

            LOGGER.info("User - %s", unit.getLogin() +unit.getName() +" " +unit.getSurname());
            usersData.add(unit);
        }
    }
}
