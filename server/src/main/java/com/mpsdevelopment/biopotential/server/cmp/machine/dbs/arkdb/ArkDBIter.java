package com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb;

import com.mps.machine.StrainDB;
import com.mps.machine.strains.EDXStrain;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

class ArkDBIter implements StrainDB {
    ArkDBIter(ResultSet patterns, Collection<Integer> filter) {
        this.patterns = patterns;
        this.filter = filter;
    }

    public EDXStrain next() {
        try {
            while (this.patterns.next()) {
                if (this.filter.contains(this.patterns.getInt("id_folder"))) {
                    try {
                        return new EDXStrain(
                            this.patterns.getString("folder_name"),
                            this.patterns.getString("pattern_name"),
                            this.patterns.getString("pattern_description"),
                            "edxfiles/" + this.patterns.getString("pattern_uid"),
                            null);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // ???
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private final ResultSet patterns;
    private final Collection<Integer> filter;
}
