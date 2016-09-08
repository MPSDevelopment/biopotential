package com.mpsdevelopment.biopotential.server.gui.diagnostics;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.dao.VisitDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.db.pojo.Visit;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.SelectUserEvent;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.SelectFromDbPanel;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiagPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiagPanelController.class);
//    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml", "webapp/web-context.xml");
    private static final int LIMIT = 2;

    @Autowired
    private BioHttpClient deviceBioHttpClient;

    private User user;
    private Stage primaryStage;

    @Autowired
    private ServerSettings settings;

    @Autowired
    private VisitDao visitDao;

    @FXML
    private Pagination pagination;

    @FXML
    private TextField surnameField;
    private final StringProperty surname = new SimpleStringProperty();

    @FXML
    private TextField nameField;
    private final StringProperty name = new SimpleStringProperty();
    private ObservableList<User> usersData = FXCollections.observableArrayList();
    private ObservableList<Visit> historyUsersData = FXCollections.observableArrayList();

    @FXML
    private TextField patronymicField;
    private final StringProperty patronymic = new SimpleStringProperty();

    @FXML
    private TextField emailField;
    private final StringProperty email = new SimpleStringProperty();

    @FXML
    private TextField telField;
    private final StringProperty tel = new SimpleStringProperty();

    @FXML
    private TextField dateField;
    private final StringProperty date = new SimpleStringProperty();

    @FXML
    private TextField yearField;
    private final StringProperty year = new SimpleStringProperty();

    @FXML
    private TextField monthField;
    private final StringProperty month = new SimpleStringProperty();

    @FXML
    private TextField bornField;
    private final StringProperty born = new SimpleStringProperty();

    @FXML
    private TableView<User> tableUsers;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> telColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableView<Visit> historyTableUsers;

    @FXML
    private TableColumn<Visit, String> historyNameColumn;

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

    @FXML
    private Button selectUserButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button selectFromDbButton;

    @FXML
    private AnchorPane dbAnchorPane;

    @FXML
    private RadioButton manRadioButton;

    @FXML
    private RadioButton womanRadioButton;

    @FXML
    private Button automaticButton;


    StackPane tablePane = new StackPane();
    @FXML
    private StackPane stackpane;
    final ToggleGroup radioGroup = new ToggleGroup();

    private final static int dataSize = 100;
    private final static int rowsPerPage = 10;
//    ProgressIndicator progressIndicator = new ProgressIndicator();
    private User[] users;
    private Visit[] visits;

    public DiagPanelController() {

    }

    @FXML
    public void initialize() throws NoSuchMethodException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.subscribe(this);
        User user = new User();
        manRadioButton.setToggleGroup(radioGroup);
        manRadioButton.setUserData("M");
        womanRadioButton.setToggleGroup(radioGroup);
        manRadioButton.setUserData("W");

        dbAnchorPane.setVisible(false);
        /*deviceBioHttpClient = APP_CONTEXT.getBean(BioHttpClient.class);
        settings = APP_CONTEXT.getBean(ServerSettings.class);*/
        Bindings.bindBidirectional(surnameField.textProperty(), surname);
        Bindings.bindBidirectional(nameField.textProperty(), name);
        Bindings.bindBidirectional(patronymicField.textProperty(), patronymic);
        Bindings.bindBidirectional(telField.textProperty(), tel);
        Bindings.bindBidirectional(emailField.textProperty(), email);
        Bindings.bindBidirectional(bornField.textProperty(), born);
        Bindings.bindBidirectional(dateField.textProperty(), date);
        Bindings.bindBidirectional(monthField.textProperty(), month);
        Bindings.bindBidirectional(yearField.textProperty(), year);

        radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (radioGroup.getSelectedToggle() != null) {
                    if(radioGroup.getSelectedToggle().getUserData().toString().equals("W")) {
                        user.setGender(User.Gender.Мужчина);
                    }
                    else user.setGender(User.Gender.Женщина);
                }
            }
        });

        dateField.lengthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    // Check if the new character is greater than LIMIT
                    if (dateField.getText().length() >= LIMIT) {

                        // if it's 11th character then just setText to previous
                        // one
                        dateField.setText(dateField.getText().substring(0, LIMIT));
                        if (Integer.parseInt(dateField.getText()) >= 31) {

                        }
                    }
                }
            }
        });

        // устанавливаем тип и значение которое должно хранится в колонке

        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> user) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%-2s %10s %10s", user.getValue().getName(), user.getValue().getSurname(), user.getValue().getPatronymic()));
                return property;
            }
        });

        telColumn.setCellValueFactory(new PropertyValueFactory<>("tel"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        historyNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Visit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Visit, String> visit) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%-2s %10s %10s", visit.getValue().getUser().getName(), visit.getValue().getUser().getSurname(), visit.getValue().getUser().getPatronymic()));
                return property;
            }
        });

        getUsers();

        // заполняем таблицу данными
        tableUsers.setItems(usersData);


        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                Visit visit = new Visit();

                user.setSurname(surname.getValue());
                user.setName(name.getValue());
                user.setPatronymic(patronymic.getValue());
                user.setTel(tel.getValue());
                user.setEmail(email.getValue());
                user.setBornPlace(born.getValue());
                user.setAdministrator(false);
                visit.setUser(user);
                user.getVisits().add(visit);
//                visitDao.save(visit);

                if (takeDate() != null) {
                    user.setBornDate(takeDate());
                }

                String body = JsonUtils.getJson(user);
                LOGGER.info("User - %s", body);
                deviceBioHttpClient.executePutRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER, body);
                getUsers();
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<User> usersData = getUserList();
                Long Id = null;
                for (User tempUsersData: usersData) {
                    if (tempUsersData.getSurname() != null && tempUsersData.getSurname().equals(surnameField.getText())) {
                        Id = tempUsersData.getId();
                    }
                }

                LOGGER.info("%s", ControllerAPI.USER_CONTROLLER + "/remove/" + String.valueOf(Id));
                deviceBioHttpClient.executeDeleteRequest(ControllerAPI.USER_CONTROLLER + "/remove/" + String.valueOf(Id));
            }
        });

        /*selectUserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                historyUsersData.clear();
                ObservableList<User> usersData = getUserList();
                Long Id = null;
                for (User tempUsersData: usersData) {
                    if (tempUsersData.getSurname() != null && tempUsersData.getSurname().equals(surnameField.getText())) {
                        Id = tempUsersData.getId();
                        historyUsersData.add(tempUsersData);
                    }
                }

                LOGGER.info("%s", String.valueOf(Id));
                historyTableUsers.setItems(historyUsersData);

            }
        });*/

        showHistoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               /* historyUsersData.clear();
                ObservableList<User> usersData = getUserList();
                Long Id = null;
                for (User tempUsersData: usersData) {
                    if (tempUsersData.getSurname() != null && tempUsersData.getSurname().equals(surnameField.getText())) {
                        Id = tempUsersData.getId();
                        historyUsersData.add(tempUsersData);
                    }
                }

                LOGGER.info("%s", String.valueOf(Id));
                historyTableUsers.setItems(historyUsersData);*/

                String url = String.format("http://%s:%s%s", settings.getHost(), settings.getPort(), ControllerAPI.VISITS_CONTROLLER + ControllerAPI.VISITS_CONTROLLER_GET_ALL);
                String json = deviceBioHttpClient.executeGetRequest(url);
                visits = JsonUtils.fromJson(Visit[].class, json);
                historyUsersData.clear();
                for (Visit visit : visits) {
                    if(visit.getUser().getName().equals(nameField.getText())) {
                        LOGGER.info("User - %s", visit.getUser().getName());
                        historyUsersData.add(visit);
                    }

                }
                historyTableUsers.setItems(historyUsersData);

            }
        });

       /* selectFromDbButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dbAnchorPane.setVisible(true);
            }
        });*/

        selectFromDbButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LOGGER.info("  ACTION EDIT METEO in  Scenario");
                SelectFromDbPanel panel = new SelectFromDbPanel();
                Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Выбрать из бд").setClazz(panel.getClass()).setHeight(500d).setWidth(650d).setHeightPanel(450d).setWidthPanel(650d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
//                Stage stage = new Stage();
                panel.setPrimaryStage(stage);

                /*Scene scene = new Scene(panel);
                stage.setScene(scene);
                stage.show();*/
            }
        });

        /*showHistoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Stage stage = new Stage();
                Scene scene = new Scene(new SelectFromDbPanel(stage));
                stage.setScene(scene);
                //Fill stage with content
                stage.show();
            }
        });*/

        automaticButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Visit visit = new Visit();

                user.setSurname(surname.getValue());
                user.setName(name.getValue());
                user.setPatronymic(patronymic.getValue());
                user.setTel(tel.getValue());
                user.setEmail(email.getValue());
                user.setBornPlace(born.getValue());
                user.setAdministrator(false);
                visit.setUser(user);
                user.getVisits().add(visit);

                String body = JsonUtils.getJson(visit);
                LOGGER.info("User - Visit %s", body);
                deviceBioHttpClient.executePutRequest(ControllerAPI.VISITS_CONTROLLER + ControllerAPI.VISITS_CONTROLLER_PUT_CREATE_VISIT, body);

            }
        });

    }

    private Date takeDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = null;
        if (date.getValue() != null && month.getValue() != null && year.getValue() != null) {
            try {
                String d = date.getValue() + "-" + month.getValue() + "-" + year.getValue();
                System.out.println(d);

                currentDate = dateFormat.parse(date.getValue() + "-" + month.getValue() + "-" + year.getValue());
            } catch (ParseException e) {
                e.printStackTrace();
            }

       /* Format formatter = new SimpleDateFormat("dd-MMM-yy");
        String s = formatter.format(date.getValue() + "-" + month.getValue() + "-" + year.getValue());
*/
            System.out.println(currentDate.toString());
        }
        return currentDate;

    }

    /*public void getVisits() {
        String url = String.format("http://%s:%s%s", settings.getHost(), settings.getPort(), ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_GET_ALL);
        String json = deviceBioHttpClient.executeGetRequest(url);
        users = JsonUtils.fromJson(User[].class, json);
        usersData.clear();
        for (User unit : users) {

            LOGGER.info("User - %s", unit.getLogin() +unit.getName() +" " +unit.getSurname());
            usersData.add(unit);
        }
    }*/

    private ObservableList<User> getUserList() {
        String json = deviceBioHttpClient.executeGetRequest("/api/users/all");
        users = JsonUtils.fromJson(User[].class, json);
        usersData.clear();
        for (User unit : users) {

            LOGGER.info("User - %s", unit.getLogin() +unit.getName() +" " +unit.getSurname());
            usersData.add(unit);
        }
        return usersData;
    }

    /*private List<User> loadData(int fromIndex, int toIndex) {
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
    }*/

    @FXML
    private void onTableClick(MouseEvent event) {
        System.out.println("Click");

        User selectedId = tableUsers.getSelectionModel().getSelectedItem();
        String formDate = null;
        if (selectedId != null) {

            surnameField.setText(selectedId.getSurname());
            nameField.setText(selectedId.getName());
            patronymicField.setText(selectedId.getPatronymic());
            telField.setText(selectedId.getTel());
            emailField.setText(selectedId.getEmail());
           /* bornField.setText(selectedId.getBornPlace());
            dateField.setText(String.valueOf(selectedId.getBornDate().getDate()));
            if (selectedId.getBornDate().getMonth()< 10) {
                formDate = "0" + String.valueOf(selectedId.getBornDate().getMonth());
            }
            monthField.setText(formDate);
            yearField.setText(String.valueOf(selectedId.getBornDate().getYear()+1900));
            if(selectedId.getGender().equals(User.Gender.Мужчина)) {
                manRadioButton.setSelected(true);
            }
            else womanRadioButton.setSelected(true);*/
        }
    }

    @Handler
    public void handleMessage(SelectUserEvent event) throws Exception {
        LOGGER.info("  GOT Updated Training Meteo or Scenario parameters ");
        refreshTable(event.getUser());
    }

    private void refreshTable(User user) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                surnameField.setText(user.getSurname());
                nameField.setText(user.getName());
                patronymicField.setText(user.getPatronymic());
                telField.setText(user.getTel());
                emailField.setText(user.getEmail());
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
