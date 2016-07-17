package com.mps.analyzer;


public class ChunkSummary {
    public ChunkSummary(double meanDeviation, double dispersion) {
        this.meanDeviation = meanDeviation;
        this.dispersion    = dispersion;
    }

    public double getMeanDeviation() {
        return this.meanDeviation;
    }

    public double getDispersion() {
        return this.dispersion;
    }

    private final double meanDeviation;
    private final double dispersion;
}
