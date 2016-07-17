package com.mps.machine.dbs.arkdb;

import com.mps.machine.StrainDB;
import com.mps.machine.strains.EDXStrain;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Collection;

public class ArkDB implements StrainDB {
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

//        try (Connection db = DriverManager.getConnection("jdbc:sqlite:" + url)) {
//            ResultSet pattern = db.createStatement().executeQuery(
//                "SELECT * FROM patterns");
//            while (pattern.getNext()) {
//                PreparedStatement folder_link_st = db.prepareStatement(
//                    "SELECT id_folder FROM link_patterns_to_folders " +
//                    "WHERE id_pattern = ?");
//                folder_link_st.setInt(1, pattern.getInt("id_pattern"));
//
//                PreparedStatement folder_st = db.prepareStatement(
//                    "SELECT * FROM folders WHERE id_folder = ?");
//                folder_st.setInt(1, folder_link_st.executeQuery().getInt(
//                    "id_folder"));
//                ResultSet folder = folder_st.executeQuery();
//            }
//        } catch (SQLException e) {
//            throw new ArkDBException("SQLException: " + e.getMessage());
//        }
    }

    public EDXStrain nextDiseaseStrain() {
        try {
            if (this.patterns.next()) {
                // TODO: first argument should be folder id, not pattern id
                return new EDXStrain(this.patterns.getString("id_pattern"),
                    this.patterns.getString("pattern_name"),
                    this.patterns.getString("pattern_description"),
                    this.patterns.getString("pattern_uid"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public EDXStrain nextHealingStrain() {
        return null;
    }

    public void setDiseaseFolders(String... diseaseFolders) {
        this.diseaseFolders = Arrays.asList(diseaseFolders);
    }

    public void setHealingFolders(String... healingFolders) {
        this.healingFolders = Arrays.asList(healingFolders);
    }

    private String url;
    private ResultSet patterns;
    private Connection db;
    private Collection<String> diseaseFolders;
    private Collection<String> healingFolders;
}
