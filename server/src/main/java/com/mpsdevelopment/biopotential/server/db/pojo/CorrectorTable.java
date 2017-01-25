package com.mpsdevelopment.biopotential.server.db.pojo;


import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;


public class CorrectorTable {
    private String name;
    private String catalogName;

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static CorrectorTable createCorrectorsTableObject(EDXPattern k) {
        CorrectorTable dataTable = new CorrectorTable();
        dataTable.setName(k.getName());
        dataTable.setCatalogName(k.getKind());


        return dataTable;
    }


}
