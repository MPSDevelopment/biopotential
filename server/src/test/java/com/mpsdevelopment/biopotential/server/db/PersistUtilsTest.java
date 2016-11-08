package com.mpsdevelopment.biopotential.server.db;

import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class PersistUtilsTest {
    private static final Logger LOGGER = LoggerUtil.getLogger(PersistUtilsTest.class);

    @Autowired
    private PersistUtils persistUtils;

    @Autowired
    private ServerSettings serverSettings;


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
    public void changeconfigureSessionFactoryTest() throws HibernateException {
        Session session = persistUtils.openSession();
        session.close();
        Assert.assertFalse(session.isConnected());
        LOGGER.info("Close session");

        Configuration configuration = null;
        configuration = new Configuration();

        configuration.setProperty("hibernate.connection.url", "jdbc:h2:file:" + serverSettings.getDbPath());
		configuration.setProperty("hibernate.connection.username", "root");
		configuration.setProperty("hibernate.connection.password", "MiumVa");

        configuration.configure();

        session = persistUtils.openSession();
        session.close();
        Assert.assertFalse(session.isConnected());
        LOGGER.info("Close session");


    }

}
