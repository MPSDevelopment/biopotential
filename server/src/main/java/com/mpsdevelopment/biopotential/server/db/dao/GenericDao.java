package com.mpsdevelopment.biopotential.server.db.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import javax.annotation.Resource;

public abstract class GenericDao<T, ID extends Serializable> {

	private static final Logger LOGGER = LoggerUtil.getLogger(GenericDao.class);

	public static int executionTime;

	private Class<T> persistentClass;

	@Resource
	protected SessionManager sessionManager;

	protected void setSession(Session session) {
		sessionManager.setSession(session);
	}

	protected Session getSession() {
//		LOGGER.info("sessionManager %s", sessionManager);
		return sessionManager.getSession();
	}

	private void commitTransaction(Session session) {

		if (session.getTransaction().getStatus() != TransactionStatus.COMMITTED
				&& session.getTransaction().getStatus() != TransactionStatus.COMMITTING) {
			session.getTransaction().commit();
		}
	}

	private void beginTransaction(Session session) {

		if (session.getTransaction() != null && session.getTransaction().getStatus() == TransactionStatus.ACTIVE) {
			// do not open nested transaction
		} else {
			session.beginTransaction();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GenericDao() {
		Class clazz = getClass();
		boolean converted = false;
		ParameterizedType pt = null;

		while (!converted) {
			try {
				pt = ((ParameterizedType) clazz.getGenericSuperclass());
				converted = true;
			} catch (ClassCastException e) {
				clazz = clazz.getSuperclass();
			}
		}
		this.persistentClass = (Class<T>) pt.getActualTypeArguments()[0];
	}

	public <T> T deproxy(T maybeProxy) {
		if (maybeProxy instanceof HibernateProxy) {
			return (T) ((HibernateProxy) maybeProxy).getHibernateLazyInitializer().getImplementation();
		}
		return maybeProxy;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	/**
	 * Evicts object from session with cascaded eviction of children.
	 * 
	 * @param object
	 */
	public void evictFromSession(T object) {
		getSession().evict(object);
	}

	public T load(ID id) {
		T entity;
		// Session session = PersistUtils.openSession();
		Session session = getSession();
		beginTransaction(session);
		try {
			entity = load(session, id);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	/**
	 * Loads object by id without transactions and session handling
	 * 
	 * @param session
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T load(Session session, ID id) {
		return (T) session.get(getPersistentClass(), id);
	}

	public T get(ID id) {
		T entity;
		// Session session = PersistUtils.openSession();
		Session session = getSession();
		beginTransaction(session);
		try {
			entity = get(session, id);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	/**
	 * Gets object by id without transactions and session handling
	 * 
	 * @param session
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T get(Session session, ID id) {
		return (T) session.get(getPersistentClass(), id);
	}

	public T saveOrUpdate(T entity) {
		// Session session = PersistUtils.openSession();
		Session session = getSession();
		beginTransaction(session);
		try {
			saveOrUpdate(getSession(), entity);
			commitTransaction(session);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			;
		}
		return entity;
	}

	/**
	 * Saves entity without transactions and session handling
	 * 
	 * @param session
	 * @param entity
	 * @return
	 */
	protected T saveOrUpdate(Session session, T entity) {
		session.saveOrUpdate(entity);
		return entity;
	}

	public T save(T entity) {
		Session session = getSession();
		beginTransaction(session);
		try {
			save(getSession(), entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	public T persist(T entity) {
		Session session = getSession();
		beginTransaction(session);
		try {
			persist(getSession(), entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	protected T persist(Session session, T entity) {
		session.persist(entity);
		return entity;
	}

	public T refresh(T entity) {
		// Session session = PersistUtils.openSession();
		Session session = getSession();
		beginTransaction(session);
		try {
			refresh(session, entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	protected T refresh(Session session, T entity) {
		session.refresh(entity);
		return entity;
	}

	/**
	 * Saves entity without trying to update, transactions and session handling
	 * 
	 * @param session
	 * @param entity
	 * @return
	 */
	protected T save(Session session, T entity) {
		session.save(entity);
		return entity;
	}

	public T update(T entity) {
		// Session session = PersistUtils.openSession();
		Session session = getSession();
		beginTransaction(session);
		try {
			update(getSession(), entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	/**
	 * Saves entity without trying to update, transactions and session handling
	 * 
	 * @param session
	 * @param entity
	 * @return
	 */
	protected T update(Session session, T entity) {
		session.update(entity);
		return entity;
	}

	public T merge(T entity) {
		// Session session = PersistUtils.openSession();
		Session session = getSession();
		beginTransaction(session);
		try {
			merge(session, entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	protected T merge(Session session, T entity) {
		session.merge(entity);
		return entity;
	}

	public T lock(T entity) {
		Session session = getSession();
		// beginTransaction(session);
		try {
			lock(session, entity);
			// commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	protected T lock(Session session, T entity) {
		session.buildLockRequest(LockOptions.NONE).lock(entity);
		return entity;
	}

	public T delete(T entity) {
		// Session session = PersistUtils.openSession();
		Session session = getSession();
		beginTransaction(session);
		try {
			delete(session, entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	protected T delete(Session session, T entity) {
		session.delete(entity);
		return entity;
	}

	public Long getCount() {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		} finally {
			;
		}
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	public ScrollableResults findAllScroll() {
		// Session session = PersistUtils.openSession();
		try {
			getSession().setCacheMode(CacheMode.IGNORE);
			getSession().setFlushMode(FlushMode.MANUAL);
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			return criteria.setReadOnly(true).setCacheable(false).scroll(ScrollMode.FORWARD_ONLY);
		} finally {
			;
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance, String[] excludeProperty) {
		// Session session = PersistUtils.openSession();
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			Example example = Example.create(exampleInstance);
			for (String exclude : excludeProperty) {
				example.excludeProperty(exclude);
			}
			criteria.add(example);
			return criteria.list();
		} finally {
			;
		}
	}

	/**
	 * Use this inside subclasses as a convenience method.
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(Criterion... criterion) {
		// Session session = PersistUtils.openSession();
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.setCacheable(false);
			for (Criterion c : criterion) {
				criteria.add(c);
			}
			return criteria.list();
		} finally {
			;
		}
	}

	/**
	 * @param identifier
	 * @return
	 * @throws DaoException
	 */
	public <A> T loadByField(String fieldName, A value) throws DaoException {
		Map<String, A> fields = new HashMap<String, A>();
		fields.put(fieldName, value);
		return loadByFields(fields);
	}

	/**
	 * Load value by fields (only Restrictions.eq()).
	 * 
	 * @param <A>
	 * @param fields
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <A> T loadByFields(Map fields) throws DaoException {
		Set<String> keys = fields.keySet();
		Map<String, List<Object>> fieldsEqOrLike = new HashMap<String, List<Object>>();
		for (String key : keys) {
			List<Object> values = new ArrayList<Object>();
			values.add(fields.get(key));
			values.add(false);
			fieldsEqOrLike.put(key, values);
		}
		return loadByFieldsEqOrLike(fieldsEqOrLike);
	}

	/**
	 * Load value by fields.
	 * <p/>
	 * example: see class DocCommentDaoTest.java method
	 * testLoadByFieldsEqOrLike()
	 * 
	 * @param <A>
	 * @param fields
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <A> T loadByFieldsEqOrLike(Map<String, List<Object>> fields) throws DaoException {
		// Session session = PersistUtils.openSession();
		Set<String> keys = fields.keySet();
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.setCacheable(true);
			for (String key : keys) {
				List<Object> values = fields.get(key);
				if (Boolean.TRUE.equals(values.get(1))) {
					criteria.add(Restrictions.like(key, values.get(0)));
				} else {
					criteria.add(Restrictions.eq(key, values.get(0)));
				}
			}
			List list = criteria.list();
			if (list != null) {
				return (T) list.get(0);
			}
			// } catch (Exception e) {
		} finally {
			;
		}
		String errString = "ERROR " + new Date() + " Object %s by fields %s with values %s not found";

		String errFields = "";
		String errValues = "";
		boolean first = true;

		for (String key : keys) {
			errFields.concat(key);
			errValues.concat("\"" + fields.get(key).toString() + "\"");
			if (!first) {
				errFields.concat(", ");
				errValues.concat(", ");
			} else {
				first = false;
			}
		}
		throw new NotFoundDaoException(String.format(errString, getPersistentClass().getName(), errFields, errValues));
	}
}