package com.mpsdevelopment.biopotential.server.db.pojo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SystemDataTable {
    private String name;
    private double maxLevel;
    private double poLevel;
    private String description;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMaxLevel() {
        return maxLevel;
    }

    private void setMaxLevel(double maxLevel) {
        this.maxLevel = maxLevel;
    }

    public double getPoLevel() {
        return poLevel;
    }

    private void setPoLevel(double poLevel) {
        this.poLevel = poLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public static ObservableList<SystemDataTable> createDataTableObject(Map<String, Double> systemMap1, Map<String, Double> systemMap2) {
        ObservableList<SystemDataTable> systemDataTables = FXCollections.observableArrayList();

        String[] systems = {"ALLERGY система", "CARDIO система","DERMA система","Endocrinology система", "GASTRO система","IMMUN система","MENTIS система","NEURAL система"
                ,"ORTHO система","SPIRITUS система","Stomat система","UROLOG система","VISION система"};

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

        systemDataTables.forEach(new Consumer<SystemDataTable>() {
            @Override
            public void accept(SystemDataTable systemDataTable) {
                for (String system : systems) {
                    if (system.substring(0,2).contains(systemDataTable.getName().substring(0,2))) {
                        systemDataTable.setDescription(system);

                    }
                }
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
