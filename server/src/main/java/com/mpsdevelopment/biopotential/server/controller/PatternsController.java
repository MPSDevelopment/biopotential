package com.mpsdevelopment.biopotential.server.controller;

import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping(ControllerAPI.PATTERNS_CONTROLLER)
@Controller
public class PatternsController {

	private static final Logger LOGGER = LoggerUtil.getLogger(PatternsController.class);
    private static final String UNDEFINED_CLIENT_DATA = "undefined";

	@Autowired
	private PatternsDao patternsDao;


	public PatternsController() {
	}

	/*@Adviceable
	@RequestMapping(value = ControllerAPI.VISITS_CONTROLLER_PUT_CREATE_VISIT, method = RequestMethod.PUT, produces = { ControllerAPI.PRODUCES_JSON })
	public ResponseEntity<String> createUser(HttpServletRequest request, @RequestBody String json) {

		Visit visit = JsonUtils.fromJson(Visit.class, json);

		visitDao.save(visit);
		List<User> users = userDao.getUsers(null, null);
		LOGGER.info("Has been loaded '%s' users", users.size());

		return new ResponseEntity<String>(JsonUtils.getJson(visit), null, HttpStatus.CREATED);

	}*/

    @RequestMapping(value = ControllerAPI.PATTERNS_CONTROLLER_GET_ALL, method = RequestMethod.GET, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> getAllUsers() throws ParseException {

		List<Pattern> patterns = patternsDao.getPatterns(null, null);

        LOGGER.info("Has been loaded '%s' users", patterns.size());
        return new ResponseEntity<>(JsonUtils.getJson(patterns), null, HttpStatus.OK);
    }

}
