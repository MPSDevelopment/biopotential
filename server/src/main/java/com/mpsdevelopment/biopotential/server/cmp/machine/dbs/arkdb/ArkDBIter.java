package com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb;


import com.mpsdevelopment.biopotential.server.cmp.machine.PatternDB;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

class ArkDBIter implements PatternDB {
    ArkDBIter(ResultSet patterns, Collection<Integer> filter) {
        this.patterns = patterns;
        this.filter = filter;
    }

    public EDXPattern next() {
        try {
            while (this.patterns.next()) {
                if (this.filter.contains(this.patterns.getInt("id_folder"))) {
                    try {
                        return new EDXPattern(
                            this.patterns.getString("folder_name"),
                            this.patterns.getString("pattern_name"),
                            this.patterns.getString("pattern_description"),
                            this.patterns.getString("pattern_uid"),
                            null,null);
                    } catch (IOException e) {
                        e.printStackTrace();
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
