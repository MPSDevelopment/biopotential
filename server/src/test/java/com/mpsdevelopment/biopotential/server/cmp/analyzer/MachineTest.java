package com.mpsdevelopment.biopotential.server.cmp.analyzer;

import com.mpsdevelopment.biopotential.server.cmp._SoundIO;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class MachineTest {

    private static final Logger LOGGER = LoggerUtil.getLogger(MachineTest.class);

    private static String edxFileFolder;
    private static final String EDX_FILE_FOLDER = "data/edxfiles/";

    @Autowired
    private PatternsDao patternsDao;

    @Test
    public void summarizePatternsTest() {
        List<ChunkSummary> sample = null;
        List<EDXPattern> list = null;
        try {
            sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("./testfiles/test3.wav"))));
        } catch (IOException | UnsupportedAudioFileException e) {
            LOGGER.printStackTrace(e);
        }

        try {
            list = patternsDao.getPatternsIsCanBeReproduced(1);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

//        List<Pattern> patterns = new ArrayList<>();

        Map<Pattern, AnalysisSummary> map = Machine.summarizePatterns(sample, list, 0);
        /*map.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                LOGGER.info("%s", pattern.getFileName());
                patterns.add(pattern);
            }
        });*/
        Assert.assertEquals(4, map.size());
        LOGGER.info("get map size %s", map.size());

        /*Set<Pattern> hs = new HashSet<>();
        hs.addAll(patterns);
        patterns.clear();
        patterns.addAll(hs);

        Assert.assertEquals(70, patterns.size());*/

        try {
            list = patternsDao.getPatternsIsCanBeReproduced(0);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        Map<Pattern, AnalysisSummary> diseaseMap = Machine.summarizePatterns(sample, list, 0);
        Assert.assertEquals(3, diseaseMap.size());

    }

}
