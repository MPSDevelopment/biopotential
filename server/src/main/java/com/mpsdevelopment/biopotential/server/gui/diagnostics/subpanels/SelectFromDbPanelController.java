package com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.SelectUserEvent;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javassist.util.proxy.FactoryHelper.dataSize;

public class SelectFromDbPanelController extends AbstractController implements Subscribable {

    private Stage primaryStage;
    StackPane tablePane;
    private /*final*/ static int dataSize = 10;
    private final static int rowsPerPage = 10;
    private User[] users;

    private static final Logger LOGGER = LoggerUtil.getLogger(SelectFromDbPanelController.class);
    private ObservableList<User> usersData = FXCollections.observableArrayList();

    @FXML
    private ProgressIndicator progressIndicator;

    @Autowired
    private BioHttpClient deviceBioHttpClient;

    @Autowired
    private ServerSettings settings;

    @FXML
    private StackPane stackpane;

    @FXML
    private TableView<User> tableUsers;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> telColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private Button selectUserButton;
    private User selectedId;

    public SelectFromDbPanelController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablePane = new StackPane();
        getUsers();

        // заполняем таблицу данными
        tableUsers.setItems(usersData);

        Pagination pagination = new Pagination((dataSize / rowsPerPage + 1), 0);
        progressIndicator.setMaxSize(200, 200);

        // wrap table and progress indicator into a stackpane, progress indicator is on top of table
        tablePane.getChildren().add(tableUsers);
        tablePane.getChildren().add(progressIndicator);

        selectUserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                LOGGER.info(" User selected");
                    EventBus.publishEvent(new SelectUserEvent(selectedId));
                    close();
                }
        });

        // устанавливаем тип и значение которое должно хранится в колонке ФИО
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> user) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s %s %s",  user.getValue().getSurname(), user.getValue().getName(), user.getValue().getPatronymic()));
                return property;
            }
        });

        telColumn.setCellValueFactory(new PropertyValueFactory<>("tel"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

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
                            /*if (loadedList.size() > dataSize) {
                                dataSize=+10;

                            }*/

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

        selectedId = tableUsers.getSelectionModel().getSelectedItem();
        LOGGER.info("Selected user %s", tableUsers.getSelectionModel().getSelectedItem().getName());

    }

    private List<User> loadData(int fromIndex, int toIndex) {
        List<User> list = new ArrayList<>();
        try {
            for (int i = fromIndex; i < toIndex; i++) {
                list.add(users[i]);
            }
            Thread.sleep(500);
        } catch( Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void getUsers() {
        String url = String.format("http://%s:%s%s", settings.getHost(), settings.getPort(), ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_GET_ALL);
        String json = deviceBioHttpClient.executeGetRequest(url);
        users = JsonUtils.fromJson(User[].class, json);
        usersData.clear();
        for (User unit : users) {

//            LOGGER.info("User - %s", unit.getLogin() +unit.getName() +" " +unit.getSurname() + unit.getGender());
            usersData.add(unit);

        }
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

    @Override
    public void subscribe() {
        EventBus.subscribe(this);
    }
}
