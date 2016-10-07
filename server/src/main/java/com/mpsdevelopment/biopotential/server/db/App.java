package com.mpsdevelopment.biopotential.server.db;

import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    private static final com.mpsdevelopment.plasticine.commons.logging.Logger LOGGER = LoggerUtil.getLogger(App.class);

    public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/web-context.xml","webapp/app-context.xml");

    public static void main(String[] args) {

        DatabaseCreator databaseCreator = APP_CONTEXT.getBean(DatabaseCreator.class);
        LOGGER.info("databaseCreator %s", databaseCreator);
        try {
            databaseCreator.convertToH2("./data/test.arkdb");
        } catch (ArkDBException e) {
            e.printStackTrace();
        }
    }
}
