package com.mpsdevelopment.biopotential.server.gui.analysis;

import com.mps._SoundIO;
import com.mps.analyzer.AnalysisSummary;
import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.machine.Machine;
import com.mps.machine.Strain;
import com.mps.machine.SummaryCondition;
import com.mps.machine.dbs.arkdb.ArkDB;
import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;

import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

public class AnalysisPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(AnalysisPanelController.class);
    private ObservableList<DataTable> analysisData = FXCollections.observableArrayList();

    @FXML
    private ScatterChart<Number,Number> scatterChart;

    @FXML
    private TableView<DataTable> healthConditionTable;

    @FXML
    private TableColumn<DataTable, String> deseaseName;

    @FXML
    private TableColumn<DataTable, String> deseaseLevel;

    @FXML
    private TableColumn<DataTable, String> numberColumn;

    private Stage primaryStage;
    private static File file;

    public AnalysisPanelController() {
        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init(file);

        healthConditionTable.setItems(analysisData);

        numberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(healthConditionTable.getItems().indexOf(p.getValue()) + "");
            }
        });
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER;");

        deseaseName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getName()));

                return property;
            }
        });
        deseaseLevel.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getDegree()));
                return property;
            }
        });

        scatterChart.setTitle("Body Overview");

        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(20.0, 90));
        series1.getData().add(new XYChart.Data(2.8, 33.6));
        series1.getData().add(new XYChart.Data(1.8, 81.4));

        scatterChart.getStylesheets().add("scater.css");
        scatterChart.getData().addAll(series1);

    }

    private void init(File file) {
        try {

            final ArkDB db = new ArkDB("test.arkdb");
            db.setHealingFolders(Arrays.asList(490, 959, 2483));
            db.setDiseaseFolders(Collections.singletonList(4328));

            LOGGER.info("start");

//            final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("test3.wav"))));
            final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));

            final Map<Strain, AnalysisSummary> diseases = Machine.summarizeStrains(new SummaryCondition() {
                @Override
                public boolean test(Strain strain, AnalysisSummary summary) {

                    return summary.getDegree() == 0;
                }
            }, sample, db.getDiseaseIds());

            diseases.forEach(new BiConsumer<Strain, AnalysisSummary>() {
                @Override
                public void accept(Strain k, AnalysisSummary v) {
                    LOGGER.info("d: %s\t%d\n", k.getName(), v.getDegree());

                    analysisData.add(createDataTableObject(k,v));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DataTable createDataTableObject(Strain k, AnalysisSummary v) {
        DataTable dataTable = new DataTable();
        dataTable.setName(k.getName());
        dataTable.setDegree(v.getDegree());
        return dataTable;
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

    @Handler
    public void handleMessage(FileChooserEvent event) throws Exception {
        LOGGER.info(" GOT audio file for analyze ");
        file = event.getFile();
    }

    @Override
    public void subscribe() {
        EventBus.subscribe(this);
    }

}





