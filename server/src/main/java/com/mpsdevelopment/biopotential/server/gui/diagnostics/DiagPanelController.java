package com.mpsdevelopment.biopotential.server.gui.diagnostics;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.db.pojo.Visit;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.eventbus.event.SelectUserEvent;
import com.mpsdevelopment.biopotential.server.gui.converter.ConverterPanel;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.AutomaticsPanel;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels.SelectFromDbPanel;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.httpclient.HttpClientFactory;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.LineChartUtil;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.biopotential.server.wave.WavFileException;
import com.mpsdevelopment.biopotential.server.wave.WavFileExtractor;
import com.mpsdevelopment.biopotential.server.wave.WaveFile;
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
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.engio.mbassy.listener.Handler;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
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
    public static final String HOST = "localhost";
    public static final int PORT = 8098;

    private File selectedFile;

    private BioHttpClient httpClient;

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
    private Button openFileButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button selectFromDbButton;

    @FXML
    public ToggleGroup genderGroup;

    @FXML
    private RadioButton manRadioButton;

    @FXML
    private RadioButton womanRadioButton;

    @FXML
    private Button automaticButton;

    @FXML
    private Button chooseBaseButton;

    @FXML
    private Button chooseStorageButton;

    @FXML
    private Button converterButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private LineChart<Number, Number> numberLineChart;

    private User[] users;
    private Visit[] visits;
    private Stage primaryStage;
    private User user = new User();
    public static final int RATE = 32;
    private String gender = null;

    public DiagPanelController() {
        EventBus.subscribe(this);
        LOGGER.info("Create DiagPanelController");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        httpClient = HttpClientFactory.getInstance();

        User admin = new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD);
        String loginBody = JsonUtils.getJson(admin);

        httpClient.executePostRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, loginBody);
        // change localization to russian
        Locale dLocale = new Locale.Builder().setLanguage("ru").setScript("Cyrl").build();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", dLocale);
        Locale.setDefault(dLocale);

        // set togglegroup
        manRadioButton.setToggleGroup(genderGroup);
        manRadioButton.setUserData("M");
        womanRadioButton.setToggleGroup(genderGroup);
        womanRadioButton.setUserData("W");

        // binding between stringproperty and textfield
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

        // add to database
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                if (surnameField.getText() == null) {
                    final Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(primaryStage);
                    VBox dialogVbox = new VBox(20);
                    Text text = new Text("Введите фамилию пользователя");
                    dialogVbox.getChildren().add(text);
                    text.setTextAlignment(TextAlignment.CENTER);
                    dialogVbox.setAlignment(Pos.CENTER);
                    Button button = new Button("Ok");
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dialog.close();
                        }
                    });
                    button.setAlignment(Pos.CENTER);
                    dialogVbox.getChildren().add(button);
                    button.setAlignment(Pos.CENTER);
                    Scene dialogScene = new Scene(dialogVbox, 200, 100);
                    dialog.setScene(dialogScene);
                    dialog.show();
                }
                else {
                    user.setLogin(login.getValue());
                    user.setSurname(surname.getValue());
                    user.setName(name.getValue());
                    user.setPatronymic(patronymic.getValue());
                    user.setTel(tel.getValue());
                    user.setEmail(email.getValue());
                    user.setBornPlace(born.getValue());
                    user.setAdministrator(false);

                    LocalDate localDate = datePicker.getValue();
                    if(datePicker.getValue() == null) {
                        user.setBornDate(null);
                    }
                    else {
                        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                        Date res = Date.from(instant);
                        user.setBornDate(res);
                    }
                    user.setGender(gender);

                    String body = JsonUtils.getJson(user);


                    httpClient.executePutRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER, body);
                    getUsers();
                }


            }
        });

        // show history
        showHistoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String url = String.format("http://%s:%s%s", HOST, PORT, ControllerAPI.VISITS_CONTROLLER + ControllerAPI.VISITS_CONTROLLER_GET_ALL);
                String json = httpClient.executeGetRequest(url);
                visits = JsonUtils.fromJson(Visit[].class, json);
                historyUsersData.clear();
                for (Visit visit : visits) {
                    if(loginField.getText() == null) {
                        loginField.setFocusTraversable(true); // нужно сделать подсветку поля через CSS
                    }
                    else
                    if(visit.getUser().getName().equals(nameField.getText())) {
                        LOGGER.info("User - %s", visit.getUser().getName());
                        historyUsersData.add(visit);
                    }

                }
                historyTableUsers.setItems(historyUsersData);

            }
        });

        // select user from database
        selectFromDbButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LOGGER.info("  Open SelectedFromDBPanel");
                SelectFromDbPanel panel = new SelectFromDbPanel();
                Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Выбрать из бд").setClazz(panel.getClass()).setHeight(500d).setWidth(650d).setHeightPanel(450d).setWidthPanel(650d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                panel.setPrimaryStage(stage);
                automaticButton.setDisable(false);

            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                /*ObservableList<User> usersData = getUserList();
                Long Id = null;
                for (User tempUsersData: usersData) {
                    if (tempUsersData.getSurname() != null && tempUsersData.getSurname().equals(surnameField.getText())) {
                        Id = tempUsersData.getId();
                    }
                }*/

                LOGGER.info("%s", ControllerAPI.USER_CONTROLLER + "/remove/" + String.valueOf(getUser().getId()));
                httpClient.executeDeleteRequest(ControllerAPI.USER_CONTROLLER + "/remove/" + String.valueOf(getUser().getId()));
                clearFields();
            }
        });

        // select birthday from datepicker
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
        genderGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (genderGroup.getSelectedToggle() != null) {
                    if(genderGroup.getSelectedToggle().getUserData().toString().equals("M")) {
                        gender = String.valueOf(User.Gender.Man);
//                        user.setGender(String.valueOf(User.Gender.Man));
                    }
                    else /*user.setGender(String.valueOf(User.Gender.Woman));*/gender = String.valueOf(User.Gender.Woman);
                }
            }
        });

        // openFile button
        openFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
//                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + System.getProperty("file.separator")+ "files")); // System.getProperty("file.separator") = "/"
                fileChooser.setInitialDirectory(new File("files"));
                selectedFile = fileChooser.showOpenDialog(null);

                EventBus.publishEvent(new FileChooserEvent(selectedFile));

                long t2 = System.currentTimeMillis();
                createChart(selectedFile);
                LOGGER.info("createChart took %s ms", System.currentTimeMillis() - t2);

            }
        });

        // choose DB
        chooseBaseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File("data"));
                File selectedFile = fileChooser.showOpenDialog(null);

                if (!selectedFile.getPath().contains(".mv.db")) {
                    httpClient.executePostRequest(ControllerAPI.FOLDERS_CONTROLLER + "/convertDB/", selectedFile.getAbsolutePath());
                }
                else {httpClient.executePostRequest(ControllerAPI.USER_CONTROLLER + "/change/db/",
                        selectedFile.getAbsolutePath().replaceAll(".mv.db",""));}

            }
        });

        chooseStorageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("EDX storage");
                chooser.setInitialDirectory(new File("data"));
                File selectedDirectory = chooser.showDialog(primaryStage);
                Machine.setEdxFileFolder(selectedDirectory.getAbsolutePath() + "/");
                LOGGER.info("EDX storage %s", selectedDirectory.getAbsolutePath() + "\\");

            }
        });

        // "Автомат" button
        automaticButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // open automatics panel
                AutomaticsPanel panel = new AutomaticsPanel(selectedFile, getUser().getGender());
                Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Автомат").setClazz(panel.getClass()).setHeight(250d).setWidth(300d).setHeightPanel(200d).setWidthPanel(300d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                panel.setPrimaryStage(stage);

                Visit visit = new Visit();
                LOGGER.info("User automatics - Id %s", user.getId());
                visit.setUser(getUser());

                Date date = new Date();
                visit.setDate(date);
                getUser().getVisits().add(visit);

                String body = JsonUtils.getJson(visit);
                LOGGER.info("User - Visit %s", body);
                httpClient.executePutRequest(ControllerAPI.VISITS_CONTROLLER + ControllerAPI.VISITS_CONTROLLER_PUT_CREATE_VISIT, body);

            }
        });

        converterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LOGGER.info("Open ConverterPanel");
                ConverterPanel panel = new ConverterPanel();
                Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Конвертор базы").setClazz(panel.getClass()).setHeight(260d).setWidth(370d).setHeightPanel(250d).setWidthPanel(370d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                panel.setPrimaryStage(stage);

            }
        });

        // ----------------------------------------Table historyTableUsers -----------------------------------------------------------------------------------------------
        // historyNameColumn
        historyNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Visit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Visit, String> visit) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%-2s %10s %10s", visit.getValue().getUser().getName(), visit.getValue().getUser().getSurname(), visit.getValue().getUser().getPatronymic()));
                return property;
            }
        });

        // dateColumn
        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Visit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Visit, String> visit) {
                SimpleStringProperty property = new SimpleStringProperty();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:SS");

                    property.setValue(String.format("%s", dateFormat.format(visit.getValue().getDate())));

                return property;
            }
        });

    }

    // --------------------------------------Methods------------------------------------------

    private void clearFields() {
        login.setValue("");
        surname.setValue("");
        name.setValue("");
        patronymic.setValue("");
        tel.setValue("");
        email.setValue("");
        born.setValue("");
        dateField.setText("");
        monthField.setText("");
        yearField.setText("");
        manRadioButton.setSelected(false);
        womanRadioButton.setSelected(false);

    }

    private void createChart(File selectedFile) {
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();

        numberLineChart.setTitle("Входной сигнал");

//        File file = new File("D:/MPS/Temp/Downloads/test3.wav");
        WaveFile waveFile = null;
        try {
            waveFile = WaveFile.openWavFile(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }
        LOGGER.info(String.valueOf(waveFile.getSampleRate()));
        long sampleRate = waveFile.getSampleRate();
        if (selectedFile != null) {
            double[] extractedData = new double[0];

            try {
                extractedData = extractWaveform(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
            long t1 = System.currentTimeMillis();
            XYChart.Series<Number, Number> numberSeries = LineChartUtil.chart(extractedData, RATE,sampleRate);
//            XYChart.Series<Number, Number> numberSeries = LineChartUtil.createNumberSeries(extractedData, RATE,sampleRate);
            LOGGER.info("Time create createNumberSeries %s ms", System.currentTimeMillis() - t1);
            numberLineChart.getData().clear();
//            numberLineChart.getStylesheets().add(AnalysisPanelController.class.getResource("main.css").toExternalForm());
            numberLineChart.getStylesheets().add("main.css");
            long t2 = System.currentTimeMillis();
            numberLineChart.setCache(true);
            numberLineChart.setCacheHint(CacheHint.SPEED);
            numberLineChart.setCacheShape(true);
            numberLineChart.getData().addAll(numberSeries);
            LOGGER.info("numberSeries size %s", numberSeries.getData().size());
            LOGGER.info("Adding data took %s ms", System.currentTimeMillis() - t2);
            numberLineChart.createSymbolsProperty();

            /*long t3 = System.currentTimeMillis();
            numberLineChart.setCreateSymbols(false);
            numberLineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            LOGGER.info("setCreateSymbols %s ms", System.currentTimeMillis() - t3);*/
        }
    }

    private Date takeDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date currentDate = null;
        if (date.getValue() != null && month.getValue() != null && year.getValue() != null) {
            try {
                String d = date.getValue() + "." + month.getValue() + "." + year.getValue();
                System.out.println(d);

                currentDate = dateFormat.parse(date.getValue() + "." + month.getValue() + "." + year.getValue());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            System.out.println(currentDate.toString());
        }
        return currentDate;

    }

    private ObservableList<User> getUserList() {
        String json = httpClient.executeGetRequest("/api/users/all");
        users = JsonUtils.fromJson(User[].class, json);
        usersData.clear();
        for (User unit : users) {

            LOGGER.info("User - %s", unit.getLogin() +unit.getName() +" " +unit.getSurname());
            usersData.add(unit);
        }
        return usersData;
    }

    // handler for selected user from db
    @Handler
    public void handleMessage(SelectUserEvent event) throws Exception {
        LOGGER.info("  GOT selected user from db table ");
        refreshTable(event.getUser());
    }

    private void refreshTable(User selectedUser) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    setUser(selectedUser);
//                    getFile().setId(selectedUser.getId());
                    LOGGER.info("User - Id %s", user.getId());
                    loginField.setText(selectedUser.getLogin());
                    surnameField.setText(selectedUser.getSurname());
                    nameField.setText(selectedUser.getName());
                    patronymicField.setText(selectedUser.getPatronymic());
                    telField.setText(selectedUser.getTel());
                    emailField.setText(selectedUser.getEmail());
                    bornField.setText(selectedUser.getBornPlace());
                    if (selectedUser.getBornDate() != null) {
                        dateField.setText(String.valueOf(selectedUser.getBornDate().getDate()));
                        if (selectedUser.getBornDate().getMonth() < 10) {
                            monthField.setText(String.valueOf("0" + String.valueOf(selectedUser.getBornDate().getMonth() + 1)));
                        }
                        yearField.setText(String.valueOf(selectedUser.getBornDate().getYear() + 1900));
                    }
                    if (getUser().getGender().equals("Man")) {
                        manRadioButton.setSelected(true);
                    }
                    else {
                        womanRadioButton.setSelected(true);
                    }
                }
                catch (NullPointerException e) {
                    System.out.println("BornDate is null");
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

    private double[] extractWaveform(File file) throws IOException, UnsupportedAudioFileException {
        return new WavFileExtractor().extract(file);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
