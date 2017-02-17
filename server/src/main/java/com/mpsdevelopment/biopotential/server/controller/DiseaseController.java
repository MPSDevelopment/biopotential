package com.mpsdevelopment.biopotential.server.controller;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.dao.DiseaseDao;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@RequestMapping(ControllerAPI.DISEAS_CONTROLLER)
@Controller
public class DiseaseController {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiseaseController.class);
    private static final String PATH_FOR_RECORD = "data/upload/files";

    @Autowired
    private DiseaseDao diseaseDao;

    private Map<Pattern, AnalysisSummary> diseases;
    private HttpHeaders requestHeaders;
    private Map<Pattern, AnalysisSummary> allHealings;

    public DiseaseController() {

    }

    /* *
        fetch = {"corNotNull", "stress", "hidden"}
        degree = {"Max", "Min"}
     */
    @RequestMapping(value = ControllerAPI.DISEAS_CONTROLLER_GET_DISEASES, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> getDiseases(@RequestParam("file") MultipartFile file,  @PathVariable(value = "degree") String currentDegree, @PathVariable(value = "fetch") String fetch,
                                       @PathVariable(value = "gender") String gender) {
        String name = file.getOriginalFilename();
        int level = getLevel(currentDegree);
        requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-8");
        diseases = new HashMap<>();

        getDiseas(file, name,level,fetch,gender);

        LOGGER.info("diseases '%s' ", diseases.size());
        return new ResponseEntity<>(JsonUtils.getJson(diseases), requestHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = ControllerAPI.DISEAS_CONTROLLER_GET_HEALINGS, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> getHealings(@RequestParam("file") MultipartFile file, @PathVariable(value = "degree") String currentDegree, @PathVariable(value = "fetch") String fetch,
                                       @PathVariable(value = "gender") String gender) {
        String name = file.getOriginalFilename();
        int level = getLevel(currentDegree);
        diseases = new HashMap<>();
        allHealings = new HashMap<>();

        Map<Pattern, AnalysisSummary> temp = new HashMap<>();
        getDiseas(file, name,level,fetch,gender);

        // separate getDisease for man and woman
        /*diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                if (gender.equals("Woman")) {
                    if (!pattern.getKind().equals("Ma Man")) {
                        temp.put(pattern,analysisSummary);
                    }
                }
                else if (gender.equals("Man")) {
                    if (!pattern.getKind().equals("Fe Femely")) {
                        temp.put(pattern,analysisSummary);
                    }
                }

            }
        });
        diseases.clear();
        diseases.putAll(temp);*/

        try {
            allHealings.putAll(diseaseDao.getHealings(diseases, level));
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

        LOGGER.info("allHealings '%s' ", allHealings.size());
        return new ResponseEntity<>(JsonUtils.getJson(allHealings), requestHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = ControllerAPI.DISEAS_CONTROLLER_GET_HEALINGS_MAP, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> getHealingsMap(@RequestParam Map<String, String> map, @PathVariable(value = "degree") String currentDegree,
                                       @PathVariable(value = "gender") String gender) {
        LOGGER.info("Got Map");
        Map<Pattern,AnalysisSummary> healingsmap = new HashMap<>();
        allHealings = new HashMap<>();
        map.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                EDXPattern edxPattern = JsonUtils.fromJson(EDXPattern.class, s);
                AnalysisSummary analysisSummary = JsonUtils.fromJson(AnalysisSummary.class, s2);
                healingsmap.put(edxPattern,analysisSummary);
            }
        });

        int level = getLevel(currentDegree);
        try {
            allHealings.putAll(diseaseDao.getHealings(healingsmap, level));
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

        LOGGER.info("allHealings '%s' ", allHealings.size());
        return new ResponseEntity<>(JsonUtils.getJson(allHealings), requestHeaders, HttpStatus.OK);
    }


    private int getLevel(String degree) {
        int level = 0;
        if (degree.equals("Po")) {
            level = -2147483648;
        }
        return level;
    }

    private Map<Pattern, AnalysisSummary> getDiseas(MultipartFile file, String name,int degree, String fetch, String gender) {
        try {
            diseases.putAll(diseaseDao.getDeseases(multipartToFile(name, file),degree,fetch,gender));
        } catch (IOException | UnsupportedAudioFileException | SQLException e) {
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

