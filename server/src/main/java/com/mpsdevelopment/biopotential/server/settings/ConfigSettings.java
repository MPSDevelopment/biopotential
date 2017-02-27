package com.mpsdevelopment.biopotential.server.settings;

import com.google.gson.annotations.Expose;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.beans.BeanUtils;

public class ConfigSettings {

	private static final Logger LOGGER = LoggerUtil.getLogger(ConfigSettings.class);
    private String path;
    @Expose
	private String systemName1;
    @Expose
    private String literal1;
    @Expose
    private String threshold1;
    @Expose
    private String systemName2;
    @Expose
    private String literal2;
    @Expose
    private String threshold2;

	private void init() {
		ConfigSettings fileSettings = (ConfigSettings) JsonUtils.getJsonObjectFromFile(ConfigSettings.class, path);
		updateSettings(fileSettings);
	}

    public String getSystemName1() {
        return systemName1;
    }

    public void setSystemName1(String systemName1) {
        this.systemName1 = systemName1;
    }

    public String getLiteral1() {
        return literal1;
    }

    public void setLiteral1(String literal1) {
        this.literal1 = literal1;
    }

    public String getThreshold1() {
        return threshold1;
    }

    public void setThreshold1(String threshold1) {
        this.threshold1 = threshold1;
    }

    public String getSystemName2() {
        return systemName2;
    }

    public void setSystemName2(String systemName2) {
        this.systemName2 = systemName2;
    }

    public String getLiteral2() {
        return literal2;
    }

    public void setLiteral2(String literal2) {
        this.literal2 = literal2;
    }

    public String getThreshold2() {
        return threshold2;
    }

    public void setThreshold2(String threshold2) {
        this.threshold2 = threshold2;
    }

    public void updateSettings(ConfigSettings newSettings) {

		BeanUtils.copyProperties(newSettings, this);
	}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}