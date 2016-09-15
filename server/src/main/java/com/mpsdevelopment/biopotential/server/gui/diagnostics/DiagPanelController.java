package com.mpsdevelopment.biopotential.server.gui.diagnostics;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
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
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class DiagPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiagPanelController.class);

    @Autowired
    private BioHttpClient deviceBioHttpClient;

    @Autowired
    private ServerSettings settings;

    /*@Autowired
    private VisitDao visitDao;
    @FXML
    private Pagination pagination;*/

    @FXML
    private TextField loginField;
    private final StringProperty login = new SimpleStringProperty();

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
    private TableView<Visit> historyTableUsers;

    @FXML
    private TableColumn<Visit, String> dateColumn;

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
    private Button deleteButton;

    @FXML
    private Button selectFromDbButton;

    @FXML
    private RadioButton manRadioButton;

    @FXML
    private RadioButton womanRadioButton;

    @FXML
    private Button automaticButton;

    @FXML
    private DatePicker datePicker;

    private User[] users;
    private Visit[] visits;
    private Stage primaryStage;
    final ToggleGroup radioGroup = new ToggleGroup();

    public DiagPanelController() {

    }

    @FXML
    public void initialize() throws NoSuchMethodException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // change localization to russain
        Locale dLocale = new Locale.Builder().setLanguage("ru").setScript("Cyrl").build();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", dLocale);
        Locale.setDefault(dLocale);

        EventBus.subscribe(this);
        User user = new User();
        manRadioButton.setToggleGroup(radioGroup);
        manRadioButton.setUserData("M");
        womanRadioButton.setToggleGroup(radioGroup);
        manRadioButton.setUserData("W");


        Bindings.bindBidirectional(loginField.textProperty(), login);
        Bindings.bindBidirectional(surnameField.textProperty(), surname);
        Bindings.bindBidirectional(nameField.textProperty(), name);
        Bindings.bindBidirectional(patronymicField.textProperty(), patronymic);
        Bindings.bindBidirectional(telField.textProperty(), tel);
        Bindings.bindBidirectional(emailField.textProperty(), email);
        Bindings.bindBidirectional(bornField.textProperty(), born);
        Bindings.bindBidirectional(dateField.textProperty(), date);
        Bindings.bindBidirectional(monthField.textProperty(), month);
        Bindings.bindBidirectional(yearField.textProperty(), year);

        // внести в базу
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                Visit visit = new Visit();

                user.setLogin(login.getValue());
                user.setSurname(surname.getValue());
                user.setName(name.getValue());
                user.setPatronymic(patronymic.getValue());
                user.setTel(tel.getValue());
                user.setEmail(email.getValue());
                user.setBornPlace(born.getValue());
                user.setAdministrator(false);
                visit.setUser(user);
                visit.setDate(takeDate());



//                Locale.setDefault(Locale.FRANCE);

                LocalDate localDate = datePicker.getValue();
                if(datePicker.getValue() == null) {
                    user.setBornDate(null);
                }
                else {
                    Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    Date res = Date.from(instant);

                    user.setBornDate(res);
                }

                user.getVisits().add(visit);

//                user.setBornDate(datePicker.getValue());

                /*if (takeDate() != null) {
                    user.setBornDate(takeDate());
                }*/

                String body = JsonUtils.getJson(user);

                deviceBioHttpClient.executePutRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER, body);
                getUsers();
            }
        });

        datePicker.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {

                datePicker.setConverter(new StringConverter<LocalDate>() {

                    @Override
                    public String toString(LocalDate object) {
                        return object.format(formatter);
                    }

                    @Override
                    public LocalDate fromString(String string) {
                        return LocalDate.parse(string, formatter);
                    }
                });

                LocalDate localDate = datePicker.getValue();
                Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                Date res = Date.from(instant);

                user.setBornDate(res);

                if (localDate.getDayOfMonth() < 10) {
                    dateField.setText(String.valueOf("0" + String.valueOf(localDate.getDayOfMonth())));
                }
                else dateField.setText(String.valueOf(String.valueOf(localDate.getDayOfMonth())));
                if (localDate.getMonthValue() < 10) {
                    monthField.setText(String.valueOf("0" + String.valueOf(localDate.getMonthValue())));
                }
                else monthField.setText(String.valueOf(String.valueOf(localDate.getMonthValue())));

                yearField.setText(String.valueOf(localDate.getYear()));
            }
        });


        // radiobutton's for choose gender
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

        historyNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Visit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Visit, String> visit) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%-2s %10s %10s", visit.getValue().getUser().getName(), visit.getValue().getUser().getSurname(), visit.getValue().getUser().getPatronymic()));
                return property;
            }
        });

        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Visit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Visit, String> visit) {
                SimpleStringProperty property = new SimpleStringProperty();
                String month = null;
                if (visit.getValue().getUser().getBornDate().getMonth() < 10) {
                    month = (String.valueOf("0" + String.valueOf(visit.getValue().getUser().getBornDate().getMonth() + 1)));
                }
                else month = String.valueOf(String.valueOf(visit.getValue().getUser().getBornDate().getMonth() + 1));

                /*Date tempdate = visit.getValue().getDate();
                Instant instant = Instant.ofEpochMilli(tempdate.getDate());
                LocalDate res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();*/

                property.setValue(String.format("%s-%s-%s", visit.getValue().getUser().getBornDate().getDate(), month , visit.getValue().getUser().getBornDate().getYear() + 1900));
//                property.setValue(String.format("%s-%s-%s", res.getDayOfMonth(), res.getMonth().getValue(), res.getYear()));
                return property;
            }
        });

        getUsers();

     /*   // заполняем таблицу данными
        tableUsers.setItems(usersData);*/




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


        showHistoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

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

        automaticButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Visit visit = new Visit();
                User user = new User();

                user.setSurname(surname.getValue());
                user.setName(name.getValue());
                user.setPatronymic(patronymic.getValue());
                user.setTel(tel.getValue());
                user.setEmail(email.getValue());
                user.setBornPlace(born.getValue());

                LocalDate localDate = datePicker.getValue();
                if(datePicker.getValue() == null) {
                    user.setBornDate(null);
                }
                else {
                    Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    Date res = Date.from(instant);

                    user.setBornDate(res);
                }

                user.setAdministrator(false);
                visit.setUser(user);
                visit.setDate(takeDate());
                user.getVisits().add(visit);

                String body = JsonUtils.getJson(visit);
                LOGGER.info("User - Visit %s", body);
                deviceBioHttpClient.executePutRequest(ControllerAPI.VISITS_CONTROLLER + ControllerAPI.VISITS_CONTROLLER_PUT_CREATE_VISIT, body);

            }
        });

    }

    private Date takeDate() {
//        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date currentDate = null;
        if (date.getValue() != null && month.getValue() != null && year.getValue() != null) {
            try {
//                String d = date.getValue() + "-" + month.getValue() + "-" + year.getValue();
                String d = date.getValue() + "." + month.getValue() + "." + year.getValue();
                System.out.println(d);

                currentDate = dateFormat.parse(date.getValue() + "." + month.getValue() + "." + year.getValue());
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



    /*@FXML
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
        }
    }*/

    @Handler
    public void handleMessage(SelectUserEvent event) throws Exception {
        LOGGER.info("  GOT Updated Training Meteo or Scenario parameters ");
        refreshTable(event.getUser());
    }

    private void refreshTable(User user) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    loginField.setText(user.getLogin());
                    surnameField.setText(user.getSurname());
                    nameField.setText(user.getName());
                    patronymicField.setText(user.getPatronymic());
                    telField.setText(user.getTel());
                    emailField.setText(user.getEmail());
                    bornField.setText(user.getBornPlace());
                    dateField.setText(String.valueOf(user.getBornDate().getDate()));
                    if (user.getBornDate().getMonth() < 10) {
                        monthField.setText(String.valueOf("0" + String.valueOf(user.getBornDate().getMonth() + 1)));
                    }
                    yearField.setText(String.valueOf(user.getBornDate().getYear() + 1900));
                }
                catch (NullPointerException e) {
                    System.out.println("Date is null");
                }

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
