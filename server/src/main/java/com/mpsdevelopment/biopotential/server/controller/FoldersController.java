package com.mpsdevelopment.biopotential.server.controller;

import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.PersistUtils;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Lob;
import java.io.IOException;
import java.sql.SQLException;

@RequestMapping(ControllerAPI.FOLDERS_CONTROLLER)
@Controller
public class FoldersController {

    private static final Logger LOGGER = LoggerUtil.getLogger(FoldersController.class);

    @Autowired
    private PersistUtils persistUtils;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private DatabaseCreator databaseCreator;

    public FoldersController() {
        LOGGER.info("Create FoldersController");
    }

    @RequestMapping(value = "/convertDB", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> getDiseases(@RequestBody String url) throws SQLException {

        restartSessionManager(url);
        try {
            databaseCreator.convertToH2(url);
        } catch (ArkDBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        databaseCreator.createUsers();

        LOGGER.info("convert complete ");
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }

    private void restartSessionManager(String url) {
        persistUtils.closeSessionFactory();
        persistUtils.setConfigurationDatabaseFilename(url);
        SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        Session session = sessionFactory.openSession();
        sessionManager.setSession(session);
    }

}

