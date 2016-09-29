package com.mpsdevelopment.biopotential.server.gui.correctors;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.db.pojo.Patterns;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.*;

public class CorrectorsPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(CorrectorsPanelController.class);
    private ObservableList<Patterns> correctorsData = FXCollections.observableArrayList();

    @Autowired
    private BioHttpClient deviceBioHttpClient;

    @Autowired
    private ServerSettings settings;

    @FXML
    private TableView<Patterns> сorrectorsTable;

    @FXML
    private TableColumn<Patterns, String> deseaseName;

    @FXML
    private TableColumn<Patterns, String> deseaseLevel;

    @FXML
    private TableColumn<Patterns, String> numberColumn;

    private Stage primaryStage;
    private Patterns[] patterns;
    private Patterns[] arrangePatterns;

    public CorrectorsPanelController() {
        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        getPatters();

        numberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Patterns, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<Patterns, String> p) {
                return new ReadOnlyObjectWrapper(сorrectorsTable.getItems().indexOf(p.getValue()) + "");
            }
        });
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER;");

        deseaseName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Patterns, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Patterns, String> deseaseName) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", deseaseName.getValue().getPatternName()));

                return property;
            }
        });
        deseaseLevel.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Patterns, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Patterns, String> deseaseLevel) {
                SimpleStringProperty property = new SimpleStringProperty();
//                property.setValue(String.format("%s", deseaseLevel.getValue().()));
                return property;
            }
        });


    }



    public void getPatters() {
        String url = String.format("http://%s:%s%s", settings.getHost(), settings.getPort(), ControllerAPI.PATTERNS_CONTROLLER + ControllerAPI.PATTERNS_CONTROLLER_GET_ALL);
        String json = deviceBioHttpClient.executeGetRequest(url);
        patterns = JsonUtils.fromJson(Patterns[].class, json);
        correctorsData.clear();
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

        // sort all users by name
        Arrays.sort(patterns, new Comparator<Patterns>() {
            public int compare(Patterns o1, Patterns o2) {
                    return o1.getPatternName().toString().compareTo(o2.getPatternName().toString());

            }
        });

        for (Patterns unit : patterns) {

//            LOGGER.info("User - %s", unit.getLogin() +unit.getName() +" " +unit.getSurname() + unit.getGender());
            correctorsData.add(unit);

        }

        сorrectorsTable.setItems(correctorsData);

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


    @Override
    public void subscribe() {
        EventBus.subscribe(this);
    }

}





