package com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db;

// import org.h2

import com.mps.machine.dbs.h2db.H2DBException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

public class H2DB {
    public H2DB(String url, String user, String password) throws H2DBException {
        try {
            Class.forName("org.h2.Driver");
            Properties props = new Properties();
            props.put("user", user);
            props.put("password", password);
            this.db = DriverManager.getConnection("jdbc:h2:" + url, props);
        } catch (ClassNotFoundException e) {
            throw new H2DBException("ClassNotFoundException: "
                + e.getMessage());
        } catch (SQLException e) {
            throw new H2DBException("SQLException: " + e.getMessage());
        }
    }

    public void setDiseaseFolderIds(Collection<String> diseaseFolderIds) {
        this.diseaseFolderIds = diseaseFolderIds;
    }

    public H2DBIter getDiseases() {
        try {
            return new H2DBIter(this.db);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public H2DBIter getIterForFolder(String folder) {
        try {
            return new H2DBIter(this.db, folder);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection db;
    private Collection<String> diseaseFolderIds;
}
