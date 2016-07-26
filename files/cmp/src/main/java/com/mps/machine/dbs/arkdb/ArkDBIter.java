package com.mps.machine.dbs.arkdb;

import com.mps.machine.StrainDB;
import com.mps.machine.strains.EDXStrain;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArkDBIter implements StrainDB {
    public ArkDBIter(ArkDB db, StrainType strainType) {
        this.db = db;
        this.strainType = strainType;
    }

    public EDXStrain next() {
        try {
            while (this.db.patterns.next()) {
                // TODO: first argument should be folder id, not pattern id
                PreparedStatement id_folder_of_pattern = this.db.db.prepareStatement(
                    "SELECT id_folder FROM link_patterns_to_folders " +
                    "WHERE id_pattern = ?");
                id_folder_of_pattern.setInt(1, this.db.patterns.getInt("id_pattern"));
                ResultSet id_folder_of_pattern_rs = id_folder_of_pattern.executeQuery();
                // TODO: Should use names
                String folder_name = id_folder_of_pattern_rs.getString("id_folder");

                if (this.strainType == StrainType.TYPE_ANY
                        || (this.strainType == StrainType.TYPE_DISEASE
                            && this.db.diseaseFolders.contains(folder_name))
                        || (this.strainType == StrainType.TYPE_HEALING
                            && this.db.healingFolders.contains(folder_name))) {
                    return new EDXStrain(
                        folder_name,
                        this.db.patterns.getString("pattern_name"),
                        this.db.patterns.getString("pattern_description"),
                        this.db.patterns.getString("pattern_uid"));
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArkDB db;
    private StrainType strainType;
}
