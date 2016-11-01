package com.mpsdevelopment.biopotential.server.db.pojo;

public class DataTable {
    private String name;
    private double dispersion;

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

        if (Double.compare(dataTable.dispersion, dispersion) != 0) return false;
        return name.equals(dataTable.name);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        temp = Double.doubleToLongBits(dispersion);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
