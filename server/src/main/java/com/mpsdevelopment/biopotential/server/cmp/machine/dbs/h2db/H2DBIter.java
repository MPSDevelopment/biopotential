package com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db;

import com.mpsdevelopment.biopotential.server.cmp.machine.PatternDB;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;

import java.io.IOException;
import java.sql.*;

public class H2DBIter implements PatternDB {

    protected final Connection db;
    protected final ResultSet patterns;

    H2DBIter(Connection db) throws SQLException {
        this.db = db;
        this.patterns = this.db.createStatement().executeQuery(
            "SELECT IDFOLDER,"
            + "     IDPATTERN,"
            + "     PATTERNNAME,"
            + "     PATTERNDESCRIPTION,"
            + "     PATTERNUID,"
            + "     FOLDERNAME,"
            + "     FOLDER_ID,"
            + "     CORRECTORS,"
            + "     PATTERN_ID\n"
            + "FROM MAIN.FOLDER\n"
            + "JOIN MAIN.PATTERNS_FOLDERS"
            + "     ON FOLDER.ID = PATTERNS_FOLDERS.FOLDER_ID\n"
            + "JOIN MAIN.PATTERN"
            + "     ON PATTERN.ID = PATTERNS_FOLDERS.PATTERN_ID\n"
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
            + "     FOLDER_ID,"
            + "     CORRECTORS,"
            + "     PATTERN_ID\n"
            + "FROM MAIN.FOLDER\n"
            + "JOIN MAIN.PATTERNS_FOLDERS"
            + "     ON FOLDER.ID = PATTERNS_FOLDERS.FOLDER_ID\n"
            + "JOIN MAIN.PATTERN"
            + "     ON PATTERN.ID = PATTERNS_FOLDERS.PATTERN_ID\n"
            + "WHERE FOLDER_ID = ?");
        ps.setString(1, filter);
        this.patterns = ps.executeQuery();
    }

    public EDXPattern next() {
        try {
            while (this.patterns.next()) {
                try {
                    return new EDXPattern(
                        this.patterns.getString("FOLDERNAME"),
                        this.patterns.getString("PATTERNNAME"),
                        this.patterns.getString("PATTERNDESCRIPTION"),
                        "./data/edxfiles/" + this.patterns.getString("PATTERNUID"),
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


}
