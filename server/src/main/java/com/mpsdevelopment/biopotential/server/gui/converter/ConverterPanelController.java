package com.mpsdevelopment.biopotential.server.gui.converter;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.PersistUtils;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.ProgressBarEvent;
import com.mpsdevelopment.biopotential.server.eventbus.event.SelectUserEvent;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.gui.analysis.AnalysisPanel;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConverterPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(ConverterPanelController.class);
//    ObservableList<String> items = FXCollections.observableArrayList("Max", "Po");


    @FXML
    private Button chooseBaseButton;

    @FXML
    private Button chooseStorageButton;

    @FXML
    private Button CancelButton;

    @FXML
    private Button OkButton;

    @FXML
    private TextField nameTextField;

    @FXML
    private ProgressIndicator indicator;

    @FXML
    private ProgressBar progressBar;

    private PersistUtils persistUtils;
    private SessionManager sessionManager;
    private DatabaseCreator databaseCreator;

    private File file;

    private Stage primaryStage;
    private CopyTask copyTask;
    public ConverterPanelController() {
        EventBus.subscribe(this);
        LOGGER.info("Create ConverterPanelController");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        progressBar.progressProperty().addListener(observable -> {
            if (progressBar.getProgress() >= 0.99) {
                progressBar.setStyle("-fx-accent: forestgreen;");
            }
        });



        persistUtils = JettyServer.WEB_CONTEXT.getBean(PersistUtils.class);
        sessionManager = JettyServer.WEB_CONTEXT.getBean(SessionManager.class);

        /*progressBar.progressProperty().unbind();
        copyTask = new CopyTask();

        // Bind progress property
        progressBar.progressProperty().bind(copyTask.progressProperty());*/





        chooseBaseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File("data"));
                file = fileChooser.showOpenDialog(null);
//                selectedFile.getPath().replaceAll(selectedFile.getName(),"");
                restartSessionManager(file.getPath().replaceAll(file.getName(),""));

                copyTask = new CopyTask(file);
                progressBar.progressProperty().bind(copyTask.progressProperty());
                indicator.progressProperty().bind(copyTask.progressProperty());

                Thread thread = new Thread(copyTask, "task-thread");
                thread.setDaemon(true);
                thread.start();

                /*databaseCreator = JettyServer.WEB_CONTEXT.getBean(DatabaseCreator.class);
                try {
                    databaseCreator.convertToH2(file.getAbsolutePath());
                } catch (ArkDBException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
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
               /* ServerSettings fileSettings = BioApplication.APP_CONTEXT.getBean(ServerSettings.class);
                fileSettings.setDbPath(file.getPath());
                String json = JsonUtils.getJson(fileSettings);
                JsonUtils.writeJsonToFile(json.replace("\\\\","/"),"config/server.json");*/
            }
        });

    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        stage.close();
    }

    /*@Handler
    public void handleMessage(ProgressBarEvent event) throws Exception {
        LOGGER.info(" Get delta from convert ");


        *//*Platform.runLater(new Runnable() {
            @Override public void run() {
                progressBar.setProgress(event.getProgress());
            }
        });*//*

        *//*copyTask.setI(event.getProgress());
        copyTask.call();*//*
    }*/

    private void restartSessionManager(String url) {
        String name = "database";
        persistUtils.closeSessionFactory();
        if (nameTextField.getText() != null) {
            name = nameTextField.getText();
        }

        ServerSettings fileSettings = BioApplication.APP_CONTEXT.getBean(ServerSettings.class);
        fileSettings.setDbPath(url+name);
        String json = JsonUtils.getJson(fileSettings);
        JsonUtils.writeJsonToFile(json.replace("\\\\","/"),"config/server.json");

        persistUtils.setConfigurationDatabaseFilename(url+name);
        SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        Session session = sessionFactory.openSession();
        sessionManager.setSession(session);


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
