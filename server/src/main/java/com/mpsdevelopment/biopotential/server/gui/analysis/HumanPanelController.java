package com.mpsdevelopment.biopotential.server.gui.analysis;

import com.mpsdevelopment.biopotential.server.AbstractController;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.DataPoints;
import com.mpsdevelopment.biopotential.server.db.pojo.HumanPoints;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.Subscribable;
import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.settings.ConsoleSettings;
import com.mpsdevelopment.biopotential.server.utils.StageUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

public class HumanPanelController extends AbstractController implements Subscribable {

    private static final Logger LOGGER = LoggerUtil.getLogger(HumanPanelController.class);

    @Autowired
    private ConsoleSettings consoleSettings;

    private Stage primaryStage;

    @FXML
    private ScatterChart scatterChart;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Path shape012;

    @FXML
    private Circle shape004;

    @FXML
    private Path shape007;

    @FXML
    private Path shape016;

    @FXML
    private Circle shape009;

    @FXML
    private Circle shape010;

    @FXML
    private Circle shape017;

    @FXML
    private Circle shape018;

    @FXML
    private Circle shape029;

    @FXML
    private Circle shape037;

    @FXML
    private Path shape019;

    @FXML
    private Path shape028;

    @FXML
    private Path shape035;

    @FXML
    private Path shape036;

    @FXML
    private Path shape042;

    @FXML
    private Path shape043;

    @FXML
    private Path shape046;

    @FXML
    private Path shape047;

    @FXML
    private Path shape050;

    @FXML
    private Path shape006;

    @FXML
    private Path shape014;

    @FXML
    private Path shape015;

    @FXML
    private Path shape022;

    @FXML
    private Path shape023;

    @FXML
    private Path shape026;

    @FXML
    private Path shape027;

    @FXML
    private Path shape033;

    @FXML
    private Path shape034;

    @FXML
    private Path shape008;

    @FXML
    private Path shape003;

    @FXML
    private Path shape001;

    @FXML
    private Path shape002;

    @FXML
    private Path shape005;

    @FXML
    private Path shape011;

    @FXML
    private Path shape013;

    @FXML
    private Path shape020;

    @FXML
    private Path shape021;

    @FXML
    private Path shape024;

    @FXML
    private Path shape025;

    @FXML
    private Path shape031;

    @FXML
    private Path shape032;

    @FXML
    private Path shape041;

    @FXML
    private Path shape040;

    @FXML
    private Path shape030;

    @FXML
    private Path shape039;

    @FXML
    private Path shape038;

    @FXML
    private Path shape044;

    @FXML
    private Path shape045;

    @FXML
    private Path shape048;

    @FXML
    private Path shape049;

    @FXML
    private Path shape052;

    @FXML
    private Path shape053;

    @FXML
    private Path shape051;

    @FXML
    private Path shape114;

    @FXML
    private Circle shape104;

    @FXML
    private Path shape107;

    @FXML
    private Circle shape109;

    @FXML
    private Circle shape110;

    @FXML
    private Circle shape111;

    @FXML
    private Circle shape112;

    @FXML
    private Circle shape127;

    @FXML
    private Circle shape135;

    @FXML
    private Path shape126;

    @FXML
    private Path shape133;

    @FXML
    private Path shape134;

    @FXML
    private Path shape140;

    @FXML
    private Path shape141;

    @FXML
    private Path shape144;

    @FXML
    private Path shape145;

    @FXML
    private Path shape148;

    @FXML
    private Path shape106;

    @FXML
    private Path shape116;

    @FXML
    private Path shape117;

    @FXML
    private Path shape120;

    @FXML
    private Path shape121;

    @FXML
    private Path shape124;

    @FXML
    private Path shape125;

    @FXML
    private Path shape131;

    @FXML
    private Path shape132;

    @FXML
    private Path shape108;

    @FXML
    private Path shape103;

    @FXML
    private Path shape101;

    @FXML
    private Path shape102;

    @FXML
    private Path shape105;

    @FXML
    private Path shape113;

    @FXML
    private Path shape115;

    @FXML
    private Path shape118;

    @FXML
    private Path shape119;

    @FXML
    private Path shape122;

    @FXML
    private Path shape123;

    @FXML
    private Path shape129;

    @FXML
    private Path shape130;

    @FXML
    private Path shape139;

    @FXML
    private Path shape138;

    @FXML
    private Path shape128;

    @FXML
    private Path shape137;

    @FXML
    private Path shape136;

    @FXML
    private Path shape142;

    @FXML
    private Path shape143;

    @FXML
    private Path shape146;

    @FXML
    private Path shape147;

    @FXML
    private Path shape150;

    @FXML
    private Path shape151;

    @FXML
    private Path shape149;

    @FXML
    private Path shape216;

    @FXML
    private Path shape208;

    @FXML
    private Path shape211;

    private Map<Pattern, AnalysisSummary> diseases;
    private List<HumanPoints> list016;
    private List<HumanPoints> list002;
    Map<String, List<HumanPoints>> lists;
    Map<String, DataPoints> poitsMap;
    Map<String, Path> shapeMap;
    Map<String, Circle> circleMap;


    public HumanPanelController() {
//        EventBus.subscribe(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        scatterChart.setTitle("Body Overview");
        scatterChart.setLegendVisible(false);
        scatterChart.getStylesheets().add("scater.css");

        circleMap = new HashMap<>();
        circleMap.put("004", shape004);
        circleMap.put("009", shape009);
        circleMap.put("010", shape010);
        circleMap.put("017", shape017);
        circleMap.put("018", shape018);
        circleMap.put("029", shape029);
        circleMap.put("037", shape037);
        circleMap.put("104", shape104);
        circleMap.put("109", shape109);
        circleMap.put("110", shape110);
        circleMap.put("111", shape111);
        circleMap.put("112", shape112);
        circleMap.put("127", shape127);
        circleMap.put("135", shape135);

        // XValue, YValue for points on human body
        poitsMap = new HashMap<>();
        poitsMap.put("001 ", new DataPoints(12.0, 94.0));
        poitsMap.put("005 ", new DataPoints(14.0, 87.0));
        poitsMap.put("008 ", new DataPoints(10.0, 75.0));
        poitsMap.put("015 ", new DataPoints(8.0, 70.0));
        poitsMap.put("020 ", new DataPoints(18.0, 70.0));

        shapeMap = new HashMap<>();
        shapeMap.put("001", shape001); shapeMap.put("101", shape101);
        shapeMap.put("002", shape002); shapeMap.put("102", shape102);
        shapeMap.put("003", shape003); shapeMap.put("103", shape103); shapeMap.put("105", shape105);
        shapeMap.put("005", shape005); shapeMap.put("106", shape106);
        shapeMap.put("006", shape006); shapeMap.put("107", shape107);
        shapeMap.put("007", shape007); shapeMap.put("108", shape108);
        shapeMap.put("008", shape008); shapeMap.put("113", shape113);
        shapeMap.put("011", shape011); shapeMap.put("114", shape114);
        shapeMap.put("012", shape012); shapeMap.put("115", shape115);
        shapeMap.put("013", shape013); shapeMap.put("116", shape116);
        shapeMap.put("014", shape014); shapeMap.put("117", shape117);
        shapeMap.put("015", shape015); shapeMap.put("118", shape118);
        shapeMap.put("016", shape016); shapeMap.put("119", shape119);
        shapeMap.put("019", shape019); shapeMap.put("120", shape120);
        shapeMap.put("020", shape020); shapeMap.put("121", shape121);
        shapeMap.put("021", shape021); shapeMap.put("122", shape122);
        shapeMap.put("022", shape022); shapeMap.put("123", shape123);
        shapeMap.put("023", shape023); shapeMap.put("124", shape124);
        shapeMap.put("024", shape024); shapeMap.put("125", shape125);
        shapeMap.put("025", shape025); shapeMap.put("126", shape126);
        shapeMap.put("026", shape026); shapeMap.put("128", shape128);
        shapeMap.put("027", shape027); shapeMap.put("129", shape129);
        shapeMap.put("028", shape028); shapeMap.put("130", shape130);
        shapeMap.put("030", shape030); shapeMap.put("131", shape131);
        shapeMap.put("031", shape031); shapeMap.put("132", shape132);
        shapeMap.put("032", shape032); shapeMap.put("133", shape133);
        shapeMap.put("033", shape033); shapeMap.put("134", shape134);
        shapeMap.put("034", shape034); shapeMap.put("136", shape136);
        shapeMap.put("035", shape035); shapeMap.put("137", shape137);
        shapeMap.put("036", shape036); shapeMap.put("138", shape138);
        shapeMap.put("038", shape038); shapeMap.put("139", shape139);
        shapeMap.put("039", shape039); shapeMap.put("140", shape140);
        shapeMap.put("040", shape040); shapeMap.put("141", shape141);
        shapeMap.put("041", shape041); shapeMap.put("142", shape142);
        shapeMap.put("042", shape042); shapeMap.put("143", shape143);
        shapeMap.put("043", shape043); shapeMap.put("144", shape144);
        shapeMap.put("044", shape044); shapeMap.put("145", shape145);
        shapeMap.put("045", shape045); shapeMap.put("146", shape146);
        shapeMap.put("046", shape046); shapeMap.put("147", shape147);
        shapeMap.put("047", shape047); shapeMap.put("148", shape148);
        shapeMap.put("048", shape048); shapeMap.put("149", shape149);
        shapeMap.put("049", shape049); shapeMap.put("150", shape150);
        shapeMap.put("050", shape050); shapeMap.put("151", shape151);
        shapeMap.put("051", shape051);
        shapeMap.put("052", shape052); shapeMap.put("208", shape208);
        shapeMap.put("053", shape053); shapeMap.put("216", shape216);
        shapeMap.put("211", shape211);
//        setVisibleOff();

    }


    public void init() {

        lists = new HashMap<>();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                /*switch (pattern.getName().substring(3,6)){
                    case "005":
                        series.getData().add(new XYChart.Data<>(14.0, 87.0));
                        addSectionToList(pattern, analysisSummary, pattern.getName().substring(3,6));
                        break;
                    case "003":
                        *//*series.getData().add(new XYChart.Data<>(11.0, 87.0));
                        humanPoints[0] = getHumanPoints(pattern, analysisSummary);
                        list002.add(humanPoints[0]);*//*
                        break;

                }*/
                // compare pattern name with section name in pointsMap
                /*poitsMap.forEach(new BiConsumer<String, DataPoints>() {
                    @Override
                    public void accept(String s, DataPoints dataPoints) {
                        LOGGER.info("pattern name %s", pattern.getName().substring(3,7));
                        if (s.equals(pattern.getName().substring(3,7))) {
                            series.getData().add(new XYChart.Data<>(dataPoints.getxValue(), dataPoints.getyValue()));

                            displayShapeOnBody(pattern.getName().substring(3,7));

                            addSectionToList(pattern, analysisSummary, pattern.getName().substring(3,7));

                        }
                    }
                });*/

                circleMap.forEach(new BiConsumer<String, Circle>() {
                    @Override
                    public void accept(String s, Circle circle) {
                        /*if (pattern.getName().length() > 7) {
                            if (s.equals(pattern.getName().substring(3, 7))) {
                                displayCircleOnBody(pattern.getName().substring(3, 7));

                                addSectionToList(pattern, analysisSummary, pattern.getName().substring(3, 7));
                            }
                        }*/

                        String[] patternName = pattern.getName().split(" ");
                        for (String section : patternName) {
                            if (section.equals(s)) {
                                addSectionToList(pattern,analysisSummary,section);
                                if (lists.get(s).size() == 1) {
                                    displayCircleOnBody(section, Color.LIGHTGREEN);
                                }
                                if (lists.get(s).size() >= 2){
                                    displayCircleOnBody(section, Color.RED);
                                }
                            }
                        }
                    }
                });

                shapeMap.forEach(new BiConsumer<String, Path>() {
                    @Override
                    public void accept(String s, Path path) {

                        String[] patternName = pattern.getName().split(" ");
                        for (String section : patternName) {
                            if (section.equals(s)) {
                                addSectionToList(pattern,analysisSummary,section);
                                if (lists.get(s).size() == 1) {
                                    displayShapeOnBody(section, Color.LIGHTGREEN);
                                }
                                if (lists.get(s).size() >= 2){
                                    displayShapeOnBody(section, Color.RED);
                                }
                            }
                        }

                        /*if (pattern.getName().length() > 7) {
                            if (pattern.getName().substring(3, 7).equals(s)) {
                                displayShapeOnBody(pattern.getName().substring(3, 7));

                                addSectionToList(pattern, analysisSummary, pattern.getName().substring(3, 7));
                            }
                        }

                        if (pattern.getName().length() > 11) {
                            if (pattern.getName().substring(7,11).equals(s)) {

                            displayShapeOnBody(pattern.getName().substring(7, 11));

                            addSectionToList(pattern, analysisSummary, pattern.getName().substring(7, 11));
                            }
                        }

                        if (pattern.getName().length() > 15) {
                            if (pattern.getName().substring(11,15).equals(s)) {

                                displayShapeOnBody(pattern.getName().substring(11,15));

                                addSectionToList(pattern, analysisSummary, pattern.getName().substring(11,15));

                            }
                        }

                        if (pattern.getName().length() > 19) {
                            if (pattern.getName().substring(15, 19).equals(s)) {

                                displayShapeOnBody(pattern.getName().substring(15, 19));

                                addSectionToList(pattern, analysisSummary, pattern.getName().substring(15, 19));

                            }
                        }*/
                    }
                });

            }
        });
        scatterChart.getData().addAll(series);

        for (XYChart.Data<Number, Number> data: series.getData()) {
            Node node = data.getNode() ;
            node.setCursor(Cursor.HAND);
            data.getNode().setOnMouseClicked((MouseEvent event) -> {
                System.out.println("you clicked " + data.toString());
                LOGGER.info("x %f y %f", data.getXValue(), data.getYValue());
                Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
                /*double xAxisLoc = scatterChart.getXAxis().sceneToLocal(pointInScene).getX();
                double yAxisLoc = scatterChart.getYAxis().sceneToLocal(pointInScene).getY();
                Number x = (Number) scatterChart.getXAxis().getValueForDisplay(xAxisLoc);
                Number y = (Number) scatterChart.getYAxis().getValueForDisplay(yAxisLoc);
                data.setXValue(x);
                data.setYValue(y);*/
                Tooltip tooltip = new Tooltip('('+data.getXValue().toString()+';'+data.getYValue().toString()+')');
                Tooltip.install(node, tooltip);
                displayPointOnBody((double)data.getXValue(), (double)data.getYValue());
            });

        }
    }

    private void setVisibleOff() {
        shapeMap.forEach(new BiConsumer<String, Path>() {
            @Override
            public void accept(String s, Path path) {
                path.setVisible(false);
            }
        });
        circleMap.forEach(new BiConsumer<String, Circle>() {
            @Override
            public void accept(String s, Circle circle) {
                circle.setVisible(false);
            }
        });
    }

    private void displayShapeOnBody(String str, Color color) {
        Path p = shapeMap.get(str);
        p.setVisible(true);
        p.setCursor(Cursor.HAND);
        p.setFill(Paint.valueOf(String.valueOf(color)));


        p.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                p.setEffect(getDropShadow(color));
            }
        });

        p.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DropShadow dropShadow = new DropShadow();
                p.setEffect(null);
            }
        });

        p.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openWindow(str, lists.get(str));
            }
        });
    }

    private void displayCircleOnBody(String str, Color color) {
        Circle circle = circleMap.get(str);
        circle.setVisible(true);
        circle.setCursor(Cursor.HAND);
        circle.setFill(Paint.valueOf(String.valueOf(color)));


        circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                circle.setEffect(getDropShadow(color));
            }
        });

        circle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DropShadow dropShadow = new DropShadow();
                circle.setEffect(null);
            }
        });

        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openWindow(str, lists.get(str));
            }
        });
    }

    private DropShadow getDropShadow(Color color) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setBlurType(BlurType.GAUSSIAN);
        dropShadow.setColor(color);
        return dropShadow;
    }

    private void addSectionToList(Pattern pattern, AnalysisSummary analysisSummary, String section) {
        List<HumanPoints> list = new ArrayList<>();
//        list.add(getHumanPoints(pattern, analysisSummary));
        List<HumanPoints> getAll = lists.get(section);
        if (getAll == null) {
            list.add(getHumanPoints(pattern, analysisSummary));

        }
        else {
            list.add(getHumanPoints(pattern, analysisSummary));
            list.addAll(getAll);

        }
        lists.put(section, list);
    }

    private HumanPoints getHumanPoints(Pattern pattern, AnalysisSummary analysisSummary) {
        HumanPoints humanPoints = new HumanPoints();
        humanPoints.setName(pattern.getName());
        humanPoints.setDispersion(analysisSummary.getDispersion());
        humanPoints.setDegree("Max");
        return humanPoints;
    }

    private void displayPointOnBody(double xValue, double yValue) {
        /*if (xValue == 14.0 && yValue == 87.0) {
            LOGGER.info("%s", list016.size());
            openWindow("005", list016);
        }*/
    poitsMap.forEach(new BiConsumer<String, DataPoints>() {
        @Override
        public void accept(String s, DataPoints dataPoints) {
            if (xValue == dataPoints.getxValue() && yValue == dataPoints.getyValue()) {
                openWindow(s, lists.get(s));
            }
        }
    });

    }

    private void openWindow(String section, List<HumanPoints> list) {
        DiseasePanel diseasePanel = new DiseasePanel(list);
        Stage diseaseStage = StageUtils.createStage(null, diseasePanel, new StageSettings().setPanelTitle("Section"+section).setClazz(diseasePanel.getClass()).setHeight(327d).setWidth(424d)
                .setHeightPanel(327d).setWidthPanel(424d).setX(StageUtils.getCenterX()).setY(StageUtils.getCenterY()));
        diseasePanel.setPrimaryStage(diseaseStage);
    }

    public Map<Pattern, AnalysisSummary> getDiseases() {
        return diseases;
    }

    public void setDiseases(Map<Pattern, AnalysisSummary> diseases) {
        this.diseases = diseases;
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
