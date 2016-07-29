package com.mpsdevelopment.biopotential.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.mpsdevelopment.biopotential.server.controller.UsersController;
import com.mpsdevelopment.biopotential.server.db.PersistUtils;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.db.advice.Adviceable;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class UsersService {
	
	private static final Logger LOGGER = LoggerUtil.getLogger(UsersService.class);

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SessionManager sessionManager;
	
	@Adviceable
    public void checkScripts() {
        List<User> users = userDao.findAll();
        users.forEach(user -> LOGGER.info("User is %s", user));
        sessionManager.printStatistics();
    }
}
