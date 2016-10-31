package com.mpsdevelopment.biopotential.server.cmp.analyzer;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChunkSummary {

    @Expose
    @SerializedName("m")
    private final double meanDeviation;

    @Expose
    @SerializedName("d")
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
