package com.mpsdevelopment.biopotential.server.cmp.analyzer;


import com.mpsdevelopment.biopotential.server.cmp._SoundIO;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AnalyzerTest {

    private static final Logger LOGGER = LoggerUtil.getLogger(AnalyzerTest.class);
    private double[] file;

    @Before
    public void setUp() {
        try {
            file = _SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("./testfiles/test3.wav")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void summarizeTest() {

        List<ChunkSummary> sample = Analyzer.summarize(file);

        Assert.assertEquals(6.926361431720674E-5, sample.get(0).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.0058614597816126, sample.get(0).getDispersion(),0.000000001);
        Assert.assertEquals(4.263317458351548E-5, sample.get(1).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.00978264691798186, sample.get(1).getDispersion(),0.000000001);
        Assert.assertEquals(1.244616410519066E-4, sample.get(2).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.018033878713370245, sample.get(2).getDispersion(),0.000000001);
        Assert.assertEquals(1.4469332464421867E-4, sample.get(3).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.046082064936417415, sample.get(3).getDispersion(),0.000000001);
        Assert.assertEquals(-9.953348409376967E-5, sample.get(4).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.11254563022707258, sample.get(4).getDispersion(),0.000000001);
        Assert.assertEquals(-0.0017082824096170193, sample.get(5).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.40909679975521558, sample.get(5).getDispersion(),0.000000001);
        Assert.assertEquals(0.003039261190385399, sample.get(6).getMeanDeviation(),0.000000001);
        Assert.assertEquals(1.3994965929272791, sample.get(6).getDispersion(),0.000000001);
        Assert.assertEquals(-0.00649796770727178, sample.get(7).getMeanDeviation(),0.000000001);
        Assert.assertEquals(5.111194954842121, sample.get(7).getDispersion(),0.000000001);
        Assert.assertEquals(-0.009995260283908295, sample.get(8).getMeanDeviation(),0.000000001);
        Assert.assertEquals(4.415418639269811, sample.get(8).getDispersion(),0.000000001);
        Assert.assertEquals(0.0010205466138119978, sample.get(9).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.1084821045128211, sample.get(9).getDispersion(),0.000000001);
        Assert.assertEquals(-0.01821956627267665, sample.get(10).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.3604624706339126, sample.get(10).getDispersion(),0.000000001);
        Assert.assertEquals(-0.009063466008082437, sample.get(11).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.19923232341743582, sample.get(11).getDispersion(),0.000000001);
        Assert.assertEquals(-0.008857072946461486, sample.get(12).getMeanDeviation(),0.000000001);
        Assert.assertEquals(0.07847255602832057, sample.get(12).getDispersion(),0.000000001);

        LOGGER.info("sample");
    }
}
