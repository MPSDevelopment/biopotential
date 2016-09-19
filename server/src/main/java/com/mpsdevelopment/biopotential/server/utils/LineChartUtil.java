package com.mpsdevelopment.biopotential.server.utils;

import javafx.scene.chart.XYChart;

public class LineChartUtil {

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
}
