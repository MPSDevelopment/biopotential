package com.mpsdevelopment.biopotential.server.cmp.analyzer;


public class AnalysisSummary {

    private final double meanDeviation;
    private final double dispersion;
    private final int degree;

    // TODO: "degree" should be enum
    public AnalysisSummary(double meanDeviation, double dispersion, int degree) {
        this.meanDeviation = meanDeviation;
        this.dispersion = dispersion;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalysisSummary that = (AnalysisSummary) o;

        if (Double.compare(that.meanDeviation, meanDeviation) != 0) return false;
        if (Double.compare(that.dispersion, dispersion) != 0) return false;
        return degree == that.degree;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(meanDeviation);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(dispersion);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + degree;
        return result;
    }
}
