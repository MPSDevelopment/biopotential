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
        return this.meanDeviation;
    }

    public double getDispersion() {
        return this.dispersion;
    }

    public int getDegree() {
        return this.degree;
    }

    private final double meanDeviation;
    private final double dispersion;
    private final int degree;
}
