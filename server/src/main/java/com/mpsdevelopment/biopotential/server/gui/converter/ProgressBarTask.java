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

public class ProgressBarTask extends Task<Void> {

    private static final Logger LOGGER = LoggerUtil.getLogger(ProgressBarTask.class);

    public ProgressBarTask() {
        EventBus.subscribe(this);
    }


        @Override
        protected Void call() throws Exception {
            return null;
        }

    @Handler
    public void handleMessage(ProgressBarEvent event) throws Exception {
//        LOGGER.info(" Get delta from convert ");
        updateProgress(event.getProgress(), 1);
//        LOGGER.info(" updateProgress %f ", event.getProgress());
    }
}



