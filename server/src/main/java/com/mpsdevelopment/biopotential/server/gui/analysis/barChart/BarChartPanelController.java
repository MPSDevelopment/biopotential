package com.mpsdevelopment.biopotential.server.gui.analysis.barChart;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.db.pojo.SystemDataTable;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.utils.PanelUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;

public class BarChartPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(BarChartPanelController.class);

    private static final int AVAILABLE_COLORS = 10;
    private static final int CASPIAN_COLOR_COUNTS = 8;

    private Map<String, Double> mapMax;
    private Map<String, Double> mapPo;

    @FXML
    private BarChart<Number, Number> histogramBarChart;

    @FXML
    private TableColumn column1;

    @FXML
    private Button printButton;

    @FXML
    private Button closeButton;

    @FXML
    private TextField text1;

    @FXML
    private TextField text2;

    @FXML
    private TextField text3;

    @FXML
    private TextField text4;

    @FXML
    private TextField text5;

    @FXML
    private TextField text6;

    @FXML
    private TextField text7;

    @FXML
    private TextField text8;

    @FXML
    private TextField text9;

    @FXML
    private TextField text10;

    @FXML
    private TextField text11;

    @FXML
    private TextField text12;

    @FXML
    private TextField text13;

    @FXML
    private Pane pane;

    private Stage primaryStage;

    public Map<String, Double> getMapMax() {
        return mapMax;
    }

    protected void setMapMax(Map<String, Double> mapMax) {
        this.mapMax = mapMax;
    }

    public Map<String, Double> getMapPo() {
        return mapPo;
    }

    protected void setMapPo(Map<String, Double> mapPo) {
        this.mapPo = mapPo;
    }

    public BarChartPanelController() {
//        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        histogramBarChart.setTitle("График состояния");
        printButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PanelUtils.saveToImage(pane, primaryStage);
//                saveToImage();
            }
        });

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });
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

    private void display() {

        ObservableList<SystemDataTable> datas = FXCollections.observableArrayList();
        datas.addAll(SystemDataTable.createDataTableObject(mapMax, mapPo));

        String[] systems = {"AL", "Ca", "De", "En", "Ga", "Im", "Me", "Ne", "Or", "Sp", "St", "Ur", "Vi"};
        String[] allsystems = {" - ALLERGY система", " - CARDIO система", " - DERMA система", " - Endocrinology система", " - GASTRO система", " - IMMUN система", " - MENTIS система",
                " - NEURAL система", " - ORTHO система", " - SPIRITUS система", " - Stomat система", " - UROLOG система", " - VISION система"};

        text1.setText(systems[0] + allsystems[0]);
        text2.setText(systems[1] + allsystems[1]);
        text3.setText(systems[2] + allsystems[2]);
        text4.setText(systems[3] + allsystems[3]);
        text5.setText(systems[4] + allsystems[4]);
        text6.setText(systems[5] + allsystems[5]);
        text7.setText(systems[6] + allsystems[6]);
        text8.setText(systems[7] + allsystems[7]);
        text9.setText(systems[8] + allsystems[8]);
        text10.setText(systems[9] + allsystems[9]);
        text11.setText(systems[10] + allsystems[10]);
        text12.setText(systems[11] + allsystems[11]);
        text13.setText(systems[12] + allsystems[12]);

        ObservableList<XYChart.Series<Number, Number>> barChartData = FXCollections.observableArrayList(
                new BarChart.Series("Max", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], mapMax.get("Al")),
                        new BarChart.Data(systems[1], mapMax.get("Ca")),
                        new BarChart.Data(systems[2], mapMax.get("De")),
                        new BarChart.Data(systems[3], mapMax.get("En")),
                        new BarChart.Data(systems[4], mapMax.get("Ga")),
                        new BarChart.Data(systems[5], mapMax.get("Im")),
                        new BarChart.Data(systems[6], mapMax.get("Me")),
                        new BarChart.Data(systems[7], mapMax.get("Ne")),
                        new BarChart.Data(systems[8], mapMax.get("Or")),
                        new BarChart.Data(systems[9], mapMax.get("Sp")),
                        new BarChart.Data(systems[10], mapMax.get("St")),
                        new BarChart.Data(systems[11], mapMax.get("Ur")),
                        new BarChart.Data(systems[12], mapMax.get("Vi"))
                )),
                new BarChart.Series("Po", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], mapPo.get("Al")),
                        new BarChart.Data(systems[1], mapPo.get("Ca")),
                        new BarChart.Data(systems[2], mapPo.get("De")),
                        new BarChart.Data(systems[3], mapPo.get("En")),
                        new BarChart.Data(systems[4], mapPo.get("Ga")),
                        new BarChart.Data(systems[5], mapPo.get("Im")),
                        new BarChart.Data(systems[6], mapPo.get("Me")),
                        new BarChart.Data(systems[7], mapPo.get("Ne")),
                        new BarChart.Data(systems[8], mapPo.get("Or")),
                        new BarChart.Data(systems[9], mapPo.get("Sp")),
                        new BarChart.Data(systems[10], mapPo.get("St")),
                        new BarChart.Data(systems[11], mapPo.get("Ur")),
                        new BarChart.Data(systems[12], mapPo.get("Vi"))

                )));

        final XYChart.Series<Number, Number> maxSeries = barChartData.get(barChartData.size() - 2);
        for (final XYChart.Data<Number, Number> data : maxSeries.getData()) {
            data.nodeProperty().addListener(new ChangeListener<Node>() {
                @Override
                public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {
                    if (node != null) {
//                        setNodeStyle(data);
                        displayLabelForData(data);
                    }
                }
            });
        }

        final XYChart.Series<Number, Number> poSeries = barChartData.get(barChartData.size() - 1);
        for (final XYChart.Data<Number, Number> data : poSeries.getData()) {
            data.nodeProperty().addListener(new ChangeListener<Node>() {
                @Override
                public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {
                    if (node != null) {
//                        setNodeStyle(data);
                        displayLabelForData(data);
                    }
                }
            });
        }


        histogramBarChart.getYAxis().setLabel("%");
        histogramBarChart.getYAxis().setStyle("-fx-fill: #171eb2;");
        histogramBarChart.getData().addAll(barChartData);
        histogramBarChart.setBarGap(0.0);

        for (int i = 0; i < barChartData.size(); i++) {
            for (Node node : histogramBarChart.lookupAll(".series" + i)) {
                node.getStyleClass().remove("default-color" + (i % CASPIAN_COLOR_COUNTS));
                node.getStyleClass().add("default-color" + (i % AVAILABLE_COLORS));
            }
        }
        histogramBarChart.getStylesheets().add("barchart.css");


    }

    /** places a text label with a bar's value above a bar node for a given XYChart.Data */
    private void displayLabelForData(XYChart.Data<Number, Number> data) {
        final Node node = data.getNode();
         double val = new BigDecimal((Double) data.getYValue()).setScale(2, RoundingMode.UP).doubleValue();
        final Text dataText = new Text(val + "");
        node.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
                Group parentGroup = (Group) parent;
                parentGroup.getChildren().add(dataText);
            }
        });

        node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                dataText.setLayoutX(
                        Math.round(
                                bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2));
                dataText.setLayoutY(
                        Math.round(
                                bounds.getMinY() - dataText.prefHeight(-1) * 0.4) + 4);
            }
        });
    }

    public void displayBar() {
        display();
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

}
