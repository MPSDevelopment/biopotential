package com.mps.machine.dbs.arkdb;

import com.mps.machine.strains.EDXStrain;

import java.sql.*;
import java.util.Arrays;
import java.util.Collection;

public class ArkDB {
    public ArkDB(String url) throws ArkDBException {
        this.url = url;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ArkDBException(
                "ClassNotFoundException: " + e.getMessage());
        }

        try {
            this.db = DriverManager.getConnection("jdbc:sqlite:" + this.url);
            this.patterns = this.db.createStatement().executeQuery(
                "SELECT * FROM patterns");
        } catch (SQLException e) {
            throw new ArkDBException("SQLException: " + e.getMessage());
        }
    }

    public void setDiseaseFolders(String... diseaseFolders) {
        this.diseaseFolders = Arrays.asList(diseaseFolders);
    }

    public void setHealingFolders(String... healingFolders) {
        this.healingFolders = Arrays.asList(healingFolders);
    }

    public ArkDBIter getDiseases() {
        return new ArkDBIter(this, StrainType.TYPE_ANY); //StrainType.TYPE_DISEASE);
    }

    public ArkDBIter getHealings() {
        return new ArkDBIter(this, StrainType.TYPE_HEALING);
    }

    ResultSet patterns;
    Collection<String> diseaseFolders;
    Collection<String> healingFolders;
    String url;
    Connection db;
}
