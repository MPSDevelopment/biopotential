package com.mpsdevelopment.biopotential.server.db.dao;


import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class PatternsDaoTest {

    private static final Logger LOGGER = LoggerUtil.getLogger(PatternsDaoTest.class);

    @Autowired
    private PatternsDao patternsDao;

    @Test
    public void getPatternsTest() {

        List<Pattern> list = patternsDao.getPatterns(1);

        /*for (int i = 0; i < list.size(); i++) {
            int count = 0;
            for (int j = 0; j < list.size(); j++) {
                if(list.get(i).getPatternUid().equals(list.get(j).getPatternUid())) {
                    count++;
                    if(count > 1) {
                        LOGGER.info("name %s", list.get(i).getPatternName());
                        LOGGER.info("filename %s", list.get(i).getPatternUid());

                    }
                }
                count = 0;
            }
        }*/

        Set<Pattern> hs = new HashSet<>();
        hs.addAll(list);
        list.clear();
        list.addAll(hs);

        Assert.assertEquals(605, list.size());
        LOGGER.info("List size %s ", list.size());

        /*list = patternsDao.getPatterns(0);
        Assert.assertEquals(856, list.size());
        LOGGER.info("List size %s ", list.size());*/

    }

    @Test
    public void getPatternsIsCanBeReproducedTest() {
        List<EDXPattern> list = null;
        try {
            list = patternsDao.getPatternsIsCanBeReproduced(1);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        /*list.forEach(new Consumer<EDXPattern>() {
            @Override
            public void accept(EDXPattern edxPattern) {
//                LOGGER.info("%s", edxPattern.getName());
                LOGGER.info("%s", edxPattern.getIsCanBeReproduced());
            }
        });
*/
        for (int i = 0; i < list.size(); i++) {
            int count = 0;
            for (int j = 0; j < list.size(); j++) {
                if(list.get(i).getFileName().equals(list.get(j).getFileName())) {
                    count++;
                    if(count > 1) {
                        LOGGER.info("name %s", list.get(i).getName());
                        LOGGER.info("filename %s", list.get(i).getFileName());

                    }
                }
                count = 0;
            }

        }

        Set<EDXPattern> hs = new HashSet<>();
        hs.addAll(list);
        list.clear();
        list.addAll(hs);

        Assert.assertEquals(605, list.size());
        LOGGER.info("getAllPatternsFromDataBaseTest");
    }

}
