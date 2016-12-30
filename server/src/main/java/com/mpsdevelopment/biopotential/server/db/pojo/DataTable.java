package com.mpsdevelopment.biopotential.server.db.pojo;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DataTable {
    private String name;
    private String catalogName;
    private double dispersion;;
    private String filename;
    private String degree;
    private BooleanProperty myCheck;

    public DataTable(boolean checked) {
        myCheck = new SimpleBooleanProperty(checked);
    }

    public BooleanProperty checkProperty() { return myCheck; }

    public static DataTable createDataTableObject(EDXPattern k) {
        DataTable dataTable = new DataTable(true);
        dataTable.setName(k.getName());
        dataTable.setCatalogName(k.getKind());
        dataTable.setFilename(k.getFileName());

        return dataTable;
    }

    public static DataTable createDataTableObject(Pattern k, AnalysisSummary v) {
        DataTable dataTable = new DataTable(true);
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

    public static DataTable createDataTableObject(Pattern k) {
        DataTable dataTable = new DataTable(true);
        dataTable.setName(k.getName());
        dataTable.setFilename(k.getFileName());

        return dataTable;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

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
