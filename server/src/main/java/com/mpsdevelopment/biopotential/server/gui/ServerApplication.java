package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.plasticine.commons.ClasspathResourceManager;
import com.mpsdevelopment.plasticine.commons.LogbackConfigureLoader;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class ServerApplication {

	private static final Logger LOGGER = LoggerUtil.getLogger(ServerApplication.class);

	private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();

	public static void main(String args[]) throws Exception {
		
		LogbackConfigureLoader.initializeLogging(resourceManager, "logback.xml", "jul.properties");
		
		JettyServer server = JettyServer.getInstance();
		try {
			server.start();
			LOGGER.info("Server started");
			server.join();

			LOGGER.info("Server stopped");
		} catch (Exception e) {
			LOGGER.error("Failed to start server. = %s", e);
		}
	}

}
