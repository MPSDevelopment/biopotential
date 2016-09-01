package com.mpsdevelopment.biopotential.server.gui.diagnostics;

import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiagPanelController {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiagPanelController.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml");

    private BioHttpClient deviceBioHttpClient;

    private User user;

    @FXML
    private Pagination pagination;

    @FXML
    private TextField surnameField;
    private final StringProperty surname = new SimpleStringProperty();

    @FXML
    private TextField nameField;
    private final StringProperty name = new SimpleStringProperty();
    private ObservableList<User> usersData = FXCollections.observableArrayList();

    @FXML
    private TextField patronymicField;
    private final StringProperty patronymic = new SimpleStringProperty();

    @FXML
    private TableView<User> tableUsers;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button showHistoryButton;

    @FXML
    private Button sensorrButton;

    @FXML
    private Button stopRecButton;

    @FXML
    private Button saveAsrButton;

    @FXML
    private Button fileButton;

    StackPane tablePane = new StackPane();
    @FXML
    private StackPane stackpane;

    private final static int dataSize = 499;
    private final static int rowsPerPage = 7;
    ProgressIndicator progressIndicator = new ProgressIndicator();
    private User[] users;


    public DiagPanelController() {

    }

    @FXML
    public void initialize() throws NoSuchMethodException {

        /*User user = new User();
        user.setName("Dima");
        user.setSurname("Nazarenko");
        user.setPatronymic("Vladimirovich");*/
        deviceBioHttpClient = APP_CONTEXT.getBean(BioHttpClient.class);
        Bindings.bindBidirectional(surnameField.textProperty(), surname);
        Bindings.bindBidirectional(nameField.textProperty(), name);
        Bindings.bindBidirectional(patronymicField.textProperty(), patronymic);

        /*String body = JsonUtils.getJson(user);
        deviceBioHttpClient.executePutRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER,body);*/

        usersData.addListener(new ListChangeListener<User>(){

            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends User> pChange) {
                while(pChange.next()) {
                    // Do your changes here
                }
            }
        });
        // TableView , return selected item
      /*  tableUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observableValue, User oldValue, User newValue) {
                //Check whether item is selected and set value of selected item to Label
                if (tableUsers.getSelectionModel().getSelectedItem() != null) {
//                    usersData.addAll((Collection<? extends User>) tableUsers);
                }
            }
        });*/

        // устанавливаем тип и значение которое должно хранится в колонке

        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> user) {
                SimpleStringProperty property = new SimpleStringProperty();
                    property.setValue(String.format("%-2s %10s %10s", user.getValue().getName(), user.getValue().getSurname(), user.getValue().getPatronymic()));
                return property;
            }
        });


        getUser();

        // заполняем таблицу данными
        tableUsers.setItems(usersData);

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                User user = new User();
                user.setSurname(surname.getValue());
                user.setName(name.getValue());
                user.setPatronymic(patronymic.getValue());
                user.setAdministrator(false);

                LOGGER.info("Surname - %s",user.getSurname());
                LOGGER.info("Name - %s",user.getName());
                LOGGER.info("Patronymic - %s",user.getPatronymic());

                String body = JsonUtils.getJson(user);
                LOGGER.info("User - %s",body);
                deviceBioHttpClient.executePutRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER,body);
                getUser();
            }
        });



        /*pagination = new Pagination((usersData.size() / rowsPerPage() + 1), 0);
//        pagination.setPageCount(5);
//           pagination = new Pagination(20 , 0);
        pagination.setStyle("-fx-border-color:red;");
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > usersData.size() / rowsPerPage() + 1) {
                    return null;
                } else {
                    return createPage(pageIndex);
                }
            }
        });*/

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

    private void getUser() {
        String json = deviceBioHttpClient.executeGetRequest("/api/users/all");
        users = JsonUtils.fromJson(User[].class, json);
        usersData.clear();
        for (User unit : users) {

            LOGGER.info("User - %s", unit.getLogin() +unit.getName() +" " +unit.getSurname());
            usersData.add(unit);
        }
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

    @FXML
    private void onTableClick(MouseEvent event) {
        System.out.println("Click");

        User selectedId = tableUsers.getSelectionModel().getSelectedItem();
        surnameField.setText(selectedId.getSurname());
        nameField.setText(selectedId.getName());
        patronymicField.setText(selectedId.getPatronymic());
    }




    /*public int itemsPerPage() {
        return 1;
    }

    public int rowsPerPage() {
        return 5;
    }

    public VBox createPage(int pageIndex) {
        int lastIndex = 0;
        int displace = usersData.size() % rowsPerPage();
        if (displace > 0) {
            lastIndex = usersData.size() / rowsPerPage();
        } else {
            lastIndex = usersData.size() / rowsPerPage() - 1;

        }

        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();

        for (int i = page; i < page + itemsPerPage(); i++) {
            TableView<User> table = new TableView<User>();
            TableColumn numCol = new TableColumn("ID");
            numCol.setCellValueFactory(
                    new PropertyValueFactory<User, String>("num"));

            numCol.setMinWidth(20);

            TableColumn firstNameCol = new TableColumn("First Name");
            firstNameCol.setCellValueFactory(
                    new PropertyValueFactory<User, String>("firstName"));


            firstNameCol.setMinWidth(160);

            TableColumn lastNameCol = new TableColumn("Last Name");
            lastNameCol.setCellValueFactory(
                    new PropertyValueFactory<User, String>("lastName"));

            lastNameCol.setMinWidth(160);

//            table.getColumns().addAll(numCol, firstNameCol, lastNameCol);
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(usersData.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(usersData.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }


            box.getChildren().add(table);
        }
        return box;
    }*/



}
