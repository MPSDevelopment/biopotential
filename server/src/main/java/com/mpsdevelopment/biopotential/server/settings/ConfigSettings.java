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
    @Expose
    private String systemName3;
    @Expose
    private String literal3;
    @Expose
    private String threshold3;
    @Expose
    private String systemName4;
    @Expose
    private String literal4;
    @Expose
    private String threshold4;
    @Expose
    private String systemName5;
    @Expose
    private String literal5;
    @Expose
    private String threshold5;
    @Expose
    private String systemName6;
    @Expose
    private String literal6;
    @Expose
    private String threshold6;
    @Expose
    private String systemName7;
    @Expose
    private String literal7;
    @Expose
    private String threshold7;
    @Expose
    private String systemName8;
    @Expose
    private String literal8;
    @Expose
    private String threshold8;
    @Expose
    private String systemName9;
    @Expose
    private String literal9;
    @Expose
    private String threshold9;
    @Expose
    private String systemName10;
    @Expose
    private String literal10;
    @Expose
    private String threshold10;
    @Expose
    private String systemName11;
    @Expose
    private String literal11;
    @Expose
    private String threshold11;
    @Expose
    private String systemName12;
    @Expose
    private String literal12;
    @Expose
    private String threshold12;

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

    public String getSystemName3() {
        return systemName3;
    }

    public void setSystemName3(String systemName3) {
        this.systemName3 = systemName3;
    }

    public String getLiteral3() {
        return literal3;
    }

    public void setLiteral3(String literal3) {
        this.literal3 = literal3;
    }

    public String getThreshold3() {
        return threshold3;
    }

    public void setThreshold3(String threshold3) {
        this.threshold3 = threshold3;
    }

    public String getSystemName4() {
        return systemName4;
    }

    public void setSystemName4(String systemName4) {
        this.systemName4 = systemName4;
    }

    public String getLiteral4() {
        return literal4;
    }

    public void setLiteral4(String literal4) {
        this.literal4 = literal4;
    }

    public String getThreshold4() {
        return threshold4;
    }

    public void setThreshold4(String threshold4) {
        this.threshold4 = threshold4;
    }

    public String getSystemName5() {
        return systemName5;
    }

    public void setSystemName5(String systemName5) {
        this.systemName5 = systemName5;
    }

    public String getLiteral5() {
        return literal5;
    }

    public void setLiteral5(String literal5) {
        this.literal5 = literal5;
    }

    public String getThreshold5() {
        return threshold5;
    }

    public void setThreshold5(String threshold5) {
        this.threshold5 = threshold5;
    }

    public String getSystemName6() {
        return systemName6;
    }

    public void setSystemName6(String systemName6) {
        this.systemName6 = systemName6;
    }

    public String getLiteral6() {
        return literal6;
    }

    public void setLiteral6(String literal6) {
        this.literal6 = literal6;
    }

    public String getThreshold6() {
        return threshold6;
    }

    public void setThreshold6(String threshold6) {
        this.threshold6 = threshold6;
    }

    public String getSystemName7() {
        return systemName7;
    }

    public void setSystemName7(String systemName7) {
        this.systemName7 = systemName7;
    }

    public String getLiteral7() {
        return literal7;
    }

    public void setLiteral7(String literal7) {
        this.literal7 = literal7;
    }

    public String getThreshold7() {
        return threshold7;
    }

    public void setThreshold7(String threshold7) {
        this.threshold7 = threshold7;
    }

    public String getSystemName8() {
        return systemName8;
    }

    public void setSystemName8(String systemName8) {
        this.systemName8 = systemName8;
    }

    public String getLiteral8() {
        return literal8;
    }

    public void setLiteral8(String literal8) {
        this.literal8 = literal8;
    }

    public String getThreshold8() {
        return threshold8;
    }

    public void setThreshold8(String threshold8) {
        this.threshold8 = threshold8;
    }

    public String getSystemName9() {
        return systemName9;
    }

    public void setSystemName9(String systemName9) {
        this.systemName9 = systemName9;
    }

    public String getLiteral9() {
        return literal9;
    }

    public void setLiteral9(String literal9) {
        this.literal9 = literal9;
    }

    public String getThreshold9() {
        return threshold9;
    }

    public void setThreshold9(String threshold9) {
        this.threshold9 = threshold9;
    }

    public String getSystemName10() {
        return systemName10;
    }

    public void setSystemName10(String systemName10) {
        this.systemName10 = systemName10;
    }

    public String getLiteral10() {
        return literal10;
    }

    public void setLiteral10(String literal10) {
        this.literal10 = literal10;
    }

    public String getThreshold10() {
        return threshold10;
    }

    public void setThreshold10(String threshold10) {
        this.threshold10 = threshold10;
    }

    public String getSystemName11() {
        return systemName11;
    }

    public void setSystemName11(String systemName11) {
        this.systemName11 = systemName11;
    }

    public String getLiteral11() {
        return literal11;
    }

    public void setLiteral11(String literal11) {
        this.literal11 = literal11;
    }

    public String getThreshold11() {
        return threshold11;
    }

    public void setThreshold11(String threshold11) {
        this.threshold11 = threshold11;
    }

    public String getSystemName12() {
        return systemName12;
    }

    public void setSystemName12(String systemName12) {
        this.systemName12 = systemName12;
    }

    public String getLiteral12() {
        return literal12;
    }

    public void setLiteral12(String literal12) {
        this.literal12 = literal12;
    }

    public String getThreshold12() {
        return threshold12;
    }

    public void setThreshold12(String threshold12) {
        this.threshold12 = threshold12;
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