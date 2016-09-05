package com.mpsdevelopment.biopotential.server;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;

public class SpringLoaderFXML {

	private static Logger LOGGER = LoggerUtil.getLogger(SpringLoaderFXML.class);
//	public static final ApplicationContext APPLICATION_CONTEXT = InstructorApplication.APP_CONTEXT;

	/**
	 * @param className
	 *            - controller class, needed for loading resources.
	 * @param url
	 *            - *.fxml file name.
	 * @return controller instance (Spring-bean).
	 */
	public static Controller load(ApplicationContext context, Class className, String url) {
		LOGGER.debug("Loading resource '%s' by class: '%s'", url, className.getSimpleName());
		try (InputStream fxmlStream = className.getResourceAsStream(url)) {

			LOGGER.debug("InputStream is %s", fxmlStream);

			LOGGER.debug("Before creation FXMLLoader");

			FXMLLoader loader = new FXMLLoader();

			LOGGER.debug("FXMLLoader  creation is ok");

			Callback<Class<?>, Object> controllerFactory = context::getBean;
			LOGGER.debug(" controllerFactory is created");

			loader.setControllerFactory(controllerFactory);

			LOGGER.debug(" loader set controllerFactory ");

			if (fxmlStream != null) {
				Pane view = loader.load(fxmlStream);
				LOGGER.debug("InputStream  '%s'", fxmlStream);
				Controller controller = loader.getController();
				controller.setView(view);
				return controller;
			} else {
				LOGGER.error("Cannot load resource '%s' as stream", url);
			}
		} catch (IOException e) {
			LOGGER.error("Can't load resource %s", e);
			LOGGER.printStackTrace(e);
			throw new RuntimeException(e);
		}
		return null;
	}
}
