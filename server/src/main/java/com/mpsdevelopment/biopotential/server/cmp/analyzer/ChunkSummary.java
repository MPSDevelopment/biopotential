package com.mpsdevelopment.biopotential.server.cmp.analyzer;


public class ChunkSummary {

    private final double meanDeviation;
    private final double dispersion;

    public ChunkSummary(double meanDeviation, double dispersion) {
        this.meanDeviation = meanDeviation;
        this.dispersion = dispersion;
    }

    public double getMeanDeviation() {
        return this.meanDeviation;
    }

    public double getDispersion() {
        return this.dispersion;
    }


}
