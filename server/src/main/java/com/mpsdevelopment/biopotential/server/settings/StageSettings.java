package com.mpsdevelopment.biopotential.server.settings;

import com.google.gson.annotations.Expose;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.scene.paint.Color;
import org.springframework.beans.BeanUtils;

public class StageSettings {
	private static final Logger LOGGER = LoggerUtil.getLogger(StageSettings.class);

	@Expose
	private Double widthPanel = 1920d;
	@Expose
	private Double heightPanel = 1080d;
	@Expose
	private String panelTitle = null;
	@Expose
	private Double x = 0d;
	@Expose
	private Double y = 0d;
	@Expose
	private Double width = null;
	@Expose
	private Double height = null;
	@Expose
	private Class clazz = null;
	@Expose
	private String cssFileName = null;
	@Expose
	private boolean decorated = true;
	@Expose
	private boolean modal = true;
	@Expose
	private Color color = Color.BLACK;
	@Expose
	private boolean show = false;

	public void updateSettings(StageSettings newSettings) {
		BeanUtils.copyProperties(newSettings, this);
	}

	public Double getWidthPanel() {
		return widthPanel;
	}

	public StageSettings setWidthPanel(Double widthPanel) {
		this.widthPanel = widthPanel;
		return this;
	}

	public Double getHeightPanel() {
		return heightPanel;
	}

	public StageSettings setHeightPanel(Double heightPanel) {
		this.heightPanel = heightPanel;
		return this;
	}

	public String getPanelTitle() {
		return panelTitle;
	}

	public StageSettings setPanelTitle(String panelTitle) {
		this.panelTitle = panelTitle;
		return this;
	}

	public Double getX() {
		return x;
	}

	public StageSettings setX(Double x) {
		this.x = x;
		return this;
	}

	public Double getY() {
		return y;
	}

	public StageSettings setY(Double y) {
		this.y = y;
		return this;
	}

	public Double getWidth() {
		return width;
	}

	public StageSettings setWidth(Double width) {
		this.width = width;
		return this;
	}

	public Double getHeight() {
		return height;
	}

	public StageSettings setHeight(Double height) {
		this.height = height;
		return this;
	}

	public Class getClazz() {
		return clazz;
	}

	public StageSettings setClazz(Class clazz) {
		this.clazz = clazz;
		return this;
	}

	public String getCssFileName() {
		return cssFileName;
	}

	public StageSettings setCssFileName(String cssFileName) {
		this.cssFileName = cssFileName;
		return this;
	}

	public boolean isDecorated() {
		return decorated;
	}

	public StageSettings setDecorated(boolean decorated) {
		this.decorated = decorated;
		return this;
	}

	public boolean isModal() {
		return modal;
	}

	public StageSettings setModal(boolean modal) {
		this.modal = modal;
		return this;
	}

	public Color getColor() {
		return color;
	}

	public StageSettings setColor(Color color) {
		this.color = color;
		return this;
	}

	public boolean isShow() {
		return show;
	}

	public StageSettings setShow(boolean show) {
		this.show = show;
		return this;
	}

}
