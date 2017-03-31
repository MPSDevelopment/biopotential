package com.mpsdevelopment.biopotential.server.cmp.analyzer;

import com.mpsdevelopment.biopotential.server.cmp._SoundIO;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        try {
            sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("./testfiles/test3.wav"))));
        } catch (IOException | UnsupportedAudioFileException e) {
            LOGGER.printStackTrace(e);
        }

//        List<EDXPattern> list = patternsDao.getPatterns(1);

//        Map<Pattern, AnalysisSummary> map = Machine.summarizePatterns(sample, list, 0);

        }




        public static Map<Pattern, AnalysisSummary> summarizePatterns(List<ChunkSummary> sampleSummary, List<EDXPattern> patterns, int degree) {
            final Map<Pattern, AnalysisSummary> summaries = new HashMap<>();
            AnalysisSummary summary;
            for (Pattern pattern : patterns) {
//			long t1 = System.currentTimeMillis();
                summary = Analyzer.compare(sampleSummary, pattern.getSummary());
//			LOGGER.info("Operation compare took %d ms", System.currentTimeMillis() - t1);
//            LOGGER.info("summary getDegree %s",summary.getDegree());
                if (summary != null && summary.getDegree() == degree) {
                    summaries.put(pattern, summary);
                }
            }
            return summaries;
        }

}
