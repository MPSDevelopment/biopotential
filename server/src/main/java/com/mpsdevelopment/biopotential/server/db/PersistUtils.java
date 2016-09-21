package com.mpsdevelopment.biopotential.server.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Properties;

import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;


import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class PersistUtils {

	private static final Logger LOGGER = LoggerUtil.getLogger(PersistUtils.class);

	private SessionFactory sessionFactory;
	private ServiceRegistry serviceRegistry;
	private EventListenerRegistry eventListenerRegistry;
	private ConfigurationService configurationService;
	
//	@Autowired(required = true)
//	private ServerSettings serverSettings;

	public synchronized SessionFactory configureSessionFactory() throws HibernateException {
		LOGGER.info("Creating session factory");
		Configuration configuration = getOrCreateConfiguration();
		
		Properties properties = configuration.getProperties();
		
		sessionFactory = configuration.buildSessionFactory();
		eventListenerRegistry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
		return sessionFactory;
	}

	private synchronized Configuration getOrCreateConfiguration() {
		Configuration configuration = null;
		configuration = new Configuration();
		configuration.configure();
		return configuration;
	}

	/**
	 * Code from here http://stackoverflow.com/questions/9600522/serializing-hibernates- configuration
	 * 
	 * @param cachedConfiguration
	 * @param configuration
	 * @throws java.io.IOException
	 */
	private void write(File cachedConfiguration, Configuration configuration) throws IOException {
		FileOutputStream fout = null;
		ObjectOutputStream out = null;
		try {
			fout = new FileOutputStream(cachedConfiguration, false);
			out = new ObjectOutputStream(fout);
			out.writeObject(configuration);
			out.flush();
		} catch (IOException e) {
			// make sure we close the inputstream by catching any exceptions
			throw e;
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private Configuration read(File cachedConfiguration) throws IOException, ClassNotFoundException {
		FileInputStream fin = null;
		ObjectInputStream in = null;
		try {
			fin = new FileInputStream(cachedConfiguration);
			in = new ObjectInputStream(fin);
			return (Configuration) in.readObject();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			configureSessionFactory();
		}
		return sessionFactory;
	}

	public void initializeHibernate() {
		getSessionFactory();
	}

	public synchronized void closeSessionFactory() {

		LOGGER.info("Closing session factory");

		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;

			StandardServiceRegistryBuilder.destroy(serviceRegistry);

		} else {
			LOGGER.fatal("Cannot close null session factory");
		}
	}

	public Session openSession() {
		Session session = getSessionFactory().openSession();
		LOGGER.debug("Open session");
		return session;
	}

	public void closeSession(Session session) {
		session.close();
		LOGGER.debug("Close session");
	}

	@SuppressWarnings({ "rawtypes" })
	public void evict(Class entityClass, Serializable identifier) {
		if (identifier == null) {
			getSessionFactory().getCache().evictEntityRegion(entityClass);
		} else {
			getSessionFactory().getCache().evictEntity(entityClass, identifier);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public void evict(Class entityClass) {
		getSessionFactory().getCache().evictEntityRegion(entityClass);
	}

	/**
	 * Evicts all specified collection region or all regions if region is not specified(region=null)
	 * 
	 * @param region
	 */
	public void evictCollectionRegion(String region) {
		if (region == null) {
			getSessionFactory().getCache().evictCollectionRegions();
		} else {
			getSessionFactory().getCache().evictCollectionRegion(region);
		}
	}

	/**
	 * Evicts all specified query region or all regions if region is not specified(region=null)
	 * 
	 * @param region
	 */
	public void evictQueryRegion(String region) {
		if (region == null) {
			getSessionFactory().getCache().evictQueryRegions();
		} else {
			getSessionFactory().getCache().evictQueryRegion(region);
		}
	}

	public String toSql(Session session, Criteria criteria) {
		try {
			CriteriaImpl c = (CriteriaImpl) criteria;
			SessionImpl s = (SessionImpl) c.getSession();
			SessionFactoryImplementor factory = s.getSessionFactory();
			String[] implementors = factory.getImplementors(c.getEntityOrClassName());
			CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), factory, c, implementors[0], s.getLoadQueryInfluencers());

			Field f = OuterJoinLoader.class.getDeclaredField("sql");
			f.setAccessible(true);
			return (String) f.get(loader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void printStatistics() {
		LOGGER.info("Session close count = %d", getSessionFactory().getStatistics().getSessionCloseCount());
		LOGGER.info("Session open count = %d", getSessionFactory().getStatistics().getSessionOpenCount());

		LOGGER.info("Object fetch count = %d ", getSessionFactory().getStatistics().getEntityFetchCount());
		LOGGER.info("Object insert count = %d ", getSessionFactory().getStatistics().getEntityInsertCount());
		LOGGER.info("Object load count = %d ", getSessionFactory().getStatistics().getEntityLoadCount());
		LOGGER.info("Object delete count = %d ", getSessionFactory().getStatistics().getEntityDeleteCount());

		LOGGER.info("Query cache hit count = %d ", getSessionFactory().getStatistics().getQueryCacheHitCount());
		LOGGER.info("Query cache miss count = %d ", getSessionFactory().getStatistics().getQueryCacheMissCount());
		LOGGER.info("Query cache putByDayStatistic count = %d ", getSessionFactory().getStatistics().getQueryCachePutCount());

		LOGGER.info("Second level hit count = %d ", getSessionFactory().getStatistics().getSecondLevelCacheHitCount());
		LOGGER.info("Second level miss count = %d ", getSessionFactory().getStatistics().getSecondLevelCacheMissCount());
		LOGGER.info("Second level putByDayStatistic count = %d ", getSessionFactory().getStatistics().getSecondLevelCachePutCount());

		for (String region : getSessionFactory().getStatistics().getSecondLevelCacheRegionNames()) {
			LOGGER.info("Second level cache region is '%s'", region);
		}
	}

	public void printStatistics(String entityName) {
		EntityStatistics entityStats = getSessionFactory().getStatistics().getEntityStatistics(entityName);
		// exactly the same as the global values, but for a single entity class.
		LOGGER.info("Entity %s fetch count = %d ", entityName, entityStats.getFetchCount());
		LOGGER.info("Entity %s load count = %d ", entityName, entityStats.getLoadCount());
		LOGGER.info("Entity %s insert count = %d ", entityName, entityStats.getInsertCount());
		LOGGER.info("Entity %s update count = %d ", entityName, entityStats.getUpdateCount());
		LOGGER.info("Entity %s delete count = %d ", entityName, entityStats.getDeleteCount());
	}

	public void printCacheStatistics(String cacheName) {
		SecondLevelCacheStatistics cacheStats = getSessionFactory().getStatistics().getSecondLevelCacheStatistics(cacheName);
		LOGGER.info("Cache %s elementCountInMemory = %d", cacheName, cacheStats.getElementCountInMemory());
		LOGGER.info("Cache %s elementCountOnDisk = %d", cacheName, cacheStats.getElementCountOnDisk());
		LOGGER.info("Cache %s entries = %d ", cacheName, cacheStats.getEntries().size());
		LOGGER.info("Cache %s hitCount = %d ", cacheName, cacheStats.getHitCount());
		LOGGER.info("Cache %s missCount = %d ", cacheName, cacheStats.getMissCount());
		LOGGER.info("Cache %s putCount = %d ", cacheName, cacheStats.getPutCount());
		LOGGER.info("Cache %s sizeInMemory = %d Mb", cacheName, cacheStats.getSizeInMemory() / (1024 * 1024));
	}

	public Statistics getStatistics() {
		return getSessionFactory().getStatistics();
	}

	public boolean checkDatabase() {
		Session session = getSessionFactory().openSession();
		// Workaround to check if connection setup.
		session.beginTransaction().rollback();
		return true;
	}
}
