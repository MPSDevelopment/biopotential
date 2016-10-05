package com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb;

import com.mps.machine.dbs.arkdb.ArkDBException;

import java.sql.*;
import java.util.Collection;

public class ArkDB {
    public ArkDB(String url) throws ArkDBException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ArkDBException("ClassNotFoundException: "
                + e.getMessage());
        }

        try {
            this.db = DriverManager.getConnection("jdbc:sqlite:" + url);
        } catch (SQLException e) {
            throw new ArkDBException("SQLException: " + e.getMessage());
        }
    }

    public void setDiseaseFolders(Collection<Integer> diseaseIds) {
        this.diseaseIds = diseaseIds;
    }

    public void setHealingFolders(Collection<Integer> healingIds) {
        this.healingIds = healingIds;
    }

    public ArkDBIter getDiseaseIds() {
        return this.genArkDBIter(diseaseIds);
    }

    public ArkDBIter getHealingIds() {
        return this.genArkDBIter(healingIds);
    }

    private ArkDBIter genArkDBIter(Collection<Integer> filter) {
        try {
            ResultSet patterns = this.db.createStatement().executeQuery(
                "SELECT folders.id_folder,"
                + "     folders.folder_name,"
                + "     patterns.id_pattern,"
                + "     pattern_name,"
                + "     pattern_description,"
                + "     pattern_uid\n"
                + "FROM patterns\n"
                + "JOIN link_patterns_to_folders"
                + " ON patterns.id_pattern = link_patterns_to_folders.id_pattern\n"
                + "JOIN folders"
                + " ON folders.id_folder = link_patterns_to_folders.id_folder\n");
            return new ArkDBIter(patterns, filter);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    Collection<Integer> diseaseIds;
    Collection<Integer> healingIds;
    Connection db;
}
