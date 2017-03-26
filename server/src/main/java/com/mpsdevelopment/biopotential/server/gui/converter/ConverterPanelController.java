package com.mpsdevelopment.biopotential.server.gui.converter;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.db.PersistUtils;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.EnableButtonEvent;
import com.mpsdevelopment.biopotential.server.gui.ConverterApplication;
import com.mpsdevelopment.biopotential.server.gui.service.JavaFxService;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.engio.mbassy.listener.Handler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ConverterPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(ConverterPanelController.class);

    @FXML
    private Button chooseBaseButton;

    @FXML
    private Button chooseStorageButton;

    @FXML
    private Button CancelButton;

    @FXML
    private Button OkButton;

    @FXML
    private ProgressIndicator indicator;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField storLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private TextField field;

    private PersistUtils persistUtils;
    private SessionManager sessionManager;

    private Stage primaryStage;
    private ProgressBarTask progressBarTask;
    private File file;

    public ConverterPanelController() {
        EventBus.subscribe(this);
        LOGGER.info("Create ConverterPanelController");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        persistUtils = ConverterApplication.APP_CONTEXT.getBean(PersistUtils.class);
        sessionManager = ConverterApplication.APP_CONTEXT.getBean(SessionManager.class);

        storLabel.setText("");
        chooseBaseButton.setDisable(true);

        progressBar.progressProperty().addListener(observable -> {
            if (progressBar.getProgress() >= 0.99) {
                progressBar.setStyle("-fx-accent: forestgreen;");
            }
        });

        chooseBaseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File("data"));
                file = fileChooser.showOpenDialog(null);
                restartSessionManager(file.getPath().replaceAll(file.getName(),""));

                progressBarTask = new ProgressBarTask();
                progressBar.progressProperty().bind(progressBarTask.progressProperty());
                indicator.progressProperty().bind(progressBarTask.progressProperty());

                /*Thread thread = new Thread(progressBarTask, "task-thread");
                thread.setDaemon(true);
                thread.start();*/
                JavaFxService service = new JavaFxService();
                service.setFile(file);
                service.start();
                disableAllButtons();
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
                storLabel.setText(selectedDirectory.getName());
                chooseBaseButton.setDisable(false);

            }
        });

        CancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });

        OkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });

    }

    private void disableAllButtons() {
        chooseBaseButton.setDisable(true);
        chooseStorageButton.setDisable(true);
        OkButton.setDisable(true);
        CancelButton.setDisable(true);

    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        stage.close();
    }

    private void restartSessionManager(String url) {
        String name = "database";
        persistUtils.closeSessionFactory();
        /*if (nameTextField.getText() != null) {
            name = nameTextField.getText();
        }*/

        name = file.getName().replaceAll(".arkdb","");

        ServerSettings fileSettings = ConverterApplication.APP_CONTEXT.getBean(ServerSettings.class);
        fileSettings.setDbPath(url+name);
        String json = JsonUtils.getJson(fileSettings);
        JsonUtils.writeJsonToFile(json.replace("\\\\","/"),"config/server.json");

        persistUtils.setConfigurationDatabaseFilename(url+name);
        SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        Session session = sessionFactory.openSession();
        sessionManager.setSession(session);

    }

    @Handler
    public void handleMessage(EnableButtonEvent event) throws Exception {
        timeLabel.setVisible(true);
        field.setVisible(true);
        field.setText(event.getTimeOfConvert() + " ms");
        LOGGER.info("Enable ok buttons ");
        if (OkButton.isDisabled()) {
            OkButton.setDisable(false);
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
}
