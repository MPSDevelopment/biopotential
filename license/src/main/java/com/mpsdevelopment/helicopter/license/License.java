package com.mpsdevelopment.helicopter.license;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class License {

	@Expose
	private String id;

	@Expose
	private Date expirationDate;

	@Expose
	private Date lastStartDate;

	public String getId() {
		return id;
	}

	public License setId(String id) {
		this.id = id;
		return this;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public License setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
		return this;
	}

	public Date getLastStartDate() {
		return lastStartDate;
	}

	public License setLastStartDate(Date lastStartDate) {
		this.lastStartDate = lastStartDate;
		return this;
	}

	@Override
	public String toString() {
		return JsonUtils.getJson(this);
	}
}
