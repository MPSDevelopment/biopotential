package com.mpsdevelopment.biopotential.server.gui.service;

import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.gui.ConverterApplication;
import com.mpsdevelopment.biopotential.server.gui.converter.ConverterPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

public class JavaFxService extends Service<Void> {

    private static final Logger LOGGER = LoggerUtil.getLogger(ConverterPanelController.class);
    private DatabaseCreator databaseCreator;
    private UserDao userDao;
    private File file;

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws IOException, MalformedURLException {
                LOGGER.info("Void call() method ");

                databaseCreator = ConverterApplication.APP_CONTEXT.getBean(DatabaseCreator.class);
                userDao = ConverterApplication.APP_CONTEXT.getBean(UserDao.class);
                try {
                    List<User> users = userDao.findAll();
                    if (users.size() < 10) {
                        databaseCreator.createUsers();
                    }
                    databaseCreator.convertToH2(file.getAbsolutePath());
                } catch (ArkDBException e) {
                    LOGGER.printStackTrace(e);
                } catch (IOException e) {
                    LOGGER.printStackTrace(e);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }



    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
