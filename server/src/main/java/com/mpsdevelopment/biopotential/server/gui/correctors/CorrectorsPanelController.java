package com.mpsdevelopment.biopotential.server.gui.correctors;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.pcm.PCM;
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
import javafx.collections.ObservableSet;
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

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public class CorrectorsPanelController extends AbstractController /*implements Subscribable*/ {

    private static final Logger LOGGER = LoggerUtil.getLogger(CorrectorsPanelController.class);
    private ObservableList<DataTable> correctorsData = FXCollections.observableArrayList();

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
            Collection lists = new HashSet();
//            Collection hash = new HashSet<DataTable>();
            HashSet<DataTable> hash = new HashSet<>();
            ObservableList<DataTable> selectedItems = сorrectorsTable.getSelectionModel().getSelectedItems();
            LOGGER.info("Selected item %s", selectedItems.size());

            ArrayList<DataTable> temp = new ArrayList<>(selectedItems);
            hash = removeDuplicates(temp);

            /*for (DataTable item : hash) {
                healingsMap.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                    @Override
                    public void accept(Pattern pattern, AnalysisSummary analysisSummary) {

                        if (item.getName().equals(pattern.getName())) {
                            List<Double> pcmData = pattern.getPCMData();
                            lists.add(pcmData);
                        }

                    }
                });
            }*/

            selectedItems.forEach((tab) -> {
                healingsMap.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                    @Override
                    public void accept(Pattern pattern, AnalysisSummary analysisSummary) {

                        if (tab.getName().equals(pattern.getName())) {
                            List<Double> pcmData = pattern.getPCMData();
                            lists.add(pcmData);
                        }

                    }
                });
            });

            LOGGER.info("Added correctors %s", lists.size());
            try {
                merge(lists);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
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

    public void getPatters()  {

        healingsMap.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                System.out.printf("%s %s\n", pattern.getKind(), pattern.getName(), analysisSummary.getDispersion());
                /*List<Double> pcmData = pattern.getPCMData();
                lists.add(pcmData);*/
                correctorsData.add(createDataTableObject(pattern,analysisSummary));

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


    private DataTable createDataTableObject(Pattern k, AnalysisSummary v) {
        DataTable dataTable = new DataTable();
        dataTable.setName(k.getName());
        dataTable.setDispersion(v.getDispersion());
        return dataTable;
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

    public static HashSet<DataTable> removeDuplicates(ArrayList<DataTable> list) {
//        ArrayList<DataTable> result = new ArrayList<>();
        HashSet<DataTable> set = new HashSet<>();
//        ObservableSet<DataTable> set = FXCollections.observableSet(new HashSet<>());
//        set.add(list.get(0));
        for (DataTable item : list) {
//            if (!set.contains(item)) {
            set.add(item);
                /*set.forEach((new Consumer<DataTable>() {
                    @Override
                    public void accept(DataTable s) {
                        if (!s.getName().equals(item.getName())) {
//                            result.add(item);
                            set.add(item);
                        }
                    }
                }));*/

//            }
        }
        return set;
    }



}




