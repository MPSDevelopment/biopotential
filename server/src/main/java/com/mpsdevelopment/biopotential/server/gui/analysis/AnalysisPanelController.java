package com.mpsdevelopment.biopotential.server.gui.analysis;

import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.dao.DiseaseDao;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.eventbus.event.FileChooserEvent;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.eventbus.event.HealingsMapEvent;
import com.mpsdevelopment.biopotential.server.gui.correctors.CorrectorsPanel;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.httpclient.HttpClientFactory;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
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
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.engio.mbassy.listener.Handler;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;

public class AnalysisPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(AnalysisPanelController.class);
    public static final int patternWeight = 10;
    private ObservableList<DataTable> analysisData;
    private ObservableList<String> level;

    @FXML
    private ScatterChart<Number, Number> scatterChart;

    @FXML
    private TableView<DataTable> healthConditionTable;

    @FXML
    private TableView<Map.Entry<String,Integer>> smallTable;

    @FXML
    private TableColumn<DataTable, String> diseaseName;

    @FXML
    private TableColumn<DataTable, String> diseaseLevel;

    @FXML
    private TableColumn<DataTable, String> numberColumn;

    @FXML
    private TableColumn numberSystemColumn;

    @FXML
    private TableColumn<Map.Entry<String, Integer>, String> systemColumn;

    @FXML
    private TableColumn levelColumn;

    @FXML
    private Button continueButton;

    private Stage primaryStage;
    private File file;
    private Map<Pattern, AnalysisSummary> diseases;
    private Map<Pattern, AnalysisSummary> allHealings;

    private static File outputFile = new File("AudioFiles\\out\\out.wav");
    private String degree;

    public AnalysisPanelController() {
//        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        analysisData = FXCollections.observableArrayList();
        level = FXCollections.observableArrayList();
        diseases = new HashMap<>();
        allHealings = new HashMap<>();

        healthConditionTable.setItems(analysisData);

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

        numberSystemColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> p) {
//                return new SimpleStringProperty(p.getValue().getKey());
                return new ReadOnlyObjectWrapper(smallTable.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });

        systemColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> p) {
                return new SimpleStringProperty(p.getValue().getKey());
            }
        });


        levelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> p) {
                return new SimpleStringProperty(Integer.toString(p.getValue().getValue())+ " %");
            }
        });

        continueButton.setOnAction(event -> {
            CorrectorsPanel panel = new CorrectorsPanel(allHealings);
            Stage stage = StageUtils.createStage(null, panel, new StageSettings().setPanelTitle("Коррекция").setClazz(panel.getClass()).setHeight(722d).setWidth(1273d)
                    .setHeightPanel(722d).setWidthPanel(1273d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
            panel.setPrimaryStage(stage);

        });

        scatterChart.setTitle("Body Overview");

        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(15.0, 90));
        series1.getData().add(new XYChart.Data(2.8, 33.6));
        series1.getData().add(new XYChart.Data(1.8, 81.4));

        scatterChart.getStylesheets().add("scater.css");
        scatterChart.getData().addAll(series1);

    }

    private void makeAnalyze(File file) throws UnsupportedAudioFileException, IOException, SQLException {
        long t2 = System.currentTimeMillis();

//        BioHttpClient bioHttpClient = new BioHttpClient();
        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();

        String json = bioHttpClient.executePostRequest("/api/diseas/" + degree + "/getDiseas", file);

        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();
        Map<Pattern, AnalysisSummary> diseases = JsonUtils.fromJson(typeOfHashMap, json);

//        diseases.putAll(diseaseDao.getDeseases(file));
        diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern k, AnalysisSummary v) {
                System.out.printf("%s\t%f\n", k.getName().replace("?",""), v.getDispersion());
                // LOGGER.info("d: %s\t%f\n", k.getName(), v.getDispersion());

                analysisData.add(DataTable.createDataTableObject(k, v));
            }
        });
        LOGGER.info("Total time for calculate diseases %d ms", System.currentTimeMillis() - t2);
        long t1 = System.currentTimeMillis();

        String heal = bioHttpClient.executePostRequest("/api/diseas/" + degree + "/getHealings",file);
        typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();
        /*Map<Pattern, AnalysisSummary> */allHealings = JsonUtils.fromJson(typeOfHashMap, heal);

//        allHealings.putAll(diseaseDao.getHealings(diseases, file));
        LOGGER.info("Total time for calculate healings %d ms", System.currentTimeMillis() - t1);

        LOGGER.info("healings size %s", allHealings.size());
        EventBus.publishEvent(new HealingsMapEvent(allHealings));

        Set<DataTable> sortedSelectedItems = new HashSet<>();
//        ObservableList<DataTable> sortedSelectedItems = FXCollections.observableArrayList();
        char [] str = new char[5];
        char [] cmp = new char[5];


        for (DataTable dataTable: analysisData) {
            int count=0;
            dataTable.getName().getChars(0,4,str,0);
            for (DataTable temp: analysisData) {
                temp.getName().getChars(0,4,cmp,0);
                if(Arrays.equals(str,cmp)){
                    count++;
                }
                if (count > 1) {
                    sortedSelectedItems.add(dataTable);
                }
            }
        }

        Map<String,Integer> systemMap = new HashMap<>();
        systemMap.put("CARDIO система",0);
        systemMap.put("DERMA система",0);
        systemMap.put("Endocrinology система",0);
        systemMap.put("GASTRO система",0);
        systemMap.put("IMMUN система",0);
        systemMap.put("MENTIS система",0);
        systemMap.put("NEURAL система",0);
        systemMap.put("ORTHO система",0);
        systemMap.put("SPIRITUS система",0);
        systemMap.put("Stomat система",0);
        systemMap.put("UROLOG система",0);
        systemMap.put("VISION система",0);

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

            switch (dataTable.getName().substring(0, index)) {

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
            }

        }

        ObservableList<Map.Entry<String,Integer>> result = FXCollections.observableArrayList(systemMap.entrySet());
        smallTable.setItems(result);
        systemColumn.setSortable(true);
//        smallTable.getColumns().addAll(numberSystemColumn,systemColumn,levelColumn);
        smallTable.getSortOrder().add(systemColumn);
    }

    /*private DataTable createDataTableObject(Pattern k, AnalysisSummary v) {
        DataTable dataTable = new DataTable();
        dataTable.setName(k.getName());
        dataTable.setDispersion(v.getDispersion());
        dataTable.setFilename(k.getFileName());
        return dataTable;
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

    public void setDegree(String degree) {
        this.degree = degree;
    }

    /*
	 * public static void merge(Collection<List<Double>> lists) throws
	 * IOException, UnsupportedAudioFileException {
	 * 
	 * Collection out; out = PCM.merge(lists);
	 * 
	 * double[] buffer = out.stream().mapToDouble(new ToDoubleFunction<Double>()
	 * {
	 * 
	 * @Override public double applyAsDouble(Double aDouble) { return
	 * aDouble.doubleValue(); } }).toArray();
	 * 
	 * 
	 *//*
		 * byte[] data = new byte[buffer.length*2]; for (int i = 0; i <
		 * buffer.length; i++) { int temp = (short) (buffer[i] * 8); data[2*i +
		 * 0] = (byte) temp; data[2*i + 1] = (byte) (temp >> 8); }
		 *//*
		 * double sum =0,av=0;
		 *//*
			 * for (int i=0;i < buffer.length;i++){
			 * System.out.println(buffer[i]); sum = sum + Math.abs(buffer[i]);
			 * 
			 * }
			 *//*
			 * 
			 * double maxP = 0,minP =0; double maxN = 0,minN =0; for (int i = 0;
			 * i < buffer.length; i++) { if (buffer[i] > maxP) maxP = buffer[i];
			 * } boolean flagp = true; boolean flagn = true; for (int i = 0; i <
			 * buffer.length; i++) { if (buffer[i] > 0) { if (flagp){minP =
			 * buffer[i]; flagp = false;} else if (buffer[i] < minP) { minP =
			 * buffer[i]; } } } for (int i = 0; i < buffer.length; i++) { if
			 * (buffer[i] < maxN) maxN = buffer[i]; } for (int i = 0; i <
			 * buffer.length; i++) { if (buffer[i] < 0) { if (flagn){minN =
			 * buffer[i]; flagn = false;} else if (buffer[i] > minN) minN =
			 * buffer[i]; } } av = sum/buffer.length; byte[] bytes = new
			 * byte[buffer.length]; short[] shortbytes = new
			 * short[buffer.length];
			 * 
			 * // byte [] units = new byte[buffer.length*8]; for (int i=0; i <
			 * buffer.length; i++) {
			 *//*
				 * if ((buffer[i]) * 1/minP > 127){ bytes[i] = 127; } else {
				 * bytes[i] = ((byte) ((buffer[i]) * 1/minP)
				 *//**//** 0.925*lists.size() */
	/**//**//**//** 1.85 */
	/**//*
		 * ); }
		 * 
		 * if ((buffer[i]) * 1/minP < -128){ bytes[i] = -128; } else { bytes[i]
		 * = (byte) ((byte) ((buffer[i]) * 1/minP)
		 *//**//** 0.925*lists.size() */
	/**//**//**//** 1.85 *//**/
	/*
	 * ); }
	 *//*
		 * shortbytes[i] = (short) ((short) ((buffer[i]) * 1/minP)+128); if
		 * (shortbytes[i] > 127) { shortbytes[i] = 127; } bytes[i] = (byte)
		 * shortbytes[i]; System.out.println(shortbytes[i]);
		 *//*
		 * ByteBuffer.wrap(bytes).putDouble(buffer[i]);
		 * 
		 * bytes = ByteBuffer.allocate(8).putDouble(buffer[i]).array(); units[i]
		 * = bytes[0];
		 *//*
			 * }
			 * 
			 * ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			 * AudioFormat format = new AudioFormat(22050, 8, 1, true, false);
			 * // AudioInputStream stream = new AudioInputStream(new
			 * ByteArrayInputStream(data), format, data.length);
			 * AudioInputStream stream = new AudioInputStream(bais, format,
			 * buffer.length); AudioSystem.write(stream,
			 * AudioFileFormat.Type.WAVE, outputFile); AudioFileFormat stream1 =
			 * AudioSystem.getAudioFileFormat(outputFile);
			 * 
			 * }
			 */

}
