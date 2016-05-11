package com.mps;

import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.analyzer.AnalysisSummary;

import java.io.File;
import java.io.FileInputStream;
import javax.sound.sampled.AudioSystem;

public class Main {
    public static void main(String[] args) {
        try {
            final double[][] sample = SoundIO.readAllFrames(
                    AudioSystem.getAudioInputStream(new File("test.wav")));

            final ChunkSummary[] sampleChunks = Analyzer.summarize(sample[0]);
            final ChunkSummary[] strainChunks = Analyzer.readSummaryFromStream(
                    new FileInputStream("test.waw"));

            final AnalysisSummary summary =
                    Analyzer.compare(sampleChunks, strainChunks);

            System.out.println(String.format("%f %f %d",
                    summary.getMeanDeviation(),
                    summary.getDispersion(),
                    summary.getDegree()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
