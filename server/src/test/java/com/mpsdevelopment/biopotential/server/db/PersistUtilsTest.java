package com.mpsdevelopment.biopotential.server.db;

import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
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

    private static ServiceRegistry serviceRegistry;


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
        Session session = sessionFactory.openSession();
        Criteria queryNewDb = session.createCriteria(Folder.class).setCacheable(false);
        List<Folder> foldersFromNewDb = queryNewDb.list();
        Assert.assertEquals(14,foldersFromNewDb.size());

        LOGGER.info("folders size %s", foldersFromNewDb.size());
        sessionFactory.close();

        Configuration configuration = new Configuration().addResource("hibernate.cfg.xml");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:file:./data/databaseArk;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MVCC=true;MODE=ORACLE;AUTO_SERVER=TRUE;INIT=CREATE SCHEMA IF NOT EXISTS main");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.password", "MiumVa");
        configuration.setProperty("hibernate.connection.username", "root");

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure().loadProperties("hibernateArk.properties").build();
        sessionFactory = new Configuration().buildSessionFactory(serviceRegistry);

//        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting("hibernate.cfg.xml",configuration.getProperties()).build();
//        sessionFactory = configuration.buildSessionFactory();
        LOGGER.info("Connection changed to %s", configuration.getProperty("hibernate.connection.url"));

        session = sessionFactory.openSession();

        Criteria query = session.createCriteria(Folder.class).setCacheable(false);
        List<Folder> folders = query.list();
        LOGGER.info("folders size %s", folders.size());
        Assert.assertEquals(5,folders.size());

        Criteria queryUsers = sessionFactory.openSession().createCriteria(User.class).setCacheable(false);
        List<User> users = queryUsers.list();
        LOGGER.info("users size %s", users.size());

        sessionFactory.close();
        Assert.assertTrue(sessionFactory.isClosed());
        LOGGER.info("Close sessionFactory");

    }

}
