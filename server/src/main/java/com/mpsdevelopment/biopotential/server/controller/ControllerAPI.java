package com.mpsdevelopment.biopotential.server.controller;

public class ControllerAPI {
	
	public static final String PRODUCES_JSON = "application/json; charset=UTF-8";
    public static final String USER_CONTROLLER_GET_ALL_USERS_BY_PAGE = "/all/{pageSize}/from/{pageNumber}";
    public static final String USER_CONTROLLER_GET_ALL = "/all";

    public static final String USER_CONTROLLER = "/api/users";
	public static final String USER_CONTROLLER_LOGIN = "/login";
	public static final String USER_CONTROLLER_LOGOUT = "/logout";
	public static final String USER_CONTROLLER_PUT_CREATE_USER = "/create";
	public static final String USER_CONTROLLER_DELETE_USER = "/remove/{id}";
	public static final String USER_CONTROLLER_POST_UPDATE_USER = "/update";

	public static final String VISITS_CONTROLLER = "/api/visits";
	public static final String VISITS_CONTROLLER_PUT_CREATE_VISIT = "/create";
	public static final String VISITS_CONTROLLER_GET_ALL = "/all";
}
