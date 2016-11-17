package com.mpsdevelopment.biopotential.server.db;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class SessionManager {

	private static final Logger LOGGER = LoggerUtil.getLogger(SessionManager.class);

	// session management block
	private static int openedSessions = 0;
	private static int closedSessions = 0;
	private static ThreadLocal<Session> threadLocalSession = new ThreadLocal<Session>();

	@Autowired
	private PersistUtils persistUtils;

	public SessionManager() {
		LOGGER.info("Create SessionManager");
	}

	public SessionManager(PersistUtils persistUtils) {
		this.persistUtils = persistUtils;
	}

	public void setSession(Session session) {
		threadLocalSession.set(session);
		openedSessions++;
	}

	public Session getSession() {
		if (threadLocalSession.get() == null) {
			Session session = persistUtils.openSession();
			setSession(session);
		} else {
		}
		return threadLocalSession.get();
	}

	public void openSession() {
		Session session = persistUtils.openSession();
		LOGGER.debug("Open session");
		setSession(session);
	}

	public void closeSession() {
		closeSession(false);
	}

	public void closeSession(boolean force) {
		if (threadLocalSession.get() != null) {
			Session session = threadLocalSession.get();
			persistUtils.closeSession(session);
			LOGGER.debug("Close session");
			closedSessions++;
		}
		threadLocalSession.remove();
	}

	public static Session getThreadLocalSession() {
		return threadLocalSession.get();
	}

	public int getOpenedSessions() {
		return openedSessions;
	}

	public int getClosedSessions() {
		return closedSessions;
	}

	public void printStatistics() {
		LOGGER.info("Session manager opened sessions count = %d", openedSessions);
		LOGGER.info("Session manager closed sessions count = %d", closedSessions);
	}
}
