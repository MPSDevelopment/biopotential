package com.mps;

import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.analyzer.AnalysisSummary;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Main {
    public static void main(String[] args) {
        try {
            final AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(new File("test.wav"));

            final double[][] frames = SoundIO.readAllFrames(audioStream);

            Analyzer.summarize(frames[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
