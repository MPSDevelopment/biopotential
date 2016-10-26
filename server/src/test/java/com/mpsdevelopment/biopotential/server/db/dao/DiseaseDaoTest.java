package com.mpsdevelopment.biopotential.server.db.dao;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DB;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DBException;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class DiseaseDaoTest {

    @Autowired
    private DiseaseDao diseaseDao;

    private static final Logger LOGGER = LoggerUtil.getLogger(com.mpsdevelopment.biopotential.server.db.dao.DiseaseDaoTest.class);
    Map<Pattern, AnalysisSummary> allHealings;
    Map<Pattern, AnalysisSummary> diseases;
    private File file;
    private H2DB db;

    public DiseaseDaoTest() throws H2DBException {
        db = new H2DB("./data/database", "", "sa");
        diseases = new HashMap<>();
        allHealings = new HashMap<>();
        file = new File("test3.wav");
    }

    @Test
    public void getHealingsTest() throws IOException, UnsupportedAudioFileException {
        diseases.put(new EDXPattern("FLORA dissection", "Muc Грибок поражение общее", "АНАЛИЗ","./data/edxfiles/#13gw7N91D/34527cd5-10b8ea35-af13d6d6-5b45a763-742acda9.edx","1549081936653189120"),new AnalysisSummary(0.004432397546419209,0.931693093422437,0 ));
        diseases.put(new EDXPattern("FLORA dissection", "VIR Вирусная аминокислота", "АНАЛИЗ","./data/edxfiles/ZViCTL2oCG/c70ebc91-2e60e663-187c1692-cf72707a-b622503d.edx","1549081936652140544"),new AnalysisSummary(0.004556173769667698,0.932749751807533,0));
        diseases.put(new EDXPattern("FLORA dissection", "BAC Бактерии", "АНАЛИЗ","./data/edxfiles/_sn~Ak@$~a/250b8a66-1d36c921-daf986c0-d008c42f-661a00cf.edx","1549081936663674880"),new AnalysisSummary(0.004402693132922528,0.9353995049351229,0));
        try {
            Map<Pattern, AnalysisSummary> healings = diseaseDao.getHealings(diseases,file);
            LOGGER.info("test complete");
            Assert.assertEquals(105, healings.size());
        } catch (H2DBException e) {
            LOGGER.printStackTrace(e);
        }

    }

}







