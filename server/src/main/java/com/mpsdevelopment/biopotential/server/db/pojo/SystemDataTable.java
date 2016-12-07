package com.mpsdevelopment.biopotential.server.db.pojo;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.function.BiConsumer;

public class SystemDataTable {
    private String name;
    private int maxLevel;
    private int poLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getPoLevel() {
        return poLevel;
    }

    public void setPoLevel(int poLevel) {
        this.poLevel = poLevel;
    }



    public static ObservableList<SystemDataTable> createDataTableObject(Map<String, Integer> systemMap1, Map<String, Integer> systemMap2) {
        ObservableList<SystemDataTable> systemDataTables = FXCollections.observableArrayList();

        systemMap1.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer integer) {
                SystemDataTable systemDataTable = new SystemDataTable();
                systemDataTable.setName(s);
                systemDataTable.setMaxLevel(integer);
                systemMap2.forEach(new BiConsumer<String, Integer>() {
                    @Override
                    public void accept(String ss, Integer integer1) {
                        if (s.equals(ss)) {
                            systemDataTable.setPoLevel(integer1);
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
