package com.mpsdevelopment.biopotential.server;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SpringLoaderFXML {

	private static Logger LOGGER = LoggerUtil.getLogger(SpringLoaderFXML.class);
//	public static final ApplicationContext APPLICATION_CONTEXT = InstructorApplication.APP_CONTEXT;

//	private static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("webapp/app-context.xml");
	private FXMLLoader loader;

	public <T> T load(URL url) {
		try  {
			loader = new FXMLLoader(url);
//			loader.setControllerFactory(applicationContext::getBean);
			return loader.load();
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}
}
