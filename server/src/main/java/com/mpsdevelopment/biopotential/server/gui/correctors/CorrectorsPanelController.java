package com.mpsdevelopment.biopotential.server.gui.correctors;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.pcm.PCM;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
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
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public class CorrectorsPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(CorrectorsPanelController.class);
    private ObservableList<DataTable> correctorsData;

    private static File outputFile = new File("data\\out\\out.wav");

    @FXML
    private TableView<DataTable> сorrectorsTable;

    @FXML
    private TableColumn<DataTable, String> deseaseName;

    @FXML
    private TableColumn<DataTable, String> deseaseLevel;

    @FXML
    private TableColumn<DataTable, String> numberColumn;

    @FXML
    private TableColumn<DataTable, Boolean> selectColumn;

    @FXML
    private Button createFileCorrection;

    @FXML
    private Button addCorrectorButton;

    @FXML
    private Button selectAllButton;

    private Stage primaryStage;
    private static Map<Pattern,AnalysisSummary> healingsMap;

    public CorrectorsPanelController() {
        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectColumn.setMinWidth(80);
//        selectColumn.setCellValueFactory(new PropertyValueFactory<DataTable, Boolean>("checkCar "));
        selectColumn.setCellFactory(new Callback<TableColumn<DataTable, Boolean>, TableCell<DataTable, Boolean>>()
        {
            @Override
            public TableCell<DataTable, Boolean> call(TableColumn<DataTable, Boolean> tableColumn)
            {
                return new BooleanCell();
            }
        });

        /*selectColumn.setCellValueFactory( new PropertyValueFactory<DataTable,Boolean>( "checkBoxValue" ) );
        selectColumn.setCellFactory( new Callback<TableColumn<DataTable,Boolean>, TableCell<DataTable,Boolean>>()
        {
            @Override
            public TableCell<DataTable,Boolean> call( TableColumn<DataTable,Boolean> param )
            {
                return new CheckBoxTableCell<DataTable,Boolean>()
                {
                    {
                        setAlignment( Pos.CENTER );
                    }
                    @Override
                    public void updateItem( Boolean item, boolean empty )
                    {
                        if ( ! empty )
                        {
                            TableRow  row = getTableRow();

                            if ( row != null )
                            {
                                int rowNo = row.getIndex();
                                TableView.TableViewSelectionModel sm = getTableView().getSelectionModel();

                                if ( item )  sm.select( rowNo );
                                else  sm.clearSelection( rowNo );
                            }
                        }

                        super.updateItem( item, empty );
                    }
                };
            }
        } );
        selectColumn.setEditable( true );*/

        сorrectorsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LOGGER.info("Click");
                сorrectorsTable.getSelectionModel().clearSelection();
            }
        });

//        сorrectorsTable.setMouseTransparent(true);


        correctorsData = FXCollections.observableArrayList();
        getPattersFromHealingsMap();

        сorrectorsTable.setItems(correctorsData);
        сorrectorsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // make enable minimize button on window
//        сorrectorsTable.getSelectionModel().setCellSelectionEnabled(true);

        numberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<DataTable, String> p) {
                return new ReadOnlyObjectWrapper(сorrectorsTable.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER;");

        selectAllButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                сorrectorsTable.getSelectionModel().selectAll();
            }
        });

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
            createFileCorrection();
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
    /**
     *  Sort correctorsData from duplicate items by filename
     */
    private void createFileCorrection() {
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

        healingsMap.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                sortedHealings.add(pattern);
            }
        });


        for (DataTable item : sortedSelectedItems) {
            sortedHealings.forEach(new Consumer<Pattern>() {
                @Override
                public void accept(Pattern pattern) {
                    if (item.getFilename().equals(pattern.getFileName())) {
                        if (selList.add(pattern.getPcmData())) {
                            LOGGER.info("%s", pattern.getName());
                            LOGGER.info("%s", item.getFilename());

                        }
                    }
                }
            });
        }

        LOGGER.info("Added correctors %s", selList.size());
        try {
            merge(selList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    /**
     * getPatterns from healingsMap and put to сorrectorsTable
     */
    public void getPattersFromHealingsMap()  {

        healingsMap.forEach((pattern, analysisSummary) -> {
            LOGGER.info("%s %s\n", pattern.getKind(), pattern.getName(), analysisSummary.getDispersion());
            correctorsData.add(DataTable.createDataTableObject(pattern,analysisSummary));

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
        LOGGER.info("GOT healings map, size %s", event.getMap().size());
        healingsMap = event.getMap();
    }


    public void setHealingsMap(Map<Pattern, AnalysisSummary> healingsMap) {
        this.healingsMap = healingsMap;
    }

    public static void merge(Collection<List<Double>> lists) throws IOException, UnsupportedAudioFileException {

        Collection out;
        out = PCM.merge(lists);

        double[] buffer = out.stream().mapToDouble(new ToDoubleFunction<Double>() {
            @Override
            public double applyAsDouble(Double aDouble) {
                return aDouble.doubleValue();
            }
        }).toArray();

        double minP =0;
        boolean flagp = true;
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] > 0) {
                if (flagp){minP = buffer[i]; flagp = false;}
                else if (buffer[i] < minP) {
                    minP = buffer[i];
                }
            }
        }
        byte[] bytes = new byte[buffer.length];

        for (int i=0; i < buffer.length; i++) {
            bytes[i] = (byte) (((buffer[i]) * 1/minP)/*+128*/);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        AudioFormat format = new AudioFormat(22050, 8, 1, true, false);
        AudioInputStream stream = new AudioInputStream(bais, format, buffer.length);
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, outputFile);

    }

    class BooleanCell extends TableCell<DataTable, Boolean> {

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
                    /*if ( item )  sm.select( rowNo );
                    else  sm.clearSelection( rowNo );*/
                }
            }

//            super.updateItem( item, empty );
        }





        }
    }







