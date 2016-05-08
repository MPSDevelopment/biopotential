package com.mps.analyzer;


public class AnalysisSummary {
    // TODO: "degree" should be enum
    public AnalysisSummary(double meanDeviation,
                           double dispersion,
                           int degree) {
        this.meanDeviation = meanDeviation;
        this.dispersion    = dispersion;
        this.degree = degree;
    }

    public double getMeanDeviation() {
        return meanDeviation;
    }

    public double getDispersion() {
        return dispersion;
    }

    public int getDegree() {
        return degree;
    }

    private final double meanDeviation;
    private final double dispersion;
    private final int degree;
}
