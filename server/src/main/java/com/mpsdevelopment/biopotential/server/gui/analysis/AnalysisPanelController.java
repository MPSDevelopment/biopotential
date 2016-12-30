package com.mpsdevelopment.biopotential.server.gui.analysis;

import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.pojo.SystemDataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.gui.converter.ConverterPanel;
import com.mpsdevelopment.biopotential.server.gui.correctors.CorrectorsPanel;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.httpclient.HttpClientFactory;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
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
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;

public class AnalysisPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(AnalysisPanelController.class);
    public static final int patternWeight = 10;
    public static final String STRESS = "stress";
    public static final String COR_NOT_NULL = "corNotNull";
    public static final String HIDDEN = "hidden";
    private ObservableList<DataTable> analysisData;
    private ObservableList<DataTable> analysisHiddenData;
    ObservableList<DataTable> data = FXCollections.observableArrayList();
    private ObservableList<String> level;

    @FXML
    private ScatterChart<Number, Number> scatterChart;

    @FXML
    private TableView<DataTable> healthConditionTable;

    @FXML
    private TableView<DataTable> healthConditionStressTable;

    @FXML
//    private TableView<Map.Entry<String,Integer>> systemTable;
    private TableView<SystemDataTable> systemTable;

    @FXML
    private TableColumn<DataTable, String> diseaseName;

    @FXML
    private TableColumn<DataTable, String> diseaseLevel;

    @FXML
    private TableColumn<DataTable, String> numberColumn;

    @FXML
    private TableColumn<DataTable, String> diseaseName1;

    @FXML
    private TableColumn<DataTable, String> diseaseLevel1;

    @FXML
    private TableColumn<DataTable, String> numberColumn1;

    @FXML
    private TableColumn numberSystemColumn;

    @FXML
//    private TableColumn<Map.Entry<String, Integer>, String> systemColumn;
    private TableColumn<SystemDataTable, String> systemColumn;

    @FXML
    private TableColumn<SystemDataTable, String> maxLevelColumn;

    @FXML
    private TableColumn<SystemDataTable, String> poLevelColumn;

    @FXML
    private TableColumn automaticsLevelColumn;

    @FXML
    private TableColumn automaticsLevelColumn1;

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

    private static File outputFile = new File("AudioFiles\\out\\out.wav");
    private String degree1;
    private String degree2;
    private SystemDataTable systemDataTable;

    public static final String HOST = "localhost";
    public static final int PORT = 8098;
    private double alWeight = 0, viWeight = 0, caWeight = 0, deWeight = 0, enWeight = 0, gaWeight = 0, imWeight = 0, neWeight = 0, orWeight = 0, spWeight = 0, stWeight = 0, urWeight = 0;
    private ObservableList<DataTable> analysisStressData;


    public void setDegree2(String degree2) {
        this.degree2 = degree2;
    }

    public AnalysisPanelController() {
//        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
//                HiddenPanel hiddenPanel = new HiddenPanel(file,"Max","Po");
                HiddenPanel hiddenPanel = new HiddenPanel(analysisHiddenData);
                LOGGER.info(" Start Converter application");
                Stage mainPanelStage = StageUtils.createStage(null, hiddenPanel, new StageSettings().setPanelTitle("Скрытая таблица").setClazz(hiddenPanel.getClass()).setHeight(444d).setWidth(615d).setHeightPanel(444d).setWidthPanel(615d)/*.setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY())*/);
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

        numberColumn1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(healthConditionStressTable.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER;");

        diseaseName1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getName()));

                return property;
            }
        });
        diseaseLevel1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getDispersion()));
                return property;
            }
        });

        automaticsLevelColumn1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
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
                return new ReadOnlyObjectWrapper(systemTable.getItems().indexOf(p.getValue()) + 1 + "");
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
            }
        });

        scatterChart.setTitle("Body Overview");

        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(15.0, 90));
        series1.getData().add(new XYChart.Data(2.8, 33.6));
        series1.getData().add(new XYChart.Data(1.8, 81.4));

        scatterChart.getStylesheets().add("scater.css");
        scatterChart.getData().addAll(series1);

        automaticsLevelColumn.setSortable(true);
        automaticsLevelColumn1.setSortable(true);
        healthConditionTable.getSortOrder().add(automaticsLevelColumn); // sort cell'a by name
        healthConditionStressTable.getSortOrder().add(automaticsLevelColumn1); // sort cell'a by name

    }

    /*private void saveToImage() {
        Image image = pane.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            System.out.println(e);
        }
    }*/

    private void makeAnalyze(File file) throws UnsupportedAudioFileException, IOException, SQLException {

        automaticsLevelColumn.setSortable(true);
        healthConditionTable.getSortOrder().add(automaticsLevelColumn); // sort cell'a by name

        long t2 = System.currentTimeMillis();
        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();

        // get diseas for stress analyze
        // degree max
        String json = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + STRESS + "/" + degree1 + "/getDiseas" , file);

        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();
        Map<Pattern, AnalysisSummary> diseasesStress = new HashMap<>();
        Map<Pattern, AnalysisSummary> diseasesStressMax = JsonUtils.fromJson(typeOfHashMap, json);

        //degree Po
        json = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + STRESS + "/" + degree2 + "/getDiseas" , file);
        Map<Pattern, AnalysisSummary> diseasesStressPo = JsonUtils.fromJson(typeOfHashMap, json);
        diseasesStress.putAll(diseasesStressMax);
        diseasesStress.putAll(diseasesStressPo);
        diseasToAnalysisData(diseasesStress, analysisStressData);

        // get diseas for systems
        // degree max
        json = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + COR_NOT_NULL + "/" + degree1 + "/getDiseas", file);
        Map<Pattern, AnalysisSummary> diseasesSystems = new HashMap<>();
        Map<Pattern, AnalysisSummary> diseasesSystemsMax = JsonUtils.fromJson(typeOfHashMap, json);

        //degree Po
        json = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + COR_NOT_NULL + "/" + degree2 + "/getDiseas", file);
        Map<Pattern, AnalysisSummary> diseasesSystemsPo = JsonUtils.fromJson(typeOfHashMap, json);

        diseasesSystems.putAll(diseasesSystemsMax);
        diseasesSystems.putAll(diseasesSystemsPo);

        Map<Pattern, AnalysisSummary> diseasesTemp = new HashMap<>();

        diseasesSystems.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                if (!(pattern.getKind().equals("Dt DETOKC")) &&!(pattern.getKind().equals("Bо Физ кодиции")) && !(pattern.getKind().equals("FL Ac Acariasis")) && !(pattern.getKind().equals("FL Ba Bacteria")) && !(pattern.getKind().equals("FL El Elementary")) &&
                        !(pattern.getKind().equals("FL He Helminths")) && !(pattern.getKind().equals("FL My Mycosis")) && !(pattern.getKind().equals("FL Vi Virus")) && !(pattern.getKind().equals("Fe Femely")) &&
                            !(pattern.getKind().equals("Ma Man"))) {
                    diseasesTemp.put(pattern, analysisSummary);
                }
            }
        });
        diseasesSystems.clear();
        diseasesSystems.putAll(diseasesTemp);

        diseasToAnalysisData(diseasesSystems, analysisData);

        // get diseas for hidden
        // degree max
        json = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + HIDDEN + "/" + degree1 + "/getDiseas", file);
        Map<Pattern, AnalysisSummary> diseasesHiden = new HashMap<>();
        Map<Pattern, AnalysisSummary> diseasesHidenMax = JsonUtils.fromJson(typeOfHashMap, json);

        //degree Po
        json = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + HIDDEN + "/" + degree2 + "/getDiseas", file);
        Map<Pattern, AnalysisSummary> diseasesHidenPo = JsonUtils.fromJson(typeOfHashMap, json);

        diseasesHiden.putAll(diseasesHidenMax);
        diseasesHiden.putAll(diseasesHidenPo);
        diseasToAnalysisData(diseasesHiden, analysisHiddenData);


        // Healings
        LOGGER.info("Total time for calculate diseases %d ms", System.currentTimeMillis() - t2);
        long t1 = System.currentTimeMillis();

        String heal = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + COR_NOT_NULL + "/"+ degree1 + "/getHealings",file);
        typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();
        allHealings = JsonUtils.fromJson(typeOfHashMap, heal);

        //
        heal = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + COR_NOT_NULL + "/" + degree2 + "/getHealings",file);
        Map<Pattern, AnalysisSummary> allHealings1 = JsonUtils.fromJson(typeOfHashMap, heal);
        allHealings.putAll(allHealings1);
        //

        Type type = new TypeToken<Map<String, Integer>>() { }.getType();
        Map<String,Integer> sizeMap = new HashMap<>();

        String url = String.format("http://%s:%s%s", HOST, PORT, ControllerAPI.PATTERNS_CONTROLLER + ControllerAPI.PATTERNS_CONTROLLER_GET_PATTERNS_SIZE);
        String size = bioHttpClient.executeGetRequest(url);

        sizeMap = JsonUtils.fromJson(type,size);

        sizeMap.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer weight) {
                if (s.contains("Al ALLERGY")) {
                    alWeight = (double) 100/weight;
                }
                if (s.contains("Ca CARDIO")) {
                    caWeight = (double) 100/weight;
                }
                if (s.contains("De DERMA")) {
                    deWeight = (double) 100/weight;
                }
                if (s.contains("En ENDOKRIN")) {
                    enWeight = (double) 100/weight;
                }
                if (s.contains("Ga GASTRO")) {
                    gaWeight = (double) 100/weight;
                }
                if (s.contains("Im IMMUN")) {
                    imWeight = (double) 100/weight;
                }
                if (s.contains("Ne NEURAL")) {
                    neWeight = (double) 100/weight;
                }
                if (s.contains("Or ORTHO")) {
                    orWeight = (double) 100/weight;
                }
                if (s.contains("Sp SPIRITUS")) {
                    spWeight = (double) 100/weight;
                }
                if (s.contains("St STOMAT")) {
                    stWeight = (double) 100/weight;
                }
                if (s.contains("Ur UROLOG")) {
                    urWeight = (double) 100/weight;
                }
                if (s.contains("Vi VISION")) {
                   viWeight = (double) 100/weight;
                }
            }
        });

//        allHealings.putAll(diseaseDao.getHealings(diseases, file));
        LOGGER.info("Total time for calculate healings %d ms", System.currentTimeMillis() - t1);
        LOGGER.info("healings size %s", allHealings.size());
        EventBus.publishEvent(new HealingsMapEvent(allHealings));

        // sortedSelectedItems set with contains system which diseas appear > than 1 time

        ObservableList<DataTable> analysisData1 = FXCollections.observableArrayList();
        diseasToAnalysisData(diseasesSystemsMax,analysisData1);
        Set<DataTable> sortedSelectedItems1 = sortSelected(analysisData1);

        ObservableList<DataTable> analysisData2 = FXCollections.observableArrayList();
        diseasToAnalysisData(diseasesSystemsPo, analysisData2);
        Set<DataTable> sortedSelectedItems2 = sortSelected(analysisData2);


        Map<String, Double> systemMap1 = getSystemMap(sortedSelectedItems1);
        Map<String, Double> systemMap2 = getSystemMap(sortedSelectedItems2);
        /*List<SystemDataTable> systemDataTables = new ArrayList<>();
        systemDataTable = null;

        systemMap1.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer integer) {
                systemDataTable = new SystemDataTable();
                systemDataTable.setName(s);
                systemDataTable.setMaxLevel(integer);
                systemMap2.forEach(new BiConsumer<String, Integer>() {
                    @Override
                    public void accept(String ss, Integer integer1) {
                        if (s.equals(ss)) {
                            systemDataTable.setPoLevel(integer1);
                        }
                    }
                });
                systemDataTables.add(systemDataTable);
            }
        });*/

        ObservableList<SystemDataTable> datas = FXCollections.observableArrayList();
        datas.addAll(SystemDataTable.createDataTableObject(systemMap1,systemMap2));

        BarChartPanel panel = new BarChartPanel(systemMap1,systemMap2);
        Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Bar chart").setClazz(panel.getClass()).setHeight(752d).setWidth(1273d).setHeightPanel(722d).setWidthPanel(1273d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
        panel.setPrimaryStage(stage);

        String[] systems = {"ALLERGY система","CARDIO система","DERMA система","Endocrinology система", "GASTRO система", "IMMUN система", "MENTIS система", "NEURAL система", "ORTHO система",
                "SPIRITUS система", "Stomat система", "UROLOG система", "VISION система"};
        ObservableList<XYChart.Series<Number, Number>> barChartData = FXCollections.observableArrayList(
                new BarChart.Series("Max", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], systemMap1.get("ALLERGY система")),
                        new BarChart.Data(systems[1], systemMap1.get("CARDIO система")),
                        new BarChart.Data(systems[2], systemMap1.get("DERMA система")),
                        new BarChart.Data(systems[3], systemMap1.get("Endocrinology система")),
                        new BarChart.Data(systems[4], systemMap1.get("GASTRO система")),
                        new BarChart.Data(systems[5], systemMap1.get("IMMUN система")),
                        new BarChart.Data(systems[6], systemMap1.get("MENTIS система")),
                        new BarChart.Data(systems[7], systemMap1.get("NEURAL система")),
                        new BarChart.Data(systems[8], systemMap1.get("ORTHO система")),
                        new BarChart.Data(systems[9], systemMap1.get("SPIRITUS система")),
                        new BarChart.Data(systems[10], systemMap1.get("Stomat система")),
                        new BarChart.Data(systems[11], systemMap1.get("UROLOG система")),
                        new BarChart.Data(systems[12], systemMap1.get("VISION система"))
                )),
                new BarChart.Series("Po", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], systemMap2.get("ALLERGY система")),
                        new BarChart.Data(systems[1], systemMap2.get("CARDIO система")),
                        new BarChart.Data(systems[2], systemMap2.get("DERMA система")),
                        new BarChart.Data(systems[3], systemMap2.get("Endocrinology система")),
                        new BarChart.Data(systems[4], systemMap2.get("GASTRO система")),
                        new BarChart.Data(systems[5], systemMap2.get("IMMUN система")),
                        new BarChart.Data(systems[6], systemMap2.get("MENTIS система")),
                        new BarChart.Data(systems[7], systemMap2.get("NEURAL система")),
                        new BarChart.Data(systems[8], systemMap2.get("ORTHO система")),
                        new BarChart.Data(systems[9], systemMap2.get("SPIRITUS система")),
                        new BarChart.Data(systems[10], systemMap2.get("Stomat система")),
                        new BarChart.Data(systems[11], systemMap2.get("UROLOG система")),
                        new BarChart.Data(systems[12], systemMap2.get("VISION система"))

                )));

        histogramBarChart.getYAxis().setLabel("%");
        histogramBarChart.getYAxis().setStyle("-fx-fill: #171eb2;");
        histogramBarChart.getData().addAll(barChartData);

        /*ObservableList<Map.Entry<String,Integer>> result1 = FXCollections.observableArrayList(systemMap1.entrySet());
        systemTable.setItems(result1);*/
        systemTable.setItems(datas);
        systemColumn.setSortable(true);
        systemTable.getSortOrder().add(systemColumn); // sort cell'a by name

        healthConditionTable.setItems(analysisData);
        automaticsLevelColumn.setSortable(true);
        healthConditionTable.getSortOrder().add(automaticsLevelColumn); // sort cell'a by name

    }

    private Map<String, Double> getSystemMap(Set<DataTable> sortedSelectedItems) {
        Map<String,Double> systemMap = new HashMap<>();
        systemMap.put("ALLERGY система",0d);
        systemMap.put("CARDIO система",0d);
        systemMap.put("DERMA система",0d);
        systemMap.put("Endocrinology система",0d);
        systemMap.put("GASTRO система",0d);
        systemMap.put("IMMUN система",0d);
        systemMap.put("MENTIS система",0d);
        systemMap.put("NEURAL система",0d);
        systemMap.put("ORTHO система",0d);
        systemMap.put("SPIRITUS система",0d);
        systemMap.put("Stomat система",0d);
        systemMap.put("UROLOG система",0d);
        systemMap.put("VISION система",0d);

        // decode diseas names to system's name's
        int index = 0;
        for (DataTable dataTable: sortedSelectedItems) {
            for (int i = 0 ; i < dataTable.getName().length(); i++) {
                if ((dataTable.getName().charAt(i) == '♥') || (dataTable.getName().charAt(i) == 'ლ') || (dataTable.getName().charAt(i) == '♋') || (dataTable.getName().charAt(i) == '⌘')
                        || (dataTable.getName().charAt(i) == '☂') || (dataTable.getName().charAt(i) == '☺') || (dataTable.getName().charAt(i) == '♕') || (dataTable.getName().charAt(i) == '☤')
                        || (dataTable.getName().charAt(i) == '✽') || (dataTable.getName().charAt(i) == '〲') || (dataTable.getName().charAt(i) == 'Ü') || (dataTable.getName().charAt(i) == '☄')) {
                    index = i;
                    break;
                }


            }

            switch (dataTable.getName().substring(0, 2)) {
                case "AL":
                    systemMap.put("ALLERGY система",systemMap.get("ALLERGY система")+alWeight);
                    break;
                case "Ca":
                    systemMap.put("CARDIO система",systemMap.get("CARDIO система")+caWeight);
                    break;
                case "De":
                    systemMap.put("DERMA система",systemMap.get("DERMA система")+deWeight);
                    break;
                case "En":
                    systemMap.put("Endocrinology система",systemMap.get("Endocrinology система")+deWeight);
                    break;
                case "Ga":
                    systemMap.put("GASTRO система",systemMap.get("GASTRO система")+gaWeight);
                    break;
                case "Im":
                    systemMap.put("IMMUN система",systemMap.get("IMMUN система")+imWeight);
                    break;
                case "Ne":
                    systemMap.put("NEURAL система",systemMap.get("NEURAL система")+neWeight);
                    break;
                case "Or":
                    systemMap.put("ORTHO система",systemMap.get("ORTHO система")+orWeight);
                    break;
                case "Sp":
                    systemMap.put("SPIRITUS система",systemMap.get("SPIRITUS система")+spWeight);
                    break;
                case "St":
                    systemMap.put("Stomat система",systemMap.get("Stomat система")+stWeight);
                    break;
                case "Ur":
                    systemMap.put("UROLOG система",systemMap.get("UROLOG система")+urWeight);
                    break;
                case "Vi":
                    systemMap.put("VISION система",systemMap.get("VISION система")+viWeight);
                    break;
            }

            /*switch (dataTable.getName().substring(0, index)) {

                case "CARDIO":
                    systemMap.put("CARDIO система",systemMap.get("CARDIO система")+patternWeight);
                    break;
                case "DERMA":
                    systemMap.put("DERMA система",systemMap.get("DERMA система")+patternWeight);
                    break;
                case "Endocrinology":
                    systemMap.put("Endocrinology система",systemMap.get("Endocrinology система")+patternWeight);
                    break;
                case "GASTRO":
                    systemMap.put("GASTRO система",systemMap.get("GASTRO система")+ patternWeight);
                    break;
                case "IMMUN":
                    systemMap.put("IMMUN система",systemMap.get("IMMUN система")+ patternWeight);
                    break;
                case "MENTIS":
                    systemMap.put("MENTIS система",systemMap.get("MENTIS система")+ patternWeight);
                    break;
                case "NEURAL":
                    systemMap.put("NEURAL система",systemMap.get("NEURAL система")+ patternWeight);
                    break;
                case "ORTHO":
                    systemMap.put("ORTHO система",systemMap.get("ORTHO система")+ patternWeight);
                    break;
                case "SPIRITUS":
                    systemMap.put("SPIRITUS система",systemMap.get("SPIRITUS система")+ patternWeight);
                    break;
                case "Stomat":
                    systemMap.put("Stomat система",systemMap.get("Stomat система")+ patternWeight);
                    break;
                case "UROLOG":
                    systemMap.put("UROLOG система",systemMap.get("UROLOG система")+ patternWeight);
                    break;
                case "VISION":
                    systemMap.put("VISION система",systemMap.get("VISION система")+ patternWeight);
                    break;
            }*/

        }
        return systemMap;
    }

    private Set<DataTable> sortSelected(ObservableList<DataTable> data) {
        Set<DataTable> sortedSelectedItems = new HashSet<>();
        char [] str = new char[5];
        char [] cmp = new char[5];
        // check how many diseas from same system have been received from analysis
        for (DataTable dataTable: data) {
            int count=0;
            dataTable.getName().getChars(0,4,str,0);
            for (DataTable temp: data) {
                temp.getName().getChars(0,4,cmp,0);
                if(Arrays.equals(str,cmp)){
                    count++;
                }
//                if (count > 1) {
                    sortedSelectedItems.add(dataTable);
//                }
            }
        }
        return sortedSelectedItems;
    }

    private ObservableList<DataTable> diseasToAnalysisData(Map<Pattern, AnalysisSummary> map, ObservableList<DataTable> analysisData) {

        map.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern k, AnalysisSummary v) {
                LOGGER.info("d: %s\t%f\n", k.getName(), v.getDispersion());
                analysisData.add(DataTable.createDataTableObject(k, v));
            }
        });
        return analysisData;
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
    public void subscribe() { EventBus.subscribe(this); }*/

    public void setFile(File file) {
        this.file = file;
    }

    public void makeCurrentAnalyze(File file) {
        try {
            makeAnalyze(file);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDegree1(String degree1) {
        this.degree1 = degree1;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
