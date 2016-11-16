package com.mpsdevelopment.biopotential.server.controller;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.db.dao.DiseaseDao;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping(ControllerAPI.DISEAS_CONTROLLER)
@Controller
public class DiseasController {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiseasController.class);
    private static final String PATH_FOR_RECORD = "data/upload/files";

    @Autowired
    private DiseaseDao diseaseDao;

    private Map<Pattern, AnalysisSummary> diseases;
    private HttpHeaders requestHeaders;
    private Map<Pattern, AnalysisSummary> allHealings;

    public DiseasController() {
    }

    @RequestMapping(value = "/getDiseas", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<String> getDiseases(@RequestParam("file") MultipartFile file) {
        String name = file.getOriginalFilename();
        requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-8");
        diseases = new HashMap<>();

        getDiseas(file, name);

        LOGGER.info("diseases '%s' ", diseases.size());
        return new ResponseEntity<>(JsonUtils.getJson(diseases), requestHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/getHealings", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<String> getHealings(@RequestParam("file") MultipartFile file) {
        String name = file.getOriginalFilename();
        allHealings = new HashMap<>();

        getDiseas(file, name);

        try {
            allHealings.putAll(diseaseDao.getHealings(diseases, multipartToFile(name, file)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

        LOGGER.info("allHealings '%s' ", allHealings.size());
        return new ResponseEntity<>(JsonUtils.getJson(allHealings), requestHeaders, HttpStatus.OK);
    }

    private Map<Pattern, AnalysisSummary> getDiseas(MultipartFile file, String name) {
        try {
            diseases.putAll(diseaseDao.getDeseases(multipartToFile(name, file)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return diseases;
    }

    private File multipartToFile(String fileName, MultipartFile multipart) {

        File folder = new File(PATH_FOR_RECORD);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String path = String.format("%s/%s", PATH_FOR_RECORD, fileName);
        File convFile = new File(path);

        try {
            multipart.transferTo(convFile);
        } catch (IOException e) {
            return convFile;
        }
        return convFile;
    }
}

