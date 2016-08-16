package com.mpsdevelopment.biopotential.server;

import com.mps.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class DatabaseCreatorTest {

    private static final com.mpsdevelopment.plasticine.commons.logging.Logger LOGGER = LoggerUtil.getLogger(DatabaseCreatorTest.class);

    @Autowired
    public DatabaseCreator databaseCreator;

    @Autowired
    public FoldersDao foldersDao;
    @Autowired
    private PatternsDao patternsDao;


    @Test
    public void convertToH2Test() throws ArkDBException {

        LOGGER.info("databaseCreator %s", databaseCreator);

        try {
            databaseCreator.convertToH2("test.arkdb");
        } catch (ArkDBException e) {
            e.printStackTrace();
        }

        List list = foldersDao.findAll();
        Assert.assertTrue(list.size() == 5);
        list.clear();
        list = (patternsDao.findAll());
        Assert.assertTrue(list.size() == 856);



    }


}
