package com.mps;

import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.analyzer.AnalysisSummary;

import java.io.File;
import javax.sound.sampled.AudioSystem;

public class Main {
    public static void main(String[] args) {
        try {
            final double[][] frames = SoundIO.readAllFrames(
                    AudioSystem.getAudioInputStream(new File("test.wav")));

            final ChunkSummary[] t1 = Analyzer.summarize(frames[0]);
            final AnalysisSummary t2 = Analyzer.compare(t1, t1);

            System.out.println(String.format("%f %f %d",
                    t2.getMeanDeviation(),
                    t2.getDispersion(),
                    t2.getDegree()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
