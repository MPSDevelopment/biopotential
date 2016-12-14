package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.PatternsFolders;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class PatternsFoldersDaoTest {

    private static final Logger LOGGER = LoggerUtil.getLogger(PatternsFoldersDaoTest.class);

    @Autowired
    private PatternsFoldersDao patternsFoldersDao;
    @Autowired
    private PatternsDao patternsDao;
    private PatternsFolders patternsFolders;

    @Test
    public void checkPatternsFolders() {

        List<Pattern> patternList = patternsDao.findAll();
        LOGGER.info("patternList size %s", patternList.size());

        for (Pattern pattern : patternList) {
            patternsFolders = new PatternsFolders();
            patternsFolders = patternsFoldersDao.getByPattern(pattern);
            patternsFoldersDao.saveOrUpdate(patternsFolders);

        }
        List<PatternsFolders> all = patternsFoldersDao.findAll();
        LOGGER.info("patternsFoldersDao size %s", all.size());







    }

}
