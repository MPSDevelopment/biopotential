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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.util.Callback;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DiagPanelController {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiagPanelController.class);
    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml");
    private static final int LIMIT = 2;

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
    private Button selectFromDbButton;

    @FXML
    private AnchorPane dbAnchorPane;

    @FXML
    private RadioButton manRadioButton;

    @FXML
    private RadioButton womanRadioButton;

    StackPane tablePane = new StackPane();
    @FXML
    private StackPane stackpane;
    final ToggleGroup radioGroup = new ToggleGroup();

    private final static int dataSize = 100;
    private final static int rowsPerPage = 10;
    ProgressIndicator progressIndicator = new ProgressIndicator();
    private User[] users;

    public DiagPanelController() {

    }

    @FXML
    public void initialize() throws NoSuchMethodException {
        User user = new User();
        manRadioButton.setToggleGroup(radioGroup);
        manRadioButton.setUserData("M");
        womanRadioButton.setToggleGroup(radioGroup);
        manRadioButton.setUserData("W");

        dbAnchorPane.setVisible(false);
        deviceBioHttpClient = APP_CONTEXT.getBean(BioHttpClient.class);
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

        getUser();

        // заполняем таблицу данными
        tableUsers.setItems(usersData);

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {


                user.setSurname(surname.getValue());
                user.setName(name.getValue());
                user.setPatronymic(patronymic.getValue());
                user.setTel(tel.getValue());
                user.setEmail(email.getValue());
                user.setBornPlace(born.getValue());
                user.setAdministrator(false);
                if (takeDate() != null) {
                    user.setBornDate(takeDate());
                }

                String body = JsonUtils.getJson(user);
                LOGGER.info("User - %s", body);
                deviceBioHttpClient.executePutRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER, body);
                getUser();
                createPopup();
            }
        });

        selectFromDbButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dbAnchorPane.setVisible(true);
            }
        });

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

    private Popup createPopup() {
        final Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setX(300);
        popup.setY(200);
        popup.getContent().addAll(new Circle(25, 25, 50, Color.AQUAMARINE));
        return popup;
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
        String formDate = null;
        if (selectedId != null) {

            surnameField.setText(selectedId.getSurname());
            nameField.setText(selectedId.getName());
            patronymicField.setText(selectedId.getPatronymic());
            telField.setText(selectedId.getTel());
            emailField.setText(selectedId.getEmail());
            bornField.setText(selectedId.getBornPlace());
            dateField.setText(String.valueOf(selectedId.getBornDate().getDate()));
            if (selectedId.getBornDate().getMonth()< 10) {
                formDate = "0" + String.valueOf(selectedId.getBornDate().getMonth());
            }
            monthField.setText(formDate);
            yearField.setText(String.valueOf(selectedId.getBornDate().getYear()+1900));
            if(selectedId.getGender().equals(User.Gender.Мужчина)) {
                manRadioButton.setSelected(true);
            }
            else womanRadioButton.setSelected(true);
        }
    }







}
