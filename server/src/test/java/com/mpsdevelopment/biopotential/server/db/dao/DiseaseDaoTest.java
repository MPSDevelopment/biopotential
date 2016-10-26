package com.mpsdevelopment.biopotential.server.db.dao;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.TestSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DB;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DBException;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DBIter;
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
import java.util.*;
import java.util.function.BiConsumer;

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

    private static Pattern pattern;
    private int count=0;


    public DiseaseDaoTest() throws H2DBException {
        db = new H2DB("./data/database", "", "sa");
        diseases = new HashMap<>();
        allHealings = new HashMap<>();
        file = new File("test3.wav");
    }

    @Test
    public void getHealingsTest() throws IOException, UnsupportedAudioFileException {
        try {
            diseases = diseaseDao.getDeseases(file);
        } catch (H2DBException e) {
            e.printStackTrace();
        }

        /*diseases.put(new EDXPattern("FLORA dissection", "Muc Грибок поражение общее", "АНАЛИЗ","./data/edxfiles/#13gw7N91D/34527cd5-10b8ea35-af13d6d6-5b45a763-742acda9.edx","1549081936653189120"),new AnalysisSummary(0.004432397546419209,0.931693093422437,0 ));
        diseases.put(new EDXPattern("FLORA dissection", "VIR Вирусная аминокислота", "АНАЛИЗ","./data/edxfiles/ZViCTL2oCG/c70ebc91-2e60e663-187c1692-cf72707a-b622503d.edx","1549081936652140544"),new AnalysisSummary(0.004556173769667698,0.932749751807533,0));
        diseases.put(new EDXPattern("FLORA dissection", "BAC Бактерии", "АНАЛИЗ","./data/edxfiles/_a1ACEXkHq/e62689dc-dcd5ff76-1874d4e2-4b8e344-8ce8d6e1.edx","1549081936663674880"),new AnalysisSummary(0.004402693132922528,0.9353995049351229,0));
*/        try {
            Map<Pattern, AnalysisSummary> healings = diseaseDao.getHealings(diseases,file);
            /*LOGGER.info("testSummaries complete");
            healings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                @Override
                public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                    if(pattern.getName().equals("Ключ 1 блокировки микозов") || pattern.getName().equals("Герпес - группа") || pattern.getName().equals("Валацикловир")
                            || pattern.getName().equals("Вирус цитомегалии (ЦМВ) D  5") || pattern.getName().equals("Aspergillus niger D5") || pattern.getName().equals("Канестен")
                            || pattern.getName().equals("Окоферон") || pattern.getName().equals("Mucor racemosus D6") || pattern.getName().equals("Comedones")
                            || pattern.getName().equals("Гепатит  E|B") || pattern.getName().equals("Вирус натуральной оспы D 15") || pattern.getName().equals("Вирус Коксаки, серотип B5 D4")
                            || pattern.getName().equals("Цефтриаксон 250") || pattern.getName().equals("Pau Darco (Паударко)") || pattern.getName().equals("Вирус ветряной оспы D 30")
                            || pattern.getName().equals("Зиннат") || pattern.getName().equals("Азаран") || pattern.getName().equals("Клион")
                            || pattern.getName().equals("Кетоконазол") || pattern.getName().equals("Протефлазид") || pattern.getName().equals("Вирус опояс лишая и ветрян оспы D 15")
                            || pattern.getName().equals("Эконазол") || pattern.getName().equals("Максипим") || pattern.getName().equals("Penicillium glabruml D3")
                            || pattern.getName().equals("Перхотал") || pattern.getName().equals("Вирус бешенства D 60") || pattern.getName().equals("Вирус цитомегалии (ЦМВ)"))  {
                        count++;
                    }
                }
            });
            Assert.assertTrue(count == 27);*/

            Set<Pattern> keys = new HashSet<>(healings.keySet());
            List<TestSummary> testSummaries = new ArrayList<>();

            for (Pattern temp: keys) {
                testSummaries.add(new TestSummary(temp.getKind(),temp.getName(),temp.getDescription(),healings.get(temp).getDispersion()));
            }

            testSummaries.sort(new Comparator<TestSummary>() {
                @Override
                public int compare(TestSummary o1, TestSummary o2) {
                    return o2.getDispersion().toString().compareTo(o1.getDispersion().toString());
                }
            });

            Assert.assertEquals(105, healings.size());
//            Assert.assertTrue(0.9307556218678325 == healings.get(new EDXPattern("FL Muc eхo","Ключ 1 блокировки микозов", "Иммунная детоксикация", "./data/edxfiles/RKK_@Sv$vm/dedb8ef0-ad37dda7-ee388a3e-571b0827-e13ae050.edx")).getDispersion());

            Assert.assertEquals(0.9428119761134071,0.9428119761134071, testSummaries.get(0).getDispersion());
            Assert.assertEquals(0.9403581859325427,0.9403581859325427, testSummaries.get(1).getDispersion());
            Assert.assertEquals(0.9402918941295554,0.9402918941295554,  testSummaries.get(2).getDispersion());
            Assert.assertEquals(0.940230391850338,0.940230391850338,  testSummaries.get(3).getDispersion());
            Assert.assertEquals(0.9401602749365161,0.9401602749365161,  testSummaries.get(4).getDispersion());
            Assert.assertEquals(0.9400048256478736,0.9400048256478736,  testSummaries.get(5).getDispersion());
            Assert.assertEquals(0.9396519675275893,0.9396519675275893,  testSummaries.get(6).getDispersion());
            Assert.assertEquals(0.9395446663374385,0.9395446663374385,  testSummaries.get(7).getDispersion());
            Assert.assertEquals(0.9394064078893043,0.9394064078893043,  testSummaries.get(8).getDispersion());
            Assert.assertEquals(0.9391722653463764,0.9391722653463764,  testSummaries.get(9).getDispersion());
            Assert.assertEquals(0.9380269027016215,0.9380269027016215, testSummaries.get(10).getDispersion());
            Assert.assertEquals(0.9374881081074107,0.9374881081074107,  testSummaries.get(11).getDispersion());
            Assert.assertEquals(0.93712043818981,0.93712043818981,  testSummaries.get(12).getDispersion());
            Assert.assertEquals(0.9369130363249805,0.9369130363249805,  testSummaries.get(13).getDispersion());
            Assert.assertEquals(0.9367212979067614,0.9367212979067614,  testSummaries.get(14).getDispersion());

        } catch (H2DBException e) {
            LOGGER.printStackTrace(e);
        }

    }

}







