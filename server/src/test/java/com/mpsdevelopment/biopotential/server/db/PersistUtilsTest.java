package com.mpsdevelopment.biopotential.server.db;

import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class PersistUtilsTest {
    private static final Logger LOGGER = LoggerUtil.getLogger(PersistUtilsTest.class);

    @Autowired
    private PersistUtils persistUtils;

    @Autowired
    private ServerSettings serverSettings;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FoldersDao foldersDao;


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
        /*SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        LOGGER.info("Creating session factory");
        sessionFactory.close();
        Assert.assertEquals(19, userDao.findAll().size());

        Assert.assertTrue(sessionFactory.isClosed());
        LOGGER.info("Close sessionFactory");*/

        Configuration configuration = new Configuration();

        configuration.setProperty("hibernate.connection.url", "jdbc:sqlite:./data/db_cutted.db" /*+ serverSettings.getDbPath()*/);
        configuration.setProperty("hibernate.dialect", "com.enigmabridge.hibernate.dialect.SQLiteDialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
        List<Folder> folders = foldersDao.findAll();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Assert.assertEquals(19, userDao.findAll().size());
        sessionFactory.close();
        Assert.assertTrue(sessionFactory.isClosed());
        LOGGER.info("Close sessionFactory");


    }

}
