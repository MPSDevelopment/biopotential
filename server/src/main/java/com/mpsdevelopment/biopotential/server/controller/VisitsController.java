package com.mpsdevelopment.biopotential.server.controller;

import com.mpsdevelopment.biopotential.server.db.advice.Adviceable;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.dao.VisitDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.db.pojo.Visit;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.SecurityUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping(ControllerAPI.VISITS_CONTROLLER)
@Controller
public class VisitsController {

	private static final Logger LOGGER = LoggerUtil.getLogger(VisitsController.class);
    private static final String UNDEFINED_CLIENT_DATA = "undefined";

	@Autowired
	private VisitDao visitDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SecurityUtils securityUtils;

	public VisitsController() {
	}

	@Adviceable
	@RequestMapping(value = ControllerAPI.VISITS_CONTROLLER_PUT_CREATE_VISIT, method = RequestMethod.PUT, produces = { ControllerAPI.PRODUCES_JSON })
	public ResponseEntity<String> createUser(HttpServletRequest request, @RequestBody String json) {

		Visit visit = JsonUtils.fromJson(Visit.class, json);

		visitDao.save(visit);
		List<User> users = userDao.getUsers(null, null);
		LOGGER.info("Has been loaded '%s' users", users.size());

		return new ResponseEntity<String>(JsonUtils.getJson(visit), null, HttpStatus.CREATED);

	}

    @RequestMapping(value = ControllerAPI.VISITS_CONTROLLER_GET_ALL, method = RequestMethod.GET, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> getAllUsers() throws ParseException {
        List<Visit> visits = visitDao.getVisits(null, null);
        LOGGER.info("Has been loaded '%s' users", visits.size());
        return new ResponseEntity<>(JsonUtils.getJson(visits),
                null, HttpStatus.OK);
    }

}
