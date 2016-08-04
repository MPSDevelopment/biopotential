package com.mpsdevelopment.biopotential.server.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mpsdevelopment.biopotential.server.service.UsersService;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

@Component
public class UsersScheduler {
	
	private static final Logger LOGGER = LoggerUtil.getLogger(UsersScheduler.class);
	
	@Autowired
	private UsersService usersService;

//    @Scheduled(cron = "*/1 * * * * *")
    public void checkScripts() {
    	LOGGER.info("Users service started");
    	usersService.checkScripts();
    }
}
