package com.mps.analyzer;


public class ChunkSummary {
    public ChunkSummary(double meanDeviation, double dispersion) {
        this.meanDeviation = meanDeviation;
        this.dispersion    = dispersion;
    }

    public double getMeanDeviation() {
        return meanDeviation;
    }

    public double getDispersion() {
        return dispersion;
    }

    private final double meanDeviation;
    private final double dispersion;
}
