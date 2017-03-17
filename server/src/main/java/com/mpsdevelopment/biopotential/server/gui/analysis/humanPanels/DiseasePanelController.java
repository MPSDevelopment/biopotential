package com.mpsdevelopment.biopotential.server.gui.analysis.humanPanels;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.HumanPoints;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
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
import net.engio.mbassy.listener.Handler;

import java.io.File;
import java.net.URL;
import java.util.*;

public class DiseasePanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiseasePanelController.class);

    @FXML
    private TableView<HumanPoints> table;

    @FXML
    private TableColumn<HumanPoints, String> numberColumn;

    @FXML
    private TableColumn<HumanPoints, String> diseaseName;

    @FXML
    private TableColumn<HumanPoints, String> dispersion;

    @FXML
    private TableColumn<HumanPoints, String> level;

    @FXML
    private Button closeButton;

    private Stage primaryStage;
    private File file;
    private String gender;
    private Map<Pattern, AnalysisSummary> diseases;
    private Map<Pattern, AnalysisSummary> allHealings;

    private String degree1;
    private String degree2;


    List<HumanPoints> list;
    private ObservableList<HumanPoints> analysisStressData;


    protected void setDegree2(String degree2) {
        this.degree2 = degree2;
    }

    public DiseasePanelController() {
//        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        numberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HumanPoints, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HumanPoints, String> p) {
                return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER;");

        diseaseName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HumanPoints, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HumanPoints, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getName()));

                return property;
            }
        });

        dispersion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HumanPoints, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HumanPoints, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getDispersion()));
                return property;
            }
        });

        level.setStyle("-fx-alignment: CENTER;");
        level.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HumanPoints, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HumanPoints, String> p) {
                return new ReadOnlyObjectWrapper(p.getValue().getDegree());
            }
        });

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
                LOGGER.info("Close AnalysisPanelController");
            }
        });


    }

    public void init() {

        ObservableList<HumanPoints> diseaseSystemTable = FXCollections.observableArrayList();
        diseaseSystemTable.addAll(list);
        table.setItems(diseaseSystemTable);

    }


    public List<HumanPoints> getList() {
        return list;
    }

    public void setList(List<HumanPoints> list) {
        this.list = list;
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

