package com.mpsdevelopment.biopotential.server;

import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.*;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import java.io.*;
import java.net.URL;
import java.util.EnumSet;

public class JettyServer {

	private static final Logger LOGGER = LoggerUtil.getLogger(JettyServer.class);

	public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/settings-context.xml");

	public static XmlWebApplicationContext WEB_CONTEXT;

	public static final String SPRING_ROOT = "webapp"; // that folder contains
														// Spring context
	public static final String MVC_SERVLET_NAME = "rest";
	public static final String SPRING_CONTEXT_FILENAME = "web-context.xml";

	private Server server;
	private static JettyServer jettyServer;
	private ServletContextHandler contextHandler;

	@Autowired
	private ServerSettings serverSettings;

	private JettyServer() {
		serverSettings = APP_CONTEXT.getBean(ServerSettings.class);
	}

	public static JettyServer getInstance() {
		if (jettyServer == null) {
			jettyServer = new JettyServer();
		}
		return jettyServer;
	}

	public void start() throws ServletException {

//		DatabaseCreator databaseCreator = BioApplication.APP_CONTEXT.getBean(DatabaseCreator.class);
		/*PersistUtils persistUtils = WEB_CONTEXT.getBean(PersistUtils.class);
		SessionManager sessionManager = WEB_CONTEXT.getBean(SessionManager.class);

		persistUtils.setConfigurationDatabaseFilename(serverSettings.getDbPath());
		SessionFactory sessionFactory = persistUtils.configureSessionFactory();
		Session session = sessionFactory.openSession();
		sessionManager.setSession(session);*/

		server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(serverSettings.getPort());
		connector.setHost(serverSettings.getHost());
		server.addConnector(connector);
		ResourceHandler webResourceHandler = new ResourceHandler();
		webResourceHandler.setDirectoriesListed(true);
		webResourceHandler.setWelcomeFiles(new String[] { "index.html" });
		webResourceHandler.setResourceBase("web");

		// disable web content locking
		webResourceHandler.setMinMemoryMappedContentLength(-1);

		// webResourceHandler.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
		// "false");

		ResourceHandler filesResourceHandler = new ResourceHandler();
		filesResourceHandler.setDirectoriesListed(true);
		filesResourceHandler.setResourceBase(serverSettings.getFilesPath());

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { webResourceHandler, filesResourceHandler, getServletHandler() });
		server.setHandler(handlers);

		try {
			server.start();
			LOGGER.info("Server started at host %s and port %s", serverSettings.getHost(), serverSettings.getPort());
		} catch (Exception e) {
			LOGGER.printStackTrace(e);
		}
	}

	public ServletContextHandler getServletHandler() {
		if (contextHandler == null) {
			File tempDirectory = new File(serverSettings.getTempDirectory());
			if (!tempDirectory.exists()) {
				tempDirectory.mkdirs();
			}

			contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
			contextHandler.setAttribute("javax.servlet.context.tempdir", tempDirectory);
			contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());

			WEB_CONTEXT = new XmlWebApplicationContext();
			WEB_CONTEXT.setConfigLocations(SPRING_CONTEXT_FILENAME);
			WEB_CONTEXT.setParent(BioApplication.APP_CONTEXT);
//			WEB_CONTEXT.setParent(new ClassPathXmlApplicationContext("webapp/app-context.xml"));

			ServletHolder mvcServletHolder = new ServletHolder(MVC_SERVLET_NAME, new DispatcherServlet(WEB_CONTEXT));
			mvcServletHolder.setInitParameter("useFileMappedBuffer", "false");
			contextHandler.addServlet(mvcServletHolder, "/");
			
			contextHandler.setResourceBase(getBaseUrl());

			int contentSize = contextHandler.getMaxFormContentSize();
			int maxContentSize = 500 * 1000 * 1000;
			contextHandler.setMaxFormContentSize(maxContentSize);

			LOGGER.info("Max content size will be changed from %s to %s", contentSize, maxContentSize);

		}
		return contextHandler;
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	private String getBaseUrl() {
		URL webInfUrl = Thread.currentThread().getContextClassLoader().getResource(SPRING_ROOT);
		if (webInfUrl == null) {
			throw new RuntimeException("Failed to find web application root: " + SPRING_ROOT);
		} else {
			LOGGER.info("Jetty spring base url is %s", webInfUrl);
		}
		return webInfUrl.toExternalForm();
	}

}
