package com.mpsdevelopment.biopotential.server.gui.correctors;

import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.eventbus.event.SelectCorrectorEvent;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.httpclient.HttpClientFactory;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class AddCorrectorController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(AddCorrectorController.class);
    private ObservableList<DataTable> analysisData;
    @FXML
    private TableView<DataTable> сorrectorsTable;

    @FXML
    private TableColumn<DataTable, String> diseaseName;

    @FXML
    private TableColumn<DataTable, String> catalogName;

    @FXML
    private TableColumn<DataTable, Boolean> selectColumn;

    @FXML
    private TableColumn<DataTable, String> numberColumn;

    @FXML
    private Button addCorrectorButton;

    @FXML
    private Button cancelButton;

    private Stage primaryStage;
    public static final String HOST = "localhost";
    public static final int PORT = 8098;

    public AddCorrectorController() {
        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        analysisData = FXCollections.observableArrayList();
        сorrectorsTable.setItems(analysisData);
        сorrectorsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // make enable minimize button on window


        /*selectColumn.setCellFactory(new Callback<TableColumn<DataTable, Boolean>, TableCell<DataTable, Boolean>>()
        {
            @Override
            public TableCell<DataTable, Boolean> call(TableColumn<DataTable, Boolean> tableColumn)
            {
                return new AddCorrectorController.BooleanCell();
            }
        });*/

        selectColumn.setCellValueFactory(new PropertyValueFactory<DataTable,Boolean>("check"));
        selectColumn.setCellFactory(new Callback<TableColumn<DataTable, Boolean>, TableCell<DataTable, Boolean>>() {
            @Override
            public TableCell<DataTable, Boolean> call(TableColumn<DataTable, Boolean> column) {
                return new TableCell<DataTable, Boolean>() {
                    public void updateItem(Boolean check, boolean empty) {
                        super.updateItem(check, empty);
                        if (check == null || empty) {
                            setGraphic(null);
                        } else {
                            CheckBox box = new CheckBox();
                            BooleanProperty checked = (BooleanProperty) column.getCellObservableValue(getIndex());
//                            box.setSelected(checked.get());
                            if (checked.get()) {
//                                сorrectorsTable.getSelectionModel().select(getTableRow().getIndex());
                            } else {
//                                сorrectorsTable.getSelectionModel().clearSelection(getTableRow().getIndex());

                            }
//                            box.selectedProperty().bindBidirectional(checked);
                            setGraphic(box);
                            box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                                    if (t1) {
                                        сorrectorsTable.getSelectionModel().select(getTableRow().getIndex());
                                        box.selectedProperty().bindBidirectional(checked);

                                    } else {
                                        сorrectorsTable.getSelectionModel().clearSelection(getTableRow().getIndex());
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });

        numberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(сorrectorsTable.getItems().indexOf(p.getValue()) + 1 + "");
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

        catalogName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> dataTable) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(String.format("%s", dataTable.getValue().getCatalogName()));

                return property;
            }
        });

        addCorrectorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<DataTable> selectedItems = сorrectorsTable.getSelectionModel().getSelectedItems();
                LOGGER.info("Selected item %s", selectedItems.size());

                Set<Pattern> sortedHealings = new HashSet<>();
                Set<DataTable> sortedSelectedItems = new HashSet<>();

                List selList = new ArrayList();

                selectedItems.forEach(new Consumer<DataTable>() {
                    @Override
                    public void accept(DataTable dataTable) {
                        sortedSelectedItems.add(dataTable);
                    }
                });

                EventBus.publishEvent(new SelectCorrectorEvent(sortedSelectedItems));
                close();

            }
        });

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });


        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();


        String url = String.format("http://%s:%s%s", HOST, PORT, ControllerAPI.PATTERNS_CONTROLLER + ControllerAPI.PATTERNS_CONTROLLER_GET_ALL);
        String json = bioHttpClient.executeGetRequest(url);

        Type typeOfHashMap = new TypeToken<List<EDXPattern>>() { }.getType();
        List<EDXPattern> patterns = JsonUtils.fromJson(typeOfHashMap, json);
        List<EDXPattern> correctors = new ArrayList<>();
        LOGGER.info("%s", patterns.size());

        /*patterns.forEach(new Consumer<EDXPattern>() {
            @Override
            public void accept(EDXPattern pattern) {
                LOGGER.info("%s",pattern.getName().substring(0, 3) );
                if (pattern.getName().substring(0, 3).contains("000")) {
                    correctors.add(pattern);
                }
            }
        });*/

        correctorsToAnalysisData(patterns,analysisData);
//        correctorsToAnalysisData(correctors,analysisData);
        сorrectorsTable.setItems(analysisData);

    }

    private ObservableList<DataTable> correctorsToAnalysisData(List<EDXPattern> correctors, ObservableList<DataTable> analysisData) {

        correctors.forEach(new Consumer<EDXPattern>() {
            @Override
            public void accept(EDXPattern pattern) {
//                LOGGER.info("d: %s\t%f\n", k.getName(), v.getDispersion());
                analysisData.add(DataTable.createDataTableObject(pattern));
            }
        });
        return analysisData;
    }

    /*class BooleanCell extends TableCell<DataTable, Boolean> {

        private CheckBox checkBox;
        public BooleanCell() {
            checkBox = new CheckBox();
//            checkBox.setDisable(false);
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                    if (t1) {
                        сorrectorsTable.getSelectionModel().select(getTableRow().getIndex());

                    } else {
                        сorrectorsTable.getSelectionModel().clearSelection(getTableRow().getIndex());
                    }
                }
            });
            this.setGraphic(checkBox);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.setEditable(true);
            setAlignment(Pos.CENTER);
        }
        @Override
        public void startEdit() {
            super.startEdit();
            if (isEmpty()) {
                return;
            }
            checkBox.setDisable(false);
            checkBox.requestFocus();
        }
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            checkBox.setDisable(true);
        }
        public void commitEdit(Boolean value) {
            super.commitEdit(value);
            checkBox.setDisable(true);
        }
        @Override
        public void updateItem(Boolean item, boolean empty) {
//            super.updateItem(item, empty);
            if (!isEmpty()) {
//                checkBox.setSelected(true);
            }
            if ( ! empty )
            {
//                TableRow  row = getTableRow();

//                if ( row != null )
                {
//                    int rowNo = row.getIndex();
//                    TableView.TableViewSelectionModel sm = getTableView().getSelectionModel();
                    if ( item )  sm.select( rowNo );
                    else  sm.clearSelection( rowNo );
                }
            }

//            super.updateItem( item, empty );
        }





    }*/

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


}
