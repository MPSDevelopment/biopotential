package com.mpsdevelopment.biopotential.server.gui.analysis;

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

    private Map<String, Double> mapMax;
    private Map<String, Double> mapPo;

    @FXML
    private BarChart<Number, Number> histogramBarChart;

    @FXML
    private Button printButton;

    @FXML
    private Button closeButton;

    @FXML
    private Pane pane;

    private Stage primaryStage;

    public Map<String, Double> getMapMax() {
        return mapMax;
    }

    public void setMapMax(Map<String, Double> mapMax) {
        this.mapMax = mapMax;
    }

    public Map<String, Double> getMapPo() {
        return mapPo;
    }

    public void setMapPo(Map<String, Double> mapPo) {
        this.mapPo = mapPo;
    }

    public BarChartPanelController() {
//        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        String[] systems = {"ALLERGY система", "CARDIO система", "DERMA система", "Endocrinology система", "GASTRO система", "IMMUN система", "MENTIS система", "NEURAL система", "ORTHO система",
                "SPIRITUS система", "Stomat система", "UROLOG система", "VISION система"};
        ObservableList<XYChart.Series<Number, Number>> barChartData = FXCollections.observableArrayList(
                new BarChart.Series("Max", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], mapMax.get("ALLERGY система")),
                        new BarChart.Data(systems[1], mapMax.get("CARDIO система")),
                        new BarChart.Data(systems[2], mapMax.get("DERMA система")),
                        new BarChart.Data(systems[3], mapMax.get("Endocrinology система")),
                        new BarChart.Data(systems[4], mapMax.get("GASTRO система")),
                        new BarChart.Data(systems[5], mapMax.get("IMMUN система")),
                        new BarChart.Data(systems[6], mapMax.get("MENTIS система")),
                        new BarChart.Data(systems[7], mapMax.get("NEURAL система")),
                        new BarChart.Data(systems[8], mapMax.get("ORTHO система")),
                        new BarChart.Data(systems[9], mapMax.get("SPIRITUS система")),
                        new BarChart.Data(systems[10], mapMax.get("Stomat система")),
                        new BarChart.Data(systems[11], mapMax.get("UROLOG система")),
                        new BarChart.Data(systems[12], mapMax.get("VISION система"))
                )),
                new BarChart.Series("Po", FXCollections.observableArrayList(
                        new BarChart.Data(systems[0], mapPo.get("ALLERGY система")),
                        new BarChart.Data(systems[1], mapPo.get("CARDIO система")),
                        new BarChart.Data(systems[2], mapPo.get("DERMA система")),
                        new BarChart.Data(systems[3], mapPo.get("Endocrinology система")),
                        new BarChart.Data(systems[4], mapPo.get("GASTRO система")),
                        new BarChart.Data(systems[5], mapPo.get("IMMUN система")),
                        new BarChart.Data(systems[6], mapPo.get("MENTIS система")),
                        new BarChart.Data(systems[7], mapPo.get("NEURAL система")),
                        new BarChart.Data(systems[8], mapPo.get("ORTHO система")),
                        new BarChart.Data(systems[9], mapPo.get("SPIRITUS система")),
                        new BarChart.Data(systems[10], mapPo.get("Stomat система")),
                        new BarChart.Data(systems[11], mapPo.get("UROLOG система")),
                        new BarChart.Data(systems[12], mapPo.get("VISION система"))

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
