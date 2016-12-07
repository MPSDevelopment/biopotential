package com.mpsdevelopment.biopotential.server.db.pojo;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;

public class DataTable {
    private String name;
    private double dispersion;;
    private String filename;
    private String degree;

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDispersion() {
        return dispersion;
    }

    public void setDispersion(double dispersion) {
        this.dispersion = dispersion;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public static DataTable createDataTableObject(Pattern k, AnalysisSummary v) {
        DataTable dataTable = new DataTable();
        dataTable.setName(k.getName());
        dataTable.setDispersion(v.getDispersion());
        dataTable.setFilename(k.getFileName());
        if (v.getDegree() == 0) {
            dataTable.setDegree("Max");
        }
        else if (v.getDegree() == -2147483648) {
            dataTable.setDegree("Po");
        }
        return dataTable;
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "name='" + name + '\'' +
                ", dispersion=" + dispersion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataTable dataTable = (DataTable) o;

        return filename != null ? filename.equals(dataTable.filename) : dataTable.filename == null;

    }

    @Override
    public int hashCode() {
        return filename != null ? filename.hashCode() : 0;
    }
}
