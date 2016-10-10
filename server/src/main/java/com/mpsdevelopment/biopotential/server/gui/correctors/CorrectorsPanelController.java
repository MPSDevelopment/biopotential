package com.mpsdevelopment.biopotential.server.gui.correctors;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
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
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

public class CorrectorsPanelController extends AbstractController /*implements Subscribable*/ {

    private static final Logger LOGGER = LoggerUtil.getLogger(CorrectorsPanelController.class);
    private ObservableList<DataTable> correctorsData = FXCollections.observableArrayList();

    @FXML
    private TableView<DataTable> сorrectorsTable;

    @FXML
    private TableColumn<DataTable, String> deseaseName;

    @FXML
    private TableColumn<DataTable, String> deseaseLevel;

    @FXML
    private TableColumn<DataTable, String> numberColumn;

    @FXML
    private Button createFileCorrection;

    @FXML
    private Button addCorrectorButton;

    private Stage primaryStage;
    private static Map<Pattern,AnalysisSummary> healingsMap;

    public CorrectorsPanelController() {
        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        getPatters();

        сorrectorsTable.setItems(correctorsData);
        сorrectorsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        сorrectorsTable.getSelectionModel().setCellSelectionEnabled(true);

        numberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(сorrectorsTable.getItems().indexOf(p.getValue()) +1 + "");
            }
        });
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER;");

        deseaseName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> deseaseName) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", deseaseName.getValue().getName()));

                return property;
            }
        });
        deseaseLevel.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> deseaseLevel) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", deseaseLevel.getValue().getDispersion()));

                return property;
            }
        });
        createFileCorrection.setOnAction(event -> {
            ObservableList<DataTable> selectedItems = сorrectorsTable.getSelectionModel().getSelectedItems();
            LOGGER.info("Selected item %s", selectedItems);
        });

        addCorrectorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddCorrectorPanel panel = new AddCorrectorPanel();
                Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Добавить корректор").setClazz(panel.getClass()).setHeight(220d).setWidth(330d).setHeightPanel(208d).setWidthPanel(306d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                panel.setPrimaryStage(stage);
            }
        });



    }



    public void getPatters() {
        /*String url = String.format("http://%s:%s%s", settings.getHost(), settings.getPort(), ControllerAPI.PATTERNS_CONTROLLER + ControllerAPI.PATTERNS_CONTROLLER_GET_ALL);
        String json = deviceBioHttpClient.executeGetRequest(url);
        patterns = JsonUtils.fromJson(Patterns[].class, json);
        correctorsData.clear();*/
        /*arrangePatterns = new Patterns[0];
        int i = 0;
        int count = 0;
        for (Patterns pat : patterns) {
            if((!pat.getPatternName().contains("BAC ")) && (!pat.getPatternName().contains("Muc ") && (!pat.getPatternName().contains("VIR ")))) {
                arrangePatterns[i] = pat;
                i++;
            }
            else count++;

        }*/

//        LOGGER.info("arrangePatterns size %s", arrangePatterns.length);

       /* final Map<Pattern, AnalysisSummary> healings = Machine.summarizePatterns(
                new SummaryCondition() {
                    @Override
                    public boolean test(Pattern strain, AnalysisSummary summary) { // и потом берутся только те которые summary.getDispersion() == 0 т.е. MAx
                        return summary.getDegree() == 0;
                    }
                },
                sample,
                db.getIterForFolder(((EDXPattern) dk).getCorrectingFolder())); // вытягиваются папка с коректорами для конкретной болезни BAC -> FL BAC
        healings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern hk, AnalysisSummary hv) {
//                            hk.getPCMData()
                System.out.printf("%s %s\n",
                        hk.getKind(), hk.getName());
            }
        });*/




        // sort all users by name
        /*Arrays.sort(patterns, new Comparator<Patterns>() {
            public int compare(Patterns o1, Patterns o2) {
                    return o1.getPatternName().toString().compareTo(o2.getPatternName().toString());

            }
        });

        for (Patterns unit : patterns) {

//            LOGGER.info("User - %s", unit.getLogin() +unit.getName() +" " +unit.getSurname() + unit.getGender());
            correctorsData.add(unit);

        }

        сorrectorsTable.setItems(correctorsData);*/

        healingsMap.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern hk, AnalysisSummary hv) {
                System.out.printf("%s %s\n", hk.getKind(), hk.getName(), hv.getDispersion());

                correctorsData.add(createDataTableObject(hk,hv));

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

    @Handler
    public void handleMessage(HealingsMapEvent event) throws Exception {
        LOGGER.info(" GOT healings map, size %s", event.getMap().size());
        healingsMap = event.getMap();
    }

    /*@Override
    public void subscribe() {
        EventBus.subscribe(this);
    }*/

    private DataTable createDataTableObject(Pattern k, AnalysisSummary v) {
        DataTable dataTable = new DataTable();
        dataTable.setName(k.getName());
        dataTable.setDispersion(v.getDispersion());
        return dataTable;
    }



}





