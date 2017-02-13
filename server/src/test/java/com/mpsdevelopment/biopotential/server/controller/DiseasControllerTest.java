package com.mpsdevelopment.biopotential.server.controller;

import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.httpclient.HttpClientFactory;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class DiseasControllerTest {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiseasController.class);

    @Autowired
    private DiseasController diseasController;

    private static final String STRESS = "stress";
    private static final String COR_NOT_NULL = "corNotNull";
    private static final String HIDDEN = "hidden";

    public DiseasControllerTest() {

    }

    @Test
    public void getDiseasesTest() throws ServletException, IOException {
        String gender = "Man";
        String degree = "Max";
        File file = new File("./testfiles/REC005.ACT");
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile fstmp = new MockMultipartFile("upload", file.getName(), "multipart/form-data",fileInputStream);

        ResponseEntity<String> diseases= diseasController.getDiseases(fstmp,degree,STRESS,gender);

        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();
        Map<Pattern, AnalysisSummary> diseasesStressMax = JsonUtils.fromJson(typeOfHashMap, diseases.getBody().toString());
        LOGGER.info("%s", diseasesStressMax.size());
        Assert.assertEquals(63, diseasesStressMax.size());

        ResponseEntity<String> healings = diseasController.getDiseases(fstmp,degree,STRESS,gender);

    }

    @Test
    public void getHealingsTest() throws ServletException, IOException {
        String gender = "Man";
        String degree = "Max";
        File file = new File("./testfiles/REC005.ACT");
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile fstmp = new MockMultipartFile("upload", file.getName(), "multipart/form-data",fileInputStream);

        ResponseEntity<String> healings= diseasController.getHealings(fstmp,degree,STRESS,gender);

        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();
        Map<Pattern, AnalysisSummary> healingsMap = JsonUtils.fromJson(typeOfHashMap, healings.getBody().toString());
        LOGGER.info("%s", healingsMap.size());
        Assert.assertEquals(6, healingsMap.size());

    }
}
