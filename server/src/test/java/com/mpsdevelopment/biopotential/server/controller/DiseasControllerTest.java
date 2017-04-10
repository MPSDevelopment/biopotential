package com.mpsdevelopment.biopotential.server.controller;

import com.auth0.jwt.JWTVerifyException;
import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.pojo.Token.Role;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.TokenUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context.xml", "classpath:/webapp/web-context.xml", "classpath:/webapp/settings-context.xml" })
@Configurable
public class DiseasControllerTest {

    private static final Logger LOGGER = LoggerUtil.getLogger(DiseaseController.class);

    @Autowired
    private DiseaseController diseaseController;

    private static final String STRESS = "stress";
    private static final String COR_NOT_NULL = "corNotNull";
    private static final String HIDDEN = "hidden";
    private String gender;
    private String degree;
    
	@Mock
	@ReplaceWithMock
	@Autowired
	private TokenUtils tokenUtils;

	@Before
	public void setUp() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		Mockito.when(tokenUtils.getRoleFromToken(Matchers.anyString())).thenReturn(Role.ADMIN);
        /*gender = "Man";
        degree = "Max";*/
	}

    public DiseasControllerTest() {

    }

    @Test
    public void getDiseasesTest() throws ServletException, IOException {
        String gender = "Man";
        String degree = "Max";
        File file = new File("./testfiles/REC005.ACT");
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile fstmp = new MockMultipartFile("upload", file.getName(), "multipart/form-data",fileInputStream);

        ResponseEntity<String> diseases= diseaseController.getDiseases(fstmp,degree,STRESS,gender);
        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();

        Map<Pattern, AnalysisSummary> diseasesStressMax = JsonUtils.fromJson(typeOfHashMap, diseases.getBody());
        LOGGER.info("%s", diseasesStressMax.size());
        Assert.assertEquals(63, diseasesStressMax.size());

        ResponseEntity<String> healings = diseaseController.getDiseases(fstmp,degree,STRESS,gender);

    }

    @Test
    public void getHealingsTest() throws ServletException, IOException {
        /*String gender = "Man";
        String degree = "Max";*/
        File file = new File("./testfiles/REC005.ACT");
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile fstmp = new MockMultipartFile("upload", file.getName(), "multipart/form-data",fileInputStream);

        ResponseEntity<String> healings= diseaseController.getHealings(fstmp,degree,STRESS,gender);
        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();

        Map<Pattern, AnalysisSummary> healingsMap = JsonUtils.fromJson(typeOfHashMap, healings.getBody());
        LOGGER.info("%s", healingsMap.size());
        Assert.assertEquals(6, healingsMap.size());

    }
}
