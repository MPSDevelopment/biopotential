package com.mpsdevelopment.helicopter.license;

import com.google.gson.*;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JsonUtils {

	private static Logger LOGGER = LoggerUtil.getLogger(JsonUtils.class);

	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String DATE_FORMAT_VERSION = "yyyy-MM-dd'T'HH:mm:ssXXX";

	private static Gson gson = createGson();

	public static String getCompactJson(Object object) {
		String result = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).setDateFormat(DATE_FORMAT).disableHtmlEscaping().create().toJson(object);
		result = result.replace("\\", "");
		result = result.trim().replaceAll("  +", "");
		return result;
	}

	public static String getCompactLicenseJson(Object object) {
		String result = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).setDateFormat(DATE_FORMAT_VERSION).disableHtmlEscaping().create()
				.toJson(object);
		result = result.replace("\\", "");
		result = result.trim().replaceAll("  +", "");
		return result;
	}

	public static String getJson(Object object) {
		return gson.toJson(object);
	}

	public static <T extends Object> T fromJson(Class clazz, String json) {
		return (T) gson.fromJson(json, clazz);
	}

	public static <T extends Object> T fromJson(Class clazz, String json, String dateFormat) {
		return (T) new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).setDateFormat(dateFormat).disableHtmlEscaping().create().fromJson(json, clazz);
	}

	private static Gson createGson() {
		return new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation()
				.setDateFormat(DATE_FORMAT).setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();
	}

	public static Object getJsonObjectFromFile(Class clazz, String filename) {
		Gson gson = createGson();
		Object object = null;
		try {
			String text = FileUtils.readFileToString(new File(filename));
			object = gson.fromJson(text, clazz);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			LOGGER.printStackTrace(e);
		}
		return object;
	}

	public static <T> List<T> getListFromJson(Class<T[]> clazz, String json) {
		return Arrays.asList(gson.fromJson(json, clazz));
	}
}
