package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;

public class Token {

	public enum TokenType {
		USER, LOST_PASSWORD
	};

	public enum Role {
		UNKNOWN, USER, OPERATOR, ADMIN
	}

	public static final String FIELD_ID = "uid";

	public static final String FIELD_TYPE = "type";
	
	public static final String FIELD_ROLE = "role";

	public static final String COOKIE_KEY_FIELD = "accessToken";

	public static final String COOKIE_DEFAULT_VALUE = "";

	@Expose
	private String uid;

	@Expose
	private TokenType type;

	@Expose
	private Role role;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
