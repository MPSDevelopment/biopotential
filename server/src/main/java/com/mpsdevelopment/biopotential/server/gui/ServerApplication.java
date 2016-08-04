package com.mpsdevelopment.biopotential.server.gui;

import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class ServerApplication {

	private static final Logger LOGGER = LoggerUtil.getLogger(ServerApplication.class);

	public static void main(String args[]) throws Exception {
		try {

			JettyServer server = JettyServer.getInstance();
			server.start();
			LOGGER.info("Server started");
			server.join();

//			Thread.sleep(100000);

			LOGGER.info("Server stopped");
		} catch (Exception e) {
			LOGGER.error("Failed to start server. = %s", e);
		}

	}

}
