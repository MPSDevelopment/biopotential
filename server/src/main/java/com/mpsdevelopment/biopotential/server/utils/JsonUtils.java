package com.mpsdevelopment.biopotential.server.utils;

import com.google.gson.*;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class JsonUtils {

	private static final String DATETIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.SSS (z)";

	/**
	 * Gson without type hierarchy adapters
	 */
	private static Gson gson;

	private static Logger LOGGER = LoggerUtil.getLogger(JsonUtils.class);

	static {
		GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().setDateFormat(DATETIME_FORMAT_STRING)
				.setLongSerializationPolicy(LongSerializationPolicy.STRING).excludeFieldsWithoutExposeAnnotation()
				.disableHtmlEscaping().enableComplexMapKeySerialization();
		gson = gsonBuilder.create();
	}

	public static String getJson(Object object) {
		return gson.toJson(object);
	}

	public static <T extends Object> T fromJson(Class<T> clazz, String json, boolean loadFromDatabase) {
		return (T) gson.fromJson(json, clazz);
	}

	public static <T extends Object> T fromJson(Class<T> clazz, String json) {
		return fromJson(clazz, json, false);
	}

	public static <T extends Object> T fromJson(Type type, String json) {
		return (T) gson.fromJson(json, type);
	}

	public static Object getJsonObjectFromFile(Class clazz, String filename) {
		Object object = null;
		try {
			String text = FileUtils.readFileToString(new File(filename)); // getContentsAsString(new
																			// File(filename));
			object = gson.fromJson(text, clazz);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			LOGGER.printStackTrace(e);
		}
		return object;
	}

	public static void writeJsonToFile(String json, String fileName) {
		try {
			FileUtils.writeStringToFile(new File(fileName), json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> List<T> getListFromJson(Class<T[]> clazz, String json) {
		return Arrays.asList(gson.fromJson(json, clazz));
	}

	public static ResponseEntity<String> getJsonResponse(String message, boolean isError, HttpStatus status) {
		JsonObject result = new JsonObject();
		result.addProperty("message", message);
		result.addProperty("error", isError);
		return new ResponseEntity<>(result.toString(), status);
	}

}
