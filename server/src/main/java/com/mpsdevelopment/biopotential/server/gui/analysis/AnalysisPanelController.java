package com.mpsdevelopment.biopotential.server.gui.analysis;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.db.pojo.HumanPoints;
import com.mpsdevelopment.biopotential.server.db.pojo.SystemDataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.gui.correctors.CorrectorsPanel;
import com.mpsdevelopment.biopotential.server.gui.service.AnalyzeService;
import com.mpsdevelopment.biopotential.server.settings.ConfigSettings;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.PanelUtils;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
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
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;
//@Component
public class AnalysisPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(AnalysisPanelController.class);
    private static final String STRESS = "stress/";
    private static final String COR_NOT_NULL = "corNotNull/";
    private static final String HIDDEN = "hidden/";
    private static final String GET_DISEAS = "/getDiseas";
    private static final String GET_HEALINGS = "/getHealingsMap";
    private ObservableList<DataTable> analysisData;
    private ObservableList<DataTable> analysisHiddenData;
    private ObservableList<String> level;

    @Autowired
    private AnalyzeService analyzeService;

    @Autowired
    private ConfigSettings configSettings;

    @FXML
    private ScatterChart<Number, Number> scatterChart;

    @FXML
    private TableView<DataTable> healthConditionTable;

    @FXML
    private TableView<DataTable> healthConditionStressTable;

    @FXML
    private TableView<SystemDataTable> overallTable;

    @FXML
    private TableColumn<DataTable, String> diseaseName;

    @FXML
    private TableColumn<DataTable, String> diseaseLevel;

    @FXML
    private TableColumn<DataTable, String> numberColumn;

    @FXML
    private TableColumn<DataTable, String> diseaseNameStress;

    @FXML
    private TableColumn<DataTable, String> diseaseLevelStress;

    @FXML
    private TableColumn<DataTable, String> numberColumnStress;

    @FXML
    private TableColumn numberSystemColumn;

    @FXML
    private TableColumn<SystemDataTable, String> systemColumn;

    @FXML
    private TableColumn<SystemDataTable, String> maxLevelColumn;

    @FXML
    private TableColumn<SystemDataTable, String> poLevelColumn;

    @FXML
    private TableColumn<SystemDataTable, String> descColumn;

    @FXML
    private TableColumn automaticsLevel;

    @FXML
    private TableColumn automaticsLevelStress;

    @FXML
    private Button continueButton;

    @FXML
    private Button printButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button hiddenButton;

    @FXML
    private Pane pane;

    @FXML
    private BarChart<Number, Number> histogramBarChart;

    private Stage primaryStage;
    private File file;
    private String gender;
    private Map<Pattern, AnalysisSummary> diseases;
    private Map<Pattern, AnalysisSummary> allHealings;

    private String degree1;
    private String degree2;
//    private ConfigSettings configSettings;

    private ObservableList<DataTable> analysisStressData;


    protected void setDegree2(String degree2) {
        this.degree2 = degree2;
    }

    public AnalysisPanelController() {
//        EventBus.subscribe(this);
        LOGGER.info("AnalysisPanelController created");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        configSettings = JettyServer.WEB_CONTEXT.getBean(ConfigSettings.class);
        /**
         * (system folder) analysisData - disease (patterns) from all folder's in which corrector's cell is not null (all folders which correcting sub-folder's )
         * (stress analyze) analysisStressData - disease (patterns) from folder's: Stress Analyze, Di Деструкция, Me Метаболизм, Bo Физ кондиции, Dt DETOKC
         * analysisHiddenData - disease (patterns) from  folder's: FL Ac Acariasis, FL Ba Bacteria, FL El Elementary, FL He Helminths, FL My Mycosis, FL Vi Virus, Fe Femely, Ma Man
         */
        analysisData = FXCollections.observableArrayList();
        analysisStressData = FXCollections.observableArrayList();
        analysisHiddenData = FXCollections.observableArrayList();
        level = FXCollections.observableArrayList();
        diseases = new HashMap<>();
        allHealings = new HashMap<>();

        healthConditionTable.setItems(analysisData);
        healthConditionStressTable.setItems(analysisStressData);

        hiddenButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HiddenPanel hiddenPanel = new HiddenPanel(analysisHiddenData);
                LOGGER.info(" Start Converter application");
                Stage mainPanelStage = StageUtils.createStage(null, hiddenPanel, new StageSettings().setPanelTitle("Скрытая таблица").setClazz(hiddenPanel.getClass()).setHeight(444d).setWidth(615d).setHeightPanel(444d).setWidthPanel(615d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                hiddenPanel.setPrimaryStage(mainPanelStage);
            }
        });



        printButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PanelUtils.saveToImage(pane, primaryStage);
//                saveToImage();
            }
        });
        numberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(healthConditionTable.getItems().indexOf(p.getValue()) + 1 + "");
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

        descColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SystemDataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SystemDataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getDescription()));

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

        automaticsLevel.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(p.getValue().getDegree());
            }
        });

        numberColumnStress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(healthConditionStressTable.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER;");

        diseaseNameStress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getName()));

                return property;
            }
        });

        diseaseLevelStress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getDispersion()));
                return property;
            }
        });

        automaticsLevelStress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(p.getValue().getDegree());
            }
        });

        // system table by percent
        numberSystemColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> p) {
//                return new SimpleStringProperty(p.getValue().getKey());
                return new ReadOnlyObjectWrapper(overallTable.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });

        systemColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SystemDataTable, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SystemDataTable, String> p) {
                return new SimpleStringProperty(p.getValue().getName());
            }
        });


        maxLevelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SystemDataTable, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SystemDataTable, String> p) {
                return new SimpleStringProperty(Double.toString(new BigDecimal(p.getValue().getMaxLevel()).setScale(2, RoundingMode.UP).doubleValue())+ " %");
            }
        });

        poLevelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SystemDataTable, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SystemDataTable, String> p) {
//                p.getValue().getKey().
                return new SimpleStringProperty(Double.toString(new BigDecimal(p.getValue().getPoLevel()).setScale(2, RoundingMode.UP).doubleValue())+ " %");
            }
        });

        continueButton.setOnAction(event -> {
            CorrectorsPanel panel = new CorrectorsPanel(allHealings);
            Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Коррекция").setClazz(panel.getClass()).setHeight(722d).setWidth(1273d)
                    .setHeightPanel(722d).setWidthPanel(1273d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
            panel.setPrimaryStage(stage);

        });

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
                LOGGER.info("Close AnalysisPanelController");
            }
        });


        /*scatterChart.setTitle("Body Overview");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(15.0, 90));
        series.getData().add(new XYChart.Data<>(2.8, 33.6));
        series.getData().add(new XYChart.Data<>(1.8, 81.4));

//        scatterChart.getStylesheets().add("scater.css");
        scatterChart.getData().addAll(series);

        for (XYChart.Data<Number, Number> item: series.getData()) {
                Node node = item.getNode() ;
                node.setCursor(Cursor.HAND);
                item.getNode().setOnMouseClicked((MouseEvent event) -> {
                    System.out.println("you clicked " + item.toString());
                });
            }
*/
    }

    public void makeCurrentAnalyze(File file) {
        try {
            makeAnalyze(file);
        } catch (UnsupportedAudioFileException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void makeAnalyze(File file) throws UnsupportedAudioFileException, IOException, SQLException {

        long t2 = System.currentTimeMillis();
        //----------------------------------- GET DISEASE'S-------------------------------------------------

        // get diseases for stress analyze
        // degree max
        // degree Po
        String urlMax = ControllerAPI.DISEAS_CONTROLLER + gender + STRESS + degree1 + GET_DISEAS;
        String urlPo = ControllerAPI.DISEAS_CONTROLLER + gender + STRESS + degree2 + GET_DISEAS;
        Map<Pattern, AnalysisSummary> diseasesStress = analyzeService.getDiseases(urlMax, urlPo, file);
        analyzeService.diseasToAnalysisData(diseasesStress, analysisStressData);

        HumanPanel humanPanel = new HumanPanel(diseasesStress);
        Stage humanStage = StageUtils.createStage(null, humanPanel, new StageSettings().setPanelTitle("Тело человека").setClazz(humanPanel.getClass()).setHeight(748d).setWidth(1470d)
                .setHeightPanel(748d).setWidthPanel(1470d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
        humanPanel.setPrimaryStage(humanStage);

        // get diseases for systems
        // degree max
        // degree Po
        urlMax = ControllerAPI.DISEAS_CONTROLLER + gender + COR_NOT_NULL + degree1 + GET_DISEAS;
        urlPo = ControllerAPI.DISEAS_CONTROLLER + gender + COR_NOT_NULL + degree2 + GET_DISEAS;
        Map<Pattern, AnalysisSummary> diseasesSystemsMax = analyzeService.getDiseasesByDegree(urlMax, file);
        Map<Pattern, AnalysisSummary> diseasesSystemsPo = analyzeService.getDiseasesByDegree(urlPo, file);
        Map<Pattern, AnalysisSummary> diseasesSystems = new HashMap<>();
        diseasesSystems.putAll(diseasesSystemsMax);
        diseasesSystems.putAll(diseasesSystemsPo);

        // sort by system folder
        analyzeService.sortBySystem(diseasesSystems);
        analyzeService.diseasToAnalysisData(diseasesSystems, analysisData);

        // get diseases for hidden
        // degree max
        // degree Po
        urlMax = ControllerAPI.DISEAS_CONTROLLER + gender + HIDDEN + degree1 + GET_DISEAS;
        urlPo = ControllerAPI.DISEAS_CONTROLLER + gender + HIDDEN + degree2 + GET_DISEAS;
        Map<Pattern, AnalysisSummary> diseasesHidden = analyzeService.getDiseases(urlMax, urlPo, file);
        analyzeService.diseasToAnalysisData(diseasesHidden, analysisHiddenData);
        LOGGER.info("Total time for calculate diseases %d ms", System.currentTimeMillis() - t2);

        //----------------------------------- GET HEALING'S-------------------------------------------------

        long t1 = System.currentTimeMillis();
        urlMax = ControllerAPI.DISEAS_CONTROLLER + gender + degree1 + GET_HEALINGS;
        urlPo = ControllerAPI.DISEAS_CONTROLLER + gender + degree2 + GET_HEALINGS;
        allHealings = analyzeService.getHealings(urlMax, urlPo, diseasesSystemsMax,diseasesSystemsPo);

        analyzeService.getPatternsSize();
        LOGGER.info("Total time for calculate healings %d ms", System.currentTimeMillis() - t1);
        LOGGER.info("healings size %s", allHealings.size());

        EventBus.publishEvent(new HealingsMapEvent(allHealings)); // publishEvent with allHealings, but it's not necessary cause handler not exist

        //---------------------------------- SORT ----------------------------------//
        ObservableList<DataTable> analysisDataByMax = FXCollections.observableArrayList();
        analyzeService.diseasToAnalysisData(diseasesSystemsMax,analysisDataByMax);
        Set<DataTable> sortedSelectedItemsByMax = analyzeService.sortSelected(analysisDataByMax);

        ObservableList<DataTable> analysisDataByPo = FXCollections.observableArrayList();
        analyzeService.diseasToAnalysisData(diseasesSystemsPo,analysisDataByPo);
        Set<DataTable> sortedSelectedItemsByPo = analyzeService.sortSelected(analysisDataByPo);
        //---------------------------------- SORT ----------------------------------//

        //---------------------------------- MAP FOR BAR CHART-----------------------//
        Map<String, Double> systemMapMax = analyzeService.getSystemMap(sortedSelectedItemsByMax);
        Map<String, Double> systemMapPo = analyzeService.getSystemMap(sortedSelectedItemsByPo);
        //---------------------------------- MAP FOR BAR CHART-----------------------//

        ObservableList<SystemDataTable> diseaseSystemTable = FXCollections.observableArrayList();
        diseaseSystemTable.addAll(SystemDataTable.createDataTableObject(systemMapMax,systemMapPo));

        BarChartPanel panel = new BarChartPanel(systemMapMax,systemMapPo);
        Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("График состояния").setClazz(panel.getClass()).setHeight(815d).setWidth(1308d).setHeightPanel(815d).setWidthPanel(1308d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
        panel.setPrimaryStage(stage);

        String[] systems = {configSettings.getLiteral1(),configSettings.getLiteral2(),configSettings.getLiteral3(),configSettings.getLiteral4(), configSettings.getLiteral5(),
                configSettings.getLiteral6(), "Me", configSettings.getLiteral7(), configSettings.getLiteral8(), configSettings.getLiteral9(),
                configSettings.getLiteral10(), configSettings.getLiteral11(), configSettings.getLiteral12()};
        ObservableList<XYChart.Series<Number, Number>> barChartData = FXCollections.observableArrayList(
                new BarChart.Series("Max", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], systemMapMax.get(systems[0])),
                        new BarChart.Data(systems[1], systemMapMax.get(systems[1])),
                        new BarChart.Data(systems[2], systemMapMax.get(systems[2])),
                        new BarChart.Data(systems[3], systemMapMax.get(systems[3])),
                        new BarChart.Data(systems[4], systemMapMax.get(systems[4])),
                        new BarChart.Data(systems[5], systemMapMax.get(systems[5])),
                        new BarChart.Data(systems[6], systemMapMax.get(systems[6])),
                        new BarChart.Data(systems[7], systemMapMax.get(systems[7])),
                        new BarChart.Data(systems[8], systemMapMax.get(systems[8])),
                        new BarChart.Data(systems[9], systemMapMax.get(systems[9])),
                        new BarChart.Data(systems[10], systemMapMax.get(systems[10])),
                        new BarChart.Data(systems[11], systemMapMax.get(systems[11])),
                        new BarChart.Data(systems[12], systemMapMax.get(systems[12]))
                )),
                new BarChart.Series("Po", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], systemMapPo.get(systems[0])),
                        new BarChart.Data(systems[1], systemMapPo.get(systems[1])),
                        new BarChart.Data(systems[2], systemMapPo.get(systems[2])),
                        new BarChart.Data(systems[3], systemMapPo.get(systems[3])),
                        new BarChart.Data(systems[4], systemMapPo.get(systems[4])),
                        new BarChart.Data(systems[5], systemMapPo.get(systems[5])),
                        new BarChart.Data(systems[6], systemMapPo.get(systems[6])),
                        new BarChart.Data(systems[7], systemMapPo.get(systems[7])),
                        new BarChart.Data(systems[8], systemMapPo.get(systems[8])),
                        new BarChart.Data(systems[9], systemMapPo.get(systems[9])),
                        new BarChart.Data(systems[10], systemMapPo.get(systems[10])),
                        new BarChart.Data(systems[11], systemMapPo.get(systems[11])),
                        new BarChart.Data(systems[12], systemMapPo.get(systems[12]))

                )));


        histogramBarChart.getYAxis().setLabel("%"); // set % on Y Axis
        histogramBarChart.getData().addAll(barChartData);
        histogramBarChart.setBarGap(0.0);   // set gap between Bar's

        // change color for bar's
        for (int i = 0; i < barChartData.size(); i++) {
            for (Node node : histogramBarChart.lookupAll(".series" + i)) {
                node.getStyleClass().add("default-color" + i);
            }
        }
        histogramBarChart.getStylesheets().add("barchart.css");
        histogramBarChart.setStyle("-fx-border-color: #75c4ff;");

        //--------------------- Section with Tables----------------//
        overallTable.setItems(diseaseSystemTable);
        systemColumn.setSortable(true);
        overallTable.getSortOrder().add(systemColumn); // sort cell'a by name
        descColumn.setSortable(true);
        overallTable.getSortOrder().add(descColumn); // sort cell'a by name

        healthConditionTable.getSortOrder().add(automaticsLevel); // sort cell'a by name
        automaticsLevel.setSortable(true);
        automaticsLevel.setSortType(TableColumn.SortType.ASCENDING);

        diseaseName.setSortable(true);
        diseaseName.setSortType(TableColumn.SortType.ASCENDING);
        healthConditionTable.getSortOrder().add(diseaseName); // sort cell'a by name
        healthConditionTable.setItems(analysisData);

        healthConditionStressTable.getSortOrder().add(automaticsLevelStress);
        diseaseNameStress.setSortable(true);
        diseaseNameStress.setSortType(TableColumn.SortType.ASCENDING);
        healthConditionStressTable.getSortOrder().add(diseaseNameStress); // sort cell'a by name

        automaticsLevelStress.setSortable(true);
        automaticsLevelStress.setSortType(TableColumn.SortType.ASCENDING);
        healthConditionStressTable.setItems(analysisStressData);

        LOGGER.info("Time for make analysis %s ms", System.currentTimeMillis());

    }



    public void updatePanel(Stage primaryStage) {
        LOGGER.info("update Analysis panel");
        this.primaryStage = primaryStage;
        this.primaryStage.getScene().getStylesheets().add("table.css");

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
    public void subscribe() { EventBus.subscribe(this); }*/

    public void setFile(File file) {
        this.file = file;
    }

    void setDegree1(String degree1) {
        this.degree1 = degree1;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}

