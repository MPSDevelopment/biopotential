package com.mpsdevelopment.biopotential.server.gui.analysis;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class HiddenPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(HiddenPanelController.class);
    private ObservableList<String> level;

    @FXML
    private TableView<DataTable> healthConditionHiddenTable;

    @FXML
    private TableColumn<DataTable, String> diseaseName;

    @FXML
    private TableColumn<DataTable, String> diseaseLevel;

    @FXML
    private TableColumn<DataTable, String> numberColumn;

    @FXML
    private TableColumn automaticsLevelColumn;

    @FXML
    private Button closeButton;

    private Stage primaryStage;
    private Map<Pattern, AnalysisSummary> diseases;
    private Map<Pattern, AnalysisSummary> allHealings;
    private ObservableList<DataTable> analysisHiddenData;

    public HiddenPanelController() {
//        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        analysisHiddenData = FXCollections.observableArrayList();
        level = FXCollections.observableArrayList();
        diseases = new HashMap<>();
        allHealings = new HashMap<>();

        healthConditionHiddenTable.setItems(analysisHiddenData);
        healthConditionHiddenTable.getStylesheets().add("table.css");

        numberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(healthConditionHiddenTable.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER;");

        diseaseName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getName()));

                return property;
            }
        });
        diseaseLevel.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getDispersion()));
                return property;
            }
        });

        automaticsLevelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(p.getValue().getDegree());
            }
        });

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });

    }

    private void putData (ObservableList<DataTable> data) throws UnsupportedAudioFileException, IOException, SQLException {

        healthConditionHiddenTable.setItems(data);

        healthConditionHiddenTable.getSortOrder().add(automaticsLevelColumn); // sort cell'a by name
        automaticsLevelColumn.setSortable(true);
        automaticsLevelColumn.setSortType(TableColumn.SortType.ASCENDING);

        diseaseName.setSortable(true);
        diseaseName.setSortType(TableColumn.SortType.ASCENDING);
        healthConditionHiddenTable.getSortOrder().add(diseaseName); // sort cell'a by name

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

    /*@Override
    public void subscribe() { EventBus.subscribe(this); }*/

    public void putDataToTable(ObservableList<DataTable> data) {
        try {
            putData(data);
        } catch (UnsupportedAudioFileException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

}
