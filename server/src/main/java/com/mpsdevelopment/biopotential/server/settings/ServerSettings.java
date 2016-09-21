package com.mpsdevelopment.biopotential.server.settings;

import com.google.gson.annotations.Expose;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ServerSettings {

	private static final Logger LOGGER = LoggerUtil.getLogger(ServerSettings.class);

	private String path;

	@Expose
	private String host = "localhost";
	@Expose
	private String cookieHost = "";

	@Expose
	public int cookieMaxAge = 24 * 60 * 60;

	@Expose
	private Integer port;
	@Expose
	private String tempDirectory = "data/tmp";
	@Expose
	private String filesPath;

	private void init() {
		ServerSettings fileSettings = (ServerSettings) JsonUtils.getJsonObjectFromFile(ServerSettings.class, path);
		updateSettings(fileSettings);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCookieHost() {
		return cookieHost;
	}

	public void setCookieHost(String cookieHost) {
		this.cookieHost = cookieHost;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	public String getFilesPath() {
		return filesPath;
	}

	public void setFilesPath(String filesPath) {
		this.filesPath = filesPath;
	}

	public int getCookieMaxAge() {
		return cookieMaxAge;
	}

	public void setCookieMaxAge(int cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	public void updateSettings(ServerSettings newSettings) {

		BeanUtils.copyProperties(newSettings, this);
	}

}