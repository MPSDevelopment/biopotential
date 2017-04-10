package com.mpsdevelopment.biopotential.server.cmp.machine;

import com.mpsdevelopment.biopotential.server.cmp._SoundIO;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
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
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml"})
@Configurable
public class MachineTest {

    private static final Logger LOGGER = LoggerUtil.getLogger(MachineTest.class);

    @Autowired
    private PatternsDao patternsDao;

    @Autowired
    private FoldersDao foldersDao;

    @Autowired
    private ServerSettings serverSettings;

    private static String edxFileFolder;


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

        Map<Pattern, AnalysisSummary> map = Machine.summarizePatterns(sample, list, 0);
        map.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                LOGGER.info("%s", pattern.getName());
                LOGGER.info("%s", analysisSummary.getDispersion());
                if (pattern.getName().contains("00009 (не совпадает)")) {
                    LOGGER.info("%s", pattern.getFileName());
                }
//                patterns.add(pattern);
            }
        });
        Assert.assertEquals(48, map.size());
        LOGGER.info("get map size %s", map.size());

        try {
            list = patternsDao.getPatternsIsCanBeReproduced(0);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        Map<Pattern, AnalysisSummary> diseaseMap = Machine.summarizePatterns(sample, list, 0);
        Assert.assertEquals(125, diseaseMap.size());

    }

    @Test
    public void getPcmData() {
        List<EDXPattern> patternsList = getInputListForTest();

        Long t1 = System.currentTimeMillis();
        patternsList.forEach(new Consumer<EDXPattern>() {
            @Override
            public void accept(EDXPattern edxPattern) {
                try {
                    Long t1 = System.currentTimeMillis();
                    Machine.getPcmData(edxPattern.getFileName(), false);
                    LOGGER.info("time getPcmData is %s ms", System.currentTimeMillis() - t1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        LOGGER.info("Patterns size is %s", patternsList.size());
        LOGGER.info("overall time is %s ms", System.currentTimeMillis() - t1);

    }

    @Test
    public void getPcmDataSingle() {

        try {
            Long t1 = System.currentTimeMillis();
            Machine.getPcmData("D:/MPS/IDEA/Biopotential material's/база автомат/my_super_puper_db_Storage/!5VbmZUWlK/1dae7585-b2cf9364-d3aa8437-2e578581-4639291.edx"
                    , false);
            LOGGER.info("time getPcmData is %s ms", System.currentTimeMillis() - t1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*ServerSettings serverSettings = (ServerSettings) BioApplication.APP_CONTEXT.getBean("serverSettings");
        serverSettings.setStoragePath("D:\\MPS\\IDEA\\Biopotential material's\\база автомат\\My_H2_Database\\");*/
        Machine.setEdxFileFolder(serverSettings.getStoragePath());

        Long t1 = System.currentTimeMillis();
        try {
            Machine.getPcmData("ZViCTL2oCG\\1a03af4e-e34e48dd-d1a136b2-a300435f-8936eab9.edx");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        LOGGER.info("time getPcmData is %s ms", System.currentTimeMillis() - t1);
    }

    @Test
    public void getPcmDataFromFile() {

        ServerSettings serverSettings = (ServerSettings) BioApplication.APP_CONTEXT.getBean("serverSettings");
        serverSettings.setStoragePath("D:\\MPS\\IDEA\\Biopotential material's\\база автомат\\My_H2_Database\\");
        Machine.setEdxFileFolder(serverSettings.getStoragePath());

        List<EDXPattern> patternsList = getInputListForTest();

        Long t1 = System.currentTimeMillis();
        patternsList.forEach(new Consumer<EDXPattern>() {
            @Override
            public void accept(EDXPattern edxPattern) {
                try {
                    Long t1 = System.currentTimeMillis();
                    Machine.getPcmData(edxPattern.getFileName());
                    LOGGER.info("time getPcmData is %s ms", System.currentTimeMillis() - t1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        LOGGER.info("Patterns size is %s", patternsList.size());
        LOGGER.info("overall time is %s ms", System.currentTimeMillis() - t1);
    }

    private List<EDXPattern> getInputListForTest() {
        List<EDXPattern> list = null;
        final List<EDXPattern> patternsList = new ArrayList<>();

        try {
            list = patternsDao.getAllPatternsFromDatabase();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("list size %s", list.size());

        list.forEach(new Consumer<EDXPattern>() {
            @Override
            public void accept(EDXPattern edxPattern) {
                try {
//                    try (RandomAccessFile in = new RandomAccessFile(new File("./data/edxfiles/" + edxPattern.getFileName()), "r")) {
                    try (RandomAccessFile in = new RandomAccessFile(new File("D:\\MPS\\IDEA\\Biopotential material's\\база автомат\\My_H2_Database\\" + edxPattern.getFileName()), "r")) {
                        patternsList.add(edxPattern);
                    }
                } catch (IOException e) {

                }
            }
        });
        return patternsList;
    }


}
