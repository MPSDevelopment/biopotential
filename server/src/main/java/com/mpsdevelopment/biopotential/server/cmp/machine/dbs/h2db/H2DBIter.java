package com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db;

import com.mps.machine.StrainDB;
import com.mps.machine.strains.EDXStrain;
import org.h2.command.Prepared;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Collection;

class H2DBIter implements StrainDB {
    H2DBIter(Connection db) throws SQLException {
        this.db = db;
        this.patterns = this.db.createStatement().executeQuery(
            "SELECT IDFOLDER,"
            + "     IDPATTERN,"
            + "     PATTERNNAME,"
            + "     PATTERNDESCRIPTION,"
            + "     PATTERNUID,"
            + "     FOLDERNAME,"
            + "     FOLDERS_ID,"
            + "     CORRECTORS,"
            + "     PATTERNS_ID\n"
            + "FROM MAIN.FOLDERS\n"
            + "JOIN MAIN.PATTERNS_FOLDERS"
            + "     ON FOLDERS.ID = PATTERNS_FOLDERS.FOLDERS_ID\n"
            + "JOIN MAIN.PATTERNS"
            + "     ON PATTERNS.ID = PATTERNS_FOLDERS.PATTERNS_ID\n"
            + "WHERE CORRECTORS IS NOT NULL");
    }

    H2DBIter(Connection db, String filter) throws SQLException {
        this.db = db;

        PreparedStatement ps = this.db.prepareStatement(
            "SELECT IDFOLDER,"
            + "     IDPATTERN,"
            + "     PATTERNNAME,"
            + "     PATTERNDESCRIPTION,"
            + "     PATTERNUID,"
            + "     FOLDERNAME,"
            + "     FOLDERS_ID,"
            + "     CORRECTORS,"
            + "     PATTERNS_ID\n"
            + "FROM MAIN.FOLDERS\n"
            + "JOIN MAIN.PATTERNS_FOLDERS"
            + "     ON FOLDERS.ID = PATTERNS_FOLDERS.FOLDERS_ID\n"
            + "JOIN MAIN.PATTERNS"
            + "     ON PATTERNS.ID = PATTERNS_FOLDERS.PATTERNS_ID\n"
            + "WHERE FOLDERS_ID = ?");
        ps.setString(1, filter);
        this.patterns = ps.executeQuery();
    }

    public EDXStrain next() {
        try {
            while (this.patterns.next()) {
                try {
                    return new EDXStrain(
                        this.patterns.getString("FOLDERNAME"),
                        this.patterns.getString("PATTERNNAME"),
                        this.patterns.getString("PATTERNDESCRIPTION"),
                        "./edxfiles/" + this.patterns.getString("PATTERNUID"),
                        this.patterns.getString("CORRECTORS"));
                } catch (IOException e) {
                    e.printStackTrace();
                    // ???
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected final Connection db;
    protected final ResultSet patterns;
}
