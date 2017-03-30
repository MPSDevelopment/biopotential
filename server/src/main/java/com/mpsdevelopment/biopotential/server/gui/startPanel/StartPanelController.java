package com.mpsdevelopment.biopotential.server.gui.startPanel;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.db.PersistUtils;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.gui.ConverterApplication;
import com.mpsdevelopment.biopotential.server.gui.diagnostics.DiagPanel;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class StartPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(StartPanelController.class);
    ObservableList<String> items = FXCollections.observableArrayList("Max", "Po");
    ObservableList<String> item = FXCollections.observableArrayList("2");

    private File file;

    @FXML
    private TextField dbLabel;

    @FXML
    private TextField storLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    @FXML
    private Button editButton;

    @FXML
    private Button chooseStorageButton;

    @FXML
    private Button chooseDbButton;

    @FXML
    private Button automaticsCancelButton;

    private Stage primaryStage;
    private PersistUtils persistUtils;
    private SessionManager sessionManager;
    private ServerSettings serverSettings;

    public StartPanelController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        persistUtils = JettyServer.WEB_CONTEXT.getBean(PersistUtils.class);
        serverSettings = JettyServer.WEB_CONTEXT.getBean(ServerSettings.class);
        sessionManager = JettyServer.WEB_CONTEXT.getBean(SessionManager.class);

        dbLabel.setText(serverSettings.getDbPath());
        storLabel.setText(serverSettings.getStoragePath());
        okButton.setDisable(false);

        chooseDbButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(new File("data"));
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("mv.db files (*.mv.db)", "*.mv.db");
                    fileChooser.getExtensionFilters().add(extFilter);
                    file = fileChooser.showOpenDialog(null);

                    dbLabel.setText(file.getName());
                    chooseDbButton.setDisable(false);

                    restartSessionManager(file.getPath().replaceAll(file.getName(),"").replaceAll(".mv.db",""));

                /*ServerSettings fileSettings = ConverterApplication.APP_CONTEXT.getBean(ServerSettings.class);
                fileSettings.setDbPath(file.getPath().replaceAll(file.getName(),""));
                String json = JsonUtils.getJson(fileSettings);
                JsonUtils.writeJsonToFile(json.replace("\\\\","/"),"config/server.json");*/
                    okButton.setDisable(false);
                } catch (NullPointerException e) {
                    LOGGER.info("File don't choose or empty");
                }


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
                serverSettings.setStoragePath(selectedDirectory.getAbsolutePath() + "/");
                String json = JsonUtils.getJson(serverSettings);
                JsonUtils.writeJsonToFile(json.replace("\\\\","/"),"config/server.json");
                storLabel.setText(selectedDirectory.getName());

            }
        });

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                restartSessionManager(serverSettings.getDbPath());
                /*DiagPanel diagPanel = new DiagPanel();
                LOGGER.info(" Start Diag panel");
                Stage mainPanelStage = StageUtils.createStage(null, diagPanel, new StageSettings().setClazz(DiagPanel.class).setHeight(740d).setWidth(1034d).setHeightPanel(727d).setWidthPanel(1034d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                diagPanel.setPrimaryStage(mainPanelStage);*/
                close();


                /*mainPanelStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent e) {
//                stop();
                    }
                });*/
            }
        });

        editButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ConfigPanel configPanel = new ConfigPanel();
                LOGGER.info(" Start Config panel");
                Stage mainPanelStage = StageUtils.createStage(null, configPanel, new StageSettings().setPanelTitle("Файл конфигурации").setClazz(DiagPanel.class).setHeight(483d).setWidth(632d).setHeightPanel(483d).setWidthPanel(632d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                configPanel.setPrimaryStage(mainPanelStage);
            }
        });

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });
    }

    private void restartSessionManager(String url) {
        String name = "database";
//        persistUtils.closeSessionFactory();

        if (file == null) {
            name = url;
        }
        else {
            name = file.getName();
//            ServerSettings fileSettings = ConverterApplication.APP_CONTEXT.getBean(ServerSettings.class);
            serverSettings.setDbPath(url+name);
            String json = JsonUtils.getJson(serverSettings);
            JsonUtils.writeJsonToFile(json.replace("\\\\","/").replace(".mv.db", ""),"config/server.json");
        }

        persistUtils.setConfigurationDatabaseFilename(serverSettings.getDbPath().replace(".mv.db", ""));
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

    public void setFile(File file) {
        this.file = file;
    }
}
