package com.mpsdevelopment.biopotential.server.gui.analysis;


import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp._SoundIO;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.KindCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.SummaryCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DB;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.gui.correctors.CorrectorsPanel;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
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
import javafx.scene.control.Button;
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

    @FXML
    private Button continueButton;

    private Stage primaryStage;
    private static File file;
    private static Map<Pattern, AnalysisSummary> healings;
    Map<Pattern, AnalysisSummary> allHealings = new HashMap<Pattern, AnalysisSummary>();


    public AnalysisPanelController() {
        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeAnalyze(file);

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
                property.setValue(String.format("%s", dataTable.getValue().getDispersion()));
                return property;
            }
        });

        continueButton.setOnAction(event -> {
            CorrectorsPanel panel = new CorrectorsPanel();
            Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Коррекция").setClazz(panel.getClass()).setHeight(722d).setWidth(1273d).setHeightPanel(722d).setWidthPanel(1273d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
            panel.setPrimaryStage(stage);


        });

        scatterChart.setTitle("Body Overview");

        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(15.0, 90));
        series1.getData().add(new XYChart.Data(2.8, 33.6));
        series1.getData().add(new XYChart.Data(1.8, 81.4));

        scatterChart.getStylesheets().add("scater.css");
        scatterChart.getData().addAll(series1);

    }

    private void makeAnalyze(File file) {
        /*try {

            final ArkDB db = new ArkDB("test.arkdb");
            db.setHealingFolders(Arrays.asList(490, 959, 2483));
            db.setDiseaseFolders(Collections.singletonList(4328));

            LOGGER.info("start");

//            final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("test3.wav"))));
            final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));

            final Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(new SummaryCondition() {
                @Override
                public boolean test(Pattern strain, AnalysisSummary summary) {

                    return summary.getDispersion() == 0;
                }
            }, sample, db.getDiseaseIds());

            diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                @Override
                public void accept(Pattern k, AnalysisSummary v) {
                    LOGGER.info("d: %s\t%d\n", k.getName(), v.getDispersion());

                    analysisData.add(createDataTableObject(k,v));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            final H2DB db = new H2DB("./data/database", "", "sa");

            System.out.println("start");

//            final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("test3.wav"))));

            final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));
            final Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(new SummaryCondition() {
                @Override
                public boolean test(Pattern strain, AnalysisSummary summary) {
                    return summary.getDegree() == 0 /*|| summary.getDispersion() == -21*/;
                }
            },sample, db.getDiseases());


            diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                @Override
                public void accept(Pattern k, AnalysisSummary v) {
                    System.out.printf("%s\t%f\n", k.getName(), v.getDispersion());
//                    LOGGER.info("d: %s\t%f\n", k.getName(), v.getDispersion());

                    analysisData.add(createDataTableObject(k,v));
                }
            });

            final Map<String, Integer> probableKinds = Machine.filterKinds(new KindCondition() {
                @Override
                public boolean test(String kind, int count) {
                    return count > 0;
                }
            }, diseases);

            diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                @Override
                public void accept(Pattern dk, AnalysisSummary dv) {
                    System.out.printf("heals for %s %s\n", dk.getKind(), dk.getName());
                    if (probableKinds.containsKey(dk.getKind())) {
                        /*final Map<Pattern, AnalysisSummary>*/
                        healings = Machine.summarizePatterns(new SummaryCondition() {
                            @Override
                            public boolean test(Pattern pattern, AnalysisSummary summary) { // и потом берутся только те которые summary.getDispersion() == 0 т.е. MAx
                                return summary.getDegree() == 0;
                            }
                        }, sample, db.getIterForFolder(((EDXPattern) dk).getCorrectingFolder())); // вытягиваются папка с коректорами для конкретной болезни BAC -> FL BAC


                        healings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                            @Override
                            public void accept(Pattern hk, AnalysisSummary hv) {
//                            hk.getPCMData()
                                LOGGER.info("%s %s\n", hk.getKind(), hk.getName(), hv.getDispersion());

                            }
                        });
                        allHealings.putAll(healings);
                    }
                }
            });
            LOGGER.info("healings size %s", allHealings.size());
            EventBus.publishEvent(new HealingsMapEvent(allHealings));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private DataTable createDataTableObject(Pattern k, AnalysisSummary v) {
        DataTable dataTable = new DataTable();
        dataTable.setName(k.getName());
        dataTable.setDispersion(v.getDispersion());
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

    /*@Override
    public void subscribe() {
        EventBus.subscribe(this);
    }*/

}





