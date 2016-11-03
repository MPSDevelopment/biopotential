package com.mpsdevelopment.biopotential.server.db.pojo;

public class DataTable {
    private String name;
    private double dispersion;
    private String filename;

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
