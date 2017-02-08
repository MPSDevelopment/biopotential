package com.mpsdevelopment.biopotential.server.db.pojo;

import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.function.BiConsumer;

public class CodeTable {
    private String codename;
    private String description;

    public static ObservableList<CodeTable> createDataTableObject(Map<String, String> codeMap) {
        ObservableList<CodeTable> codeTables = FXCollections.observableArrayList();

        codeMap.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                CodeTable dataTable = new CodeTable();
                dataTable.setCodename(s);
                dataTable.setDescription(s2);
                codeTables.addAll(dataTable);
            }


        });

        return codeTables;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
