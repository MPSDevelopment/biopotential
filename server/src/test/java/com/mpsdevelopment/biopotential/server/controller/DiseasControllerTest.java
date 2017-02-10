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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import java.io.File;
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
    public void getDiseasesTest() throws ServletException {

        JettyServer server = JettyServer.getInstance();
        server.start();

        String gender = "Man";
        String degree = "Max";
        File file = new File("./testfiles/REC005.ACT");

        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();
        String json = bioHttpClient.executePostRequest("/api/diseas/" + gender + "/" + STRESS + "/" + degree + "/getDiseas" , file);

        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();
        Map<Pattern, AnalysisSummary> diseasesStressMax = JsonUtils.fromJson(typeOfHashMap, json);
        LOGGER.info("%s", diseasesStressMax.size());
        Assert.assertEquals(63, diseasesStressMax.size());

    }
}
