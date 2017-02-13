package com.mpsdevelopment.biopotential.server.gui.correctors;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.pcm.PCM;
import com.mpsdevelopment.biopotential.server.db.dao.DiseaseDao;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.eventbus.event.SelectCorrectorEvent;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public class CorrectorsPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(CorrectorsPanelController.class);
    private ObservableList<DataTable> correctorsData;

    private static File outputFile = new File("data\\out\\out1.wav");

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

    @FXML
    private Label patLabel;

    private Stage primaryStage;
    private static Map<Pattern,AnalysisSummary> healingsMap;
    private ServerSettings serverSettings;

    public CorrectorsPanelController() {

        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        serverSettings = BioApplication.APP_CONTEXT.getBean(ServerSettings.class);

        selectColumn.setMinWidth(80);
//        selectColumn.setCellValueFactory(new PropertyValueFactory<DataTable, Boolean>("checkCar "));
        /*selectColumn.setCellFactory(new Callback<TableColumn<DataTable, Boolean>, TableCell<DataTable, Boolean>>()
        {
            @Override
            public TableCell<DataTable, Boolean> call(TableColumn<DataTable, Boolean> tableColumn)
            {
                return new BooleanCell();
//                return new EditingCell();
            }
        });*/

        selectColumn.setCellValueFactory(new PropertyValueFactory<DataTable,Boolean>("check"));
        selectColumn.setCellFactory(new Callback<TableColumn<DataTable, Boolean>, TableCell<DataTable, Boolean>>() {
            @Override
            public TableCell<DataTable, Boolean> call(TableColumn<DataTable, Boolean> column) {
                return new TableCell<DataTable, Boolean>() {
                    public void updateItem(Boolean check, boolean empty) {
//                        super.updateItem(check, empty);
                        if (check == null || empty) {
                            setGraphic(null);
                        } else {
                            CheckBox box = new CheckBox();
                            BooleanProperty checked = (BooleanProperty) column.getCellObservableValue(getIndex());
                            box.setSelected(checked.get());
                            if (checked.get()) {
                                сorrectorsTable.getSelectionModel().select(getTableRow().getIndex());
                            } else {
                                сorrectorsTable.getSelectionModel().clearSelection(getTableRow().getIndex());

                            }
                            box.selectedProperty().bindBidirectional(checked);
                            setGraphic(box);
                        }
                    }
                };
            }
        });


        сorrectorsTable.getSelectionModel().selectAll();






        /*selectColumn.setCellFactory(new Callback<TableColumn<DataTable, Boolean>, //
                TableCell<DataTable, Boolean>>() {
            @Override
            public TableCell<DataTable, Boolean> call(TableColumn<DataTable, Boolean> p) {
                CheckBoxTableCell<DataTable, Boolean> cell = new CheckBoxTableCell<DataTable, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });*/

        /*selectColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataTable, Boolean>, ObservableValue<Boolean>>() {
            @Override public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<DataTable, Boolean> p) {
                return new ReadOnlyObjectWrapper(сorrectorsTable.getItems().get(p.getValue().));
            }
        });*/
        /*selectColumn.setCellFactory( new Callback<TableColumn<DataTable,Boolean>, TableCell<DataTable,Boolean>>()
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
//                сorrectorsTable.getSelectionModel().clearSelection();
            }
        });

//        сorrectorsTable.setMouseTransparent(true);


        correctorsData = FXCollections.observableArrayList();
        getPattersFromHealingsMap();
        patLabel.setText("Pattern's: " + correctorsData.size());

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
                Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Добавить корректор").setClazz(panel.getClass()).setHeight(515d).setWidth(748d).setHeightPanel(515d).setWidthPanel(718d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
                panel.setPrimaryStage(stage);
            }
        });

    }
    /**
     *  Sort correctorsData from duplicate items by filename
     */
    private void createFileCorrection() {
        сorrectorsTable.getSelectionModel().selectAll();
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

        Machine.setEdxFileFolder(serverSettings.getStoragePath());


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

    @Handler
    public void handleMessage(SelectCorrectorEvent event) throws Exception {
        LOGGER.info(" GOT correctors ");
        Set<DataTable> correctorsItems = event.getMap();

        if(correctorsData != null) {
            correctorsItems.forEach(new Consumer<DataTable>() {
                @Override
                public void accept(DataTable dataTable) {
                    correctorsData.add(dataTable);
                }
            });

            сorrectorsTable.setItems(correctorsData);
        }
    }


    public void setHealingsMap(Map<Pattern, AnalysisSummary> healingsMap) {
        this.healingsMap = healingsMap;
    }

    public void merge(Collection<List<Double>> lists) throws IOException, UnsupportedAudioFileException {

        Collection out;
        out = PCM.merge(lists);

        double[] buffer = out.stream().mapToDouble(new ToDoubleFunction<Double>() {
            @Override
            public double applyAsDouble(Double aDouble) {
                return aDouble.doubleValue();
            }
        }).toArray();

        byte[] bytes = new byte[buffer.length];

        for (int i=0; i < buffer.length; i++) {
            if (((buffer[i]) * 128) >= 127) {
                bytes[i] = (byte) 0xFF;
            }
            else if (((buffer[i]) * 128)  <= -128) {
                bytes[i] = (byte) 0x01;
            }
            else {
                bytes[i] = (byte) ((byte) ((buffer[i]) * 128) ^ 0x80);

            }
        }

        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilterMp3 = new FileChooser.ExtensionFilter("Mp3 files (*.mp3)","*.mp3");
        FileChooser.ExtensionFilter extFilterWav = new FileChooser.ExtensionFilter("Wav files (*.wav)","*.wav");
        fileChooser.getExtensionFilters().addAll(extFilterMp3, extFilterWav);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file.getName().contains(".wav")) {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            AudioFormat format = new AudioFormat(22050, 8, 1, false, false);
            AudioInputStream stream = new AudioInputStream(bais, format, buffer.length);
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);

        }
        else if (file.getName().contains(".mp3")){
            OutputStream outstream = new FileOutputStream(file);
            byte[] data = DiseaseDao.encodePcmToMp3(bytes);
            outstream.write(data, 0, data.length);
        }

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
            super.updateItem(item, empty);
            if (!isEmpty()) {
                checkBox.setSelected(true);
            }
            if ( ! empty )
            {
                TableRow  row = getTableRow();

                if ( row != null )
                {
                    *//*int rowNo = row.getIndex();
                    TableView.TableViewSelectionModel sm = getTableView().getSelectionModel();
                    if ( item )  sm.select( rowNo );
                    else  sm.clearSelection( rowNo );*//*
                }
            }

            super.updateItem( item, true );
        }
    }*/

    class BooleanCell extends TableCell<DataTable, Boolean> {
        private CheckBox checkBox;
        public BooleanCell() {
            checkBox = new CheckBox();
            checkBox.setDisable(false);

            checkBox.selectedProperty().addListener(new ChangeListener<Boolean> () {
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(!isEditing())
                    {
//                        commitEdit(newValue == null ? false : newValue);
                        if (newValue) {
                            сorrectorsTable.getSelectionModel().select(getTableRow().getIndex());

                        } else {
                            сorrectorsTable.getSelectionModel().clearSelection(getTableRow().getIndex());
                        }
                    }
                }
            });
            this.setGraphic(checkBox);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.setEditable(true);
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
//                checkBox.setSelected(item);
            }
        }
    }









}







