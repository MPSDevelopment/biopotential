package com.mpsdevelopment.biopotential.server.gui.analysis;

import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.pojo.CodeTable;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.db.pojo.SystemDataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.gui.ModalWindow;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;

import javax.sound.sampled.UnsupportedAudioFileException;
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

    private static final int AVAILABLE_COLORS = 10;
    private static final int CASPIAN_COLOR_COUNTS = 8;

    private static final Logger LOGGER = LoggerUtil.getLogger(AnalysisPanelController.class);
    public static final int patternWeight = 10;
    private static final String STRESS = "stress";
    private static final String COR_NOT_NULL = "corNotNull";
    private static final String HIDDEN = "hidden";
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
    private TableView<SystemDataTable> systemTable;

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

    private static File outputFile = new File("AudioFiles\\out\\out.wav");
    private String degree1;
    private String degree2;
    private SystemDataTable systemDataTable;

    private static final String HOST = "localhost";
    private static final int PORT = 8098;
    private double alWeight = 0, viWeight = 0, caWeight = 0, deWeight = 0, enWeight = 0, gaWeight = 0, imWeight = 0, neWeight = 0, orWeight = 0, spWeight = 0, stWeight = 0, urWeight = 0;
    private ObservableList<DataTable> analysisStressData;


    protected void setDegree2(String degree2) {
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

        /*codename.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CodeTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CodeTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getCodename()));

                return property;
            }
        });*/

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

        /*automaticsLevel.setSortable(true);
        automaticsLevelStress.setSortable(true);
        healthConditionTable.getSortOrder().add(automaticsLevel); // sort cell'a by name*/


        /*healthConditionStressTable.getSortOrder().clear();
        healthConditionStressTable.getSortOrder().add(diseaseNameStress);
        diseaseNameStress.setSortType(TableColumn.SortType.ASCENDING);
        healthConditionStressTable.getSortOrder().add(diseaseNameStress); // sort cell'a by name
        healthConditionStressTable.sort();*/

        /*SortedList<DataTable> sortedList = new SortedList<>(analysisStressData,
                (DataTable stock1, DataTable stock2) -> {
                   return stock1.getName().compareTo(stock2.getName());
                });

        healthConditionStressTable.setItems(sortedList);*/

//        diseaseNameStress.setSortable(true);
//        healthConditionStressTable.getSortOrder().add(diseaseNameStress);
        /*diseaseNameStress.setSortType(TableColumn.SortType.ASCENDING);
        diseaseNameStress.setComparator(new myComp());
        healthConditionStressTable.getSortOrder().setAll(diseaseNameStress);*/

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

        String url = String.format("http://%s:%s%s", HOST, PORT, ControllerAPI.PATTERNS_CONTROLLER + ControllerAPI.PATTERNS_CONTROLLER_GET_PATTERNS_SIZE);
        String size = bioHttpClient.executeGetRequest(url);

        Map<String,Integer> sizeMap = JsonUtils.fromJson(type,size);

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

        Map<String, String> codeMap = new HashMap<>();
        codeMap.put("AL", "ALLERGY система");
        codeMap.put("CA", "CARDIO система");
        codeMap.put("DE","DERMA система");
        codeMap.put("En","Endocrinology система");
        codeMap.put("GA","GASTRO система");
        codeMap.put("IM","IMMUN система");
        codeMap.put("ME","MENTIS система");
        codeMap.put("NE","NEURAL система");
        codeMap.put("OR","ORTHO система");
        codeMap.put("SP","SPIRITUS система");
        codeMap.put("St","Stomat система");
        codeMap.put("UR","UROLOG система");
        codeMap.put("VI","VISION система");

        /*ObservableList<CodeTable> datacode = FXCollections.observableArrayList();
        datacode.addAll(CodeTable.createDataTableObject(codeMap));
        codeTable.setItems(datacode);
        codename.setSortable(true);
        codeTable.getSortOrder().add(codename); // sort cell'a by name*/

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
                systemDataTable.setCodename(s);
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
        Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Bar chart").setClazz(panel.getClass()).setHeight(815d).setWidth(1308d).setHeightPanel(815d).setWidthPanel(1308d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
        panel.setPrimaryStage(stage);

        String[] systems = {"AL","CA","DE","En", "GA", "IM", "ME", "NE", "OR", "SP", "St", "UR", "VI"};
        ObservableList<XYChart.Series<Number, Number>> barChartData = FXCollections.observableArrayList(
                new BarChart.Series("Max", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], systemMap1.get("AL")),
                        new BarChart.Data(systems[1], systemMap1.get("CA")),
                        new BarChart.Data(systems[2], systemMap1.get("DE")),
                        new BarChart.Data(systems[3], systemMap1.get("En")),
                        new BarChart.Data(systems[4], systemMap1.get("GA")),
                        new BarChart.Data(systems[5], systemMap1.get("IM")),
                        new BarChart.Data(systems[6], systemMap1.get("ME")),
                        new BarChart.Data(systems[7], systemMap1.get("NE")),
                        new BarChart.Data(systems[8], systemMap1.get("OR")),
                        new BarChart.Data(systems[9], systemMap1.get("SP")),
                        new BarChart.Data(systems[10], systemMap1.get("St")),
                        new BarChart.Data(systems[11], systemMap1.get("UR")),
                        new BarChart.Data(systems[12], systemMap1.get("VI"))
                )),
                new BarChart.Series("Po", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], systemMap2.get("AL")),
                        new BarChart.Data(systems[1], systemMap2.get("CA")),
                        new BarChart.Data(systems[2], systemMap2.get("DE")),
                        new BarChart.Data(systems[3], systemMap2.get("En")),
                        new BarChart.Data(systems[4], systemMap2.get("GA")),
                        new BarChart.Data(systems[5], systemMap2.get("IM")),
                        new BarChart.Data(systems[6], systemMap2.get("ME")),
                        new BarChart.Data(systems[7], systemMap2.get("NE")),
                        new BarChart.Data(systems[8], systemMap2.get("OR")),
                        new BarChart.Data(systems[9], systemMap2.get("SP")),
                        new BarChart.Data(systems[10], systemMap2.get("St")),
                        new BarChart.Data(systems[11], systemMap2.get("UR")),
                        new BarChart.Data(systems[12], systemMap2.get("VI"))

                )));


        histogramBarChart.getYAxis().setLabel("%");
        histogramBarChart.getData().addAll(barChartData);
        histogramBarChart.setBarGap(0.0);

        for (int i = 0; i < barChartData.size(); i++) {
            for (Node node : histogramBarChart.lookupAll(".series" + i)) {
                node.getStyleClass().remove("default-color" + (i % CASPIAN_COLOR_COUNTS));
                node.getStyleClass().add("default-color" + (i % AVAILABLE_COLORS));
            }
        }
        histogramBarChart.getStylesheets().add("barchart.css");

        histogramBarChart.setStyle("-fx-border-color: #75c4ff;");
//        primaryStage.getScene().getStylesheets().add("table.css");
//        histogramBarChart.getYAxis().setStyle("-fx-fill: #171eb2;");

        /*ObservableList<Map.Entry<String,Integer>> result1 = FXCollections.observableArrayList(systemMap1.entrySet());
        systemTable.setItems(result1);*/

        systemTable.setItems(datas);
        systemColumn.setSortable(true);
        systemTable.getSortOrder().add(systemColumn); // sort cell'a by name
        descColumn.setSortable(true);
        systemTable.getSortOrder().add(descColumn); // sort cell'a by system's name



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
//        healthConditionStressTable.getSortOrder().add(automaticsLevelStress); // sort cell'a by name

        healthConditionStressTable.setItems(analysisStressData);



//        healthConditionTable.getStylesheets().add("table.css");

    }

    private Map<String, Double> getSystemMap(Set<DataTable> sortedSelectedItems) {
        Map<String,Double> systemMap = new HashMap<>();
        systemMap.put("AL",0d);
        systemMap.put("CA",0d);
        systemMap.put("DE",0d);
        systemMap.put("En",0d);
        systemMap.put("GA",0d);
        systemMap.put("IM",0d);
        systemMap.put("ME",0d);
        systemMap.put("NE",0d);
        systemMap.put("OR",0d);
        systemMap.put("SP",0d);
        systemMap.put("St",0d);
        systemMap.put("UR",0d);
        systemMap.put("VI",0d);

        // decode diseas names to system's name's
        for (DataTable dataTable: sortedSelectedItems) {
            for (int i = 0 ; i < dataTable.getName().length(); i++) {
                if ((dataTable.getName().charAt(i) == '♥') || (dataTable.getName().charAt(i) == 'ლ') || (dataTable.getName().charAt(i) == '♋') || (dataTable.getName().charAt(i) == '⌘')
                        || (dataTable.getName().charAt(i) == '☂') || (dataTable.getName().charAt(i) == '☺') || (dataTable.getName().charAt(i) == '♕') || (dataTable.getName().charAt(i) == '☤')
                        || (dataTable.getName().charAt(i) == '✽') || (dataTable.getName().charAt(i) == '〲') || (dataTable.getName().charAt(i) == 'Ü') || (dataTable.getName().charAt(i) == '☄')) {
                    break;
                }


            }

            switch (dataTable.getName().substring(0, 2)) {
                case "AL":
                    systemMap.put("AL",systemMap.get("AL")+alWeight);
                    break;
                case "Ca":
                    systemMap.put("CA",systemMap.get("CA")+caWeight);
                    break;
                case "De":
                    systemMap.put("DE",systemMap.get("DE")+deWeight);
                    break;
                case "En":
                    systemMap.put("En",systemMap.get("En")+deWeight);
                    break;
                case "Ga":
                    systemMap.put("GA",systemMap.get("GA")+gaWeight);
                    break;
                case "Im":
                    systemMap.put("IM",systemMap.get("IM")+imWeight);
                    break;
                case "Ne":
                    systemMap.put("NE",systemMap.get("NE")+neWeight);
                    break;
                case "Or":
                    systemMap.put("OR",systemMap.get("OR")+orWeight);
                    break;
                case "Sp":
                    systemMap.put("SP",systemMap.get("SP")+spWeight);
                    break;
                case "St":
                    systemMap.put("St",systemMap.get("St")+stWeight);
                    break;
                case "Ur":
                    systemMap.put("UR",systemMap.get("UR")+urWeight);
                    break;
                case "Vi":
                    systemMap.put("VI",systemMap.get("VI")+viWeight);
                    break;
            }
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

    protected void makeCurrentAnalyze(File file) {

        if (file == null) {
            ModalWindow.makepopup(primaryStage, "Введите фамилию пользователя");

            /*final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            VBox dialogVbox = new VBox(20);
            Text text = new Text("Введите фамилию пользователя");
            dialogVbox.getChildren().add(text);
            text.setTextAlignment(TextAlignment.CENTER);
            dialogVbox.setAlignment(Pos.CENTER);
            Button button = new Button("Ok");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.close();
                }
            });
            button.setAlignment(Pos.CENTER);
            dialogVbox.getChildren().add(button);
            button.setAlignment(Pos.CENTER);
            Scene dialogScene = new Scene(dialogVbox, 200, 100);
            dialog.setScene(dialogScene);
            dialog.show();*/
        }
        else {
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


    }

    protected void setDegree1(String degree1) {
        this.degree1 = degree1;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}

