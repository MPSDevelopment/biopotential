package com.mpsdevelopment.biopotential.server.db;

import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.httpclient.HttpClientFactory;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class PersistUtilsTest {
    private static final Logger LOGGER = LoggerUtil.getLogger(PersistUtilsTest.class);

    @Autowired
    private PersistUtils persistUtils;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private FoldersDao foldersDao;

    @Autowired
    private PatternsDao patternsDao;


    public PersistUtilsTest() {
    }

    @Test
    public void configureSessionFactoryTest() throws HibernateException {
        SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        LOGGER.info("Creating session factory");
        Assert.assertNotNull(sessionFactory);
    }

    @Test
    public void openSessionTest() {
        Session session = persistUtils.openSession();
        LOGGER.info("Open session");
        Assert.assertNotNull(session);
    }

    @Test
    public void closeSessionTest() {
        Session session = persistUtils.openSession();
        session.close();
        Assert.assertFalse(session.isConnected());
        LOGGER.info("Close session");
    }

    @Test
    public void changeConfigureSessionFactoryTest() throws HibernateException {

        Assert.assertEquals(14,foldersDao.findAll().size());
        Assert.assertEquals(363,patternsDao.findAll().size());
        persistUtils.closeSessionFactory();
        sessionManager.getSession().getSessionFactory().close();

        persistUtils.setConfigurationDatabaseFilename("testfiles/databaseArk");
        SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        Session session = sessionFactory.openSession();
        sessionManager.setSession(session);

        Assert.assertEquals(5,foldersDao.findAll().size());
        Assert.assertEquals(856,patternsDao.findAll().size());

    }

    @Test
    public void changeDBTest() throws HibernateException, IOException, SQLException {

        Assert.assertEquals(132,patternsDao.getFromDatabase().size());

        persistUtils.closeSessionFactory();
        persistUtils.setConfigurationDatabaseFilename("./testfiles/test.arkdb");
        SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        Session session = sessionFactory.openSession();
        sessionManager.setSession(session);

        BioHttpClient httpClient = HttpClientFactory.getInstance();
        httpClient.executePostRequest(ControllerAPI.CONVERT_DB + "/convertDB/", "./testfiles/test.arkdb");

        Assert.assertEquals(44,patternsDao.getFromDatabase().size());

        persistUtils.closeSessionFactory();
        persistUtils.setConfigurationDatabaseFilename("./testfiles/db_cutted.db");
        sessionFactory = persistUtils.configureSessionFactory();
        session = sessionFactory.openSession();
        sessionManager.setSession(session);

        Assert.assertEquals(120,patternsDao.getFromDatabase().size());

    }

}
