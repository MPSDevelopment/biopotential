package com.mpsdevelopment.biopotential.server.gui.analysis;

import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.db.pojo.SystemDataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
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

public class HiddenPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(HiddenPanelController.class);
    public static final int patternWeight = 10;
    private ObservableList<DataTable> analysisData;
    ObservableList<DataTable> data = FXCollections.observableArrayList();
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
    private File file;
    private Map<Pattern, AnalysisSummary> diseases;
    private Map<Pattern, AnalysisSummary> allHealings;

    private static File outputFile = new File("AudioFiles\\out\\out.wav");
    private String degree1;
    private String degree2;
    private SystemDataTable systemDataTable;

    public static final String HOST = "localhost";
    public static final int PORT = 8098;
    private double alWeight = 0, viWeight = 0, caWeight = 0, deWeight = 0, enWeight = 0, gaWeight = 0, imWeight = 0, neWeight = 0, orWeight = 0, spWeight = 0, stWeight = 0, urWeight = 0;
    private ObservableList<DataTable> analysisHiddenData;


    public void setDegree2(String degree2) {
        this.degree2 = degree2;
    }

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

        /*automaticsLevelColumn.setSortable(true);
        healthConditionHiddenTable.getSortOrder().add(automaticsLevelColumn); // sort cell'a by name*/

    }

    private void makeAnalyze(ObservableList<DataTable> data) throws UnsupportedAudioFileException, IOException, SQLException {

        /*long t2 = System.currentTimeMillis();
        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();

        // get diseas for hidden
        // degree max
        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();

        // get diseas for hiden
        // degree max
        String json = bioHttpClient.executePostRequest("/api/diseas/" + "hidden" + "/" + degree1 + "/getDiseas", file);
        Map<Pattern, AnalysisSummary> diseasesHiden = new HashMap<>();
        Map<Pattern, AnalysisSummary> diseasesHidenMax = JsonUtils.fromJson(typeOfHashMap, json);

        //degree Po
        json = bioHttpClient.executePostRequest("/api/diseas/" + "hiden" + "/" + degree2 + "/getDiseas", file);
        Map<Pattern, AnalysisSummary> diseasesHidenPo = JsonUtils.fromJson(typeOfHashMap, json);

        diseasesHiden.putAll(diseasesHidenMax);
        diseasesHiden.putAll(diseasesHidenPo);
        diseasToAnalysisData(diseasesHiden, analysisHiddenData);


        healthConditionTable.setItems(analysisData);
        automaticsLevelColumn.setSortable(true);
        healthConditionTable.getSortOrder().add(automaticsLevelColumn); // sort cell'a by name*/
        healthConditionHiddenTable.setItems(data);

        healthConditionHiddenTable.getSortOrder().add(automaticsLevelColumn); // sort cell'a by name
        automaticsLevelColumn.setSortable(true);
        automaticsLevelColumn.setSortType(TableColumn.SortType.ASCENDING);

        diseaseName.setSortable(true);
        diseaseName.setSortType(TableColumn.SortType.ASCENDING);
        healthConditionHiddenTable.getSortOrder().add(diseaseName); // sort cell'a by name

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

    public void makeCurrentAnalyze(ObservableList<DataTable> data) {
        try {
            makeAnalyze(data);
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

}
