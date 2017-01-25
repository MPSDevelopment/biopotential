package com.mpsdevelopment.biopotential.server.utils;


import com.mpsdevelopment.biopotential.server.gui.analysis.AnalysisPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class LineChartUtil {
    private static final Logger LOGGER = LoggerUtil.getLogger(LineChartUtil.class);

    public static  XYChart.Series<Number, Number> createNumberSeries(int[] data) {
        return createNumberSeries(data, 1);
    }

    public static  XYChart.Series<Number, Number> createNumberSeries(int[] data, int rate) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < data.length; i = i + rate) {
            series.getData().add(new XYChart.Data<>(i, data[i]));
        }
        return series;
    }

    /*public static  XYChart.Series<Number, Number> createNumberSeries(double[] data) {
        return createNumberSeries(data, 1);
    }*/
    public static  XYChart.Series<Number, Number> createNumberSeries(double[] data, int rate, long sampleRate) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < data.length; i = i+ rate) {
            series.getData().add(new XYChart.Data<>((float)i/sampleRate, data[i]));
//            System.out.println(Double.toString((float)data[i]));
        }
        return series;
    }

    public static XYChart.Series<Number, Number> chart(double[] data, double pre, long sampleRate) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        GeometryFactory gf = new GeometryFactory();

//        long t0 = System.nanoTime();
        long t1 = System.currentTimeMillis();
        Coordinate[] coordinates = new Coordinate[data.length];
        for (int i = 0; i < data.length; i++) {
            coordinates[i] = new Coordinate(((double)i/(double)sampleRate),data[i]);
        }
        LOGGER.info("Calculate coordinates %d ms", System.currentTimeMillis() - t1);
        Geometry geom = new LineString(new CoordinateArraySequence(coordinates), gf);
        Geometry simplified = DouglasPeuckerSimplifier.simplify(geom, pre);

        long t2 = System.currentTimeMillis();

        List<XYChart.Data<Number, Number>> update = new ArrayList<XYChart.Data<Number, Number>>();
        for (Coordinate each : simplified.getCoordinates()) {
            update.add(new XYChart.Data<>(each.x, each.y));
        }
        LOGGER.info("For XYChart %d ms", System.currentTimeMillis() - t2);

//        long t1 = System.nanoTime();

        /*System.out.println(String.format("Reduces points from %d to %d in %.1f ms", coordinates.length, update.size(),
                (t1 - t0) / 1e6));*/
        long t3 = System.currentTimeMillis();
        ObservableList<XYChart.Data<Number, Number>> list = FXCollections.observableArrayList(update);
        LOGGER.info("List XYChart %d ms", System.currentTimeMillis() - t3);

        long t4 = System.currentTimeMillis();
        series.setData(list);
        LOGGER.info("SetData %d ms", System.currentTimeMillis() - t4);
        return series;
//        graph.getData().add(series);
    }


}
