package com.mpsdevelopment.biopotential.server.gui.diagnostics.subpanels;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.gui.analysis.AnalysisPanel;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AutomaticsPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(AutomaticsPanelController.class);
    ObservableList<String> items = FXCollections.observableArrayList("Max", "Po");
    ObservableList<String> item = FXCollections.observableArrayList("2");

    private File file;

    @FXML
    private ComboBox whatShowComboBox;

    @FXML
    private ComboBox whatShowComboBox1;

    @FXML
    private ComboBox criterionComboBox;

    @FXML
    private Button whatToShowButton;

    @FXML
    private Button criterionButton;

    @FXML
    private Button makeAnalyzerButton;

    @FXML
    private Button automaticsCancelButton;

    private Stage primaryStage;

    public AutomaticsPanelController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        whatShowComboBox.setItems(items);
        whatShowComboBox.setValue("Max");
        whatShowComboBox1.setItems(items);
        whatShowComboBox1.setValue("Po");
        criterionComboBox.setItems(item);
        criterionComboBox.setValue("2");

        makeAnalyzerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String degree1 = null;
                String degree2 = null;
                degree1 = (String) whatShowComboBox.getValue();
                degree2 = (String) whatShowComboBox1.getValue();

                AnalysisPanel panel = new AnalysisPanel(file,degree1,degree2);
                Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Результат анализа").setClazz(panel.getClass()).setHeight(752d).setWidth(1172d).setHeightPanel(722d).setWidthPanel(1172d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                panel.setPrimaryStage(stage);
                close();
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

    public void setFile(File file) {
        this.file = file;
    }
}
