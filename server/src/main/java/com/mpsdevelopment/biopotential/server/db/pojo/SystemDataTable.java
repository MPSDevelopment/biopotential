package com.mpsdevelopment.biopotential.server.db.pojo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.function.BiConsumer;

public class SystemDataTable {
    private String name;
    private double maxLevel;
    private double poLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(double maxLevel) {
        this.maxLevel = maxLevel;
    }

    public double getPoLevel() {
        return poLevel;
    }

    public void setPoLevel(double poLevel) {
        this.poLevel = poLevel;
    }



    public static ObservableList<SystemDataTable> createDataTableObject(Map<String, Double> systemMap1, Map<String, Double> systemMap2) {
        ObservableList<SystemDataTable> systemDataTables = FXCollections.observableArrayList();

        systemMap1.forEach(new BiConsumer<String, Double>() {
            @Override
            public void accept(String s, Double dou) {
                SystemDataTable systemDataTable = new SystemDataTable();
                systemDataTable.setName(s);
                systemDataTable.setMaxLevel(dou);
                systemMap2.forEach(new BiConsumer<String, Double>() {
                    @Override
                    public void accept(String ss, Double dou1) {
                        if (s.equals(ss)) {
                            systemDataTable.setPoLevel(dou1);
                        }
                    }
                });
            systemDataTables.addAll(systemDataTable);
            }
        });

        return systemDataTables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemDataTable that = (SystemDataTable) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
