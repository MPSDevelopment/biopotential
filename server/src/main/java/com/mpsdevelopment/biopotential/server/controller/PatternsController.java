package com.mpsdevelopment.biopotential.server.controller;

import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(ControllerAPI.PATTERNS_CONTROLLER)
@Controller
public class PatternsController {

    private static final Logger LOGGER = LoggerUtil.getLogger(PatternsController.class);
    private static final String UNDEFINED_CLIENT_DATA = "undefined";

    @Autowired
    private PatternsDao patternsDao;

    @Autowired
    private FoldersDao foldersDao;

    private HttpHeaders requestHeaders;

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

    @RequestMapping(value = ControllerAPI.PATTERNS_CONTROLLER_GET_PATTERNS_SIZE, method = RequestMethod.GET, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> getAllPatternsByFolder() throws ParseException {
        requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-8");
        Map<String,Integer> map = new HashMap<>();

        List<Folder> folders = foldersDao.findAll();
        for (Folder fol : folders) {
//            List<EDXPattern> list = foldersDao.getAllPatternsByFolder(fol);
            int size = 0;
            List<EDXPattern> list = null;

            try {
                size = patternsDao.getPatternsFromFoldersToSystem(fol);
//                list = patternsDao.getPatternsFromFolders(fol);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fol.getFolderName().contains("Al ALLERGY")) {
                map.put("Al ALLERGY", size);
            }
            if (fol.getFolderName().contains("Ca CARDIO")) {
                map.put("Ca CARDIO", size);
            }
            if (fol.getFolderName().contains("De DERMA")) {
                map.put("De DERMA", size);
            }
            if (fol.getFolderName().contains("En ENDOKRIN")) {
                map.put("En ENDOKRIN", size);
            }
            if (fol.getFolderName().contains("Ga GASTRO")) {
                map.put("Ga GASTRO", size);
            }
            if (fol.getFolderName().contains("Im IMMUN")) {
                map.put("Im IMMUN", size);
            }
            if (fol.getFolderName().contains("Ne NEURAL")) {
                map.put("Ne NEURAL", size);
            }
            if (fol.getFolderName().contains("Or ORTHO")) {
                map.put("Or ORTHO", size);
            }
            if (fol.getFolderName().contains("Sp SPIRITUS")) {
                map.put("Sp SPIRITUS", size);
            }
            if (fol.getFolderName().contains("St STOMAT")) {
                map.put("St STOMAT", size);
            }
            if (fol.getFolderName().contains("Ur UROLOG")) {
                map.put("Ur UROLOG", size);
            }
            if (fol.getFolderName().contains("Vi VISION")) {
                map.put("Vi VISION ", size);
            }
        }

        return new ResponseEntity<>(JsonUtils.getJson(map), requestHeaders, HttpStatus.OK);
    }

}
