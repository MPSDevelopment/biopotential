package com.mpsdevelopment.biopotential.server.db.pojo;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class HumanPoints {
    private String name;
    private double dispersion;
    ;
    private String degree;

    public HumanPoints() {

    }


    public static HumanPoints createObject(EDXPattern k) {
        HumanPoints dataTable = new HumanPoints();
        dataTable.setName(k.getName());

        return dataTable;
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
}



