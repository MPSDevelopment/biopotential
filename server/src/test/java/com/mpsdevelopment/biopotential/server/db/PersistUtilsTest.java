package com.mpsdevelopment.biopotential.server.db;

import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
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
        SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        Criteria queryNewDb = sessionFactory.openSession().createCriteria(Folder.class).setCacheable(false);
        List<Folder> foldersFromNewDb = queryNewDb.list();

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:file:./data/databaseArk" /*+ serverSettings.getDbPath()*/);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");

        sessionFactory = configuration.buildSessionFactory();
        Criteria query = sessionFactory.openSession().createCriteria(Folder.class).setCacheable(false);
        List<Folder> folders = query.list();

        sessionFactory.close();
        Assert.assertTrue(sessionFactory.isClosed());
        LOGGER.info("Close sessionFactory");

    }

}
