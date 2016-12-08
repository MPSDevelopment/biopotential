package com.mpsdevelopment.biopotential.server.gui.converter;

import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.event.ProgressBarEvent;
import com.mpsdevelopment.biopotential.server.gui.ConverterApplication;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import net.engio.mbassy.listener.Handler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CopyTask extends Task<Void> {

    private File file;

    public CopyTask(File file) {
        EventBus.subscribe(this);
        this.file = file;
    }

    private static final Logger LOGGER = LoggerUtil.getLogger(CopyTask.class);
    private DatabaseCreator databaseCreator;
    private UserDao userDao;

    private double i = 0;
        private double delta = 0;

        @Override
        protected Void call() throws Exception {
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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            updateProgress(delta, 1);
            /*for (int i = 0; i < N_ITERATIONS; i++) {
                updateProgress(i + 1, N_ITERATIONS);
                // sleep is used to simulate doing some work which takes some time....
                Thread.sleep(10);
            }*/

            return null;
        }

    @Handler
    public void handleMessage(ProgressBarEvent event) throws Exception {
        LOGGER.info(" Get delta from convert ");
        updateProgress(event.getProgress(), 1);
        LOGGER.info(" updateProgress %f ", event.getProgress());
    }
    };



