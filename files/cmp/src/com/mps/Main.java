package com.mps;

import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.analyzer.AnalysisSummary;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;

import com.mps.machine.Machine;
import com.mps.machine.Strain;
import com.mps.machine.WAWStrain;

public class Main {
    public static void main(String[] args) {
        try {
//            final double[][] sample = SoundIO.readAllFrames(
//                    AudioSystem.getAudioInputStream(new File("test.wav")));
//
//            final ChunkSummary[] sampleChunks = Analyzer.summarize(sample[0]);
//            final ChunkSummary[] strainChunks = Analyzer.readSummaryFromWAW(
//                    new FileInputStream("test.waw"));
//
//            final AnalysisSummary summary =
//                    Analyzer.compare(sampleChunks, strainChunks);
//
//            System.out.println(String.format("%f %f %d",
//                    summary.getMeanDeviation(),
//                    summary.getDispersion(),
//                    summary.getDegree()));
            final Machine machine = new Machine();
            machine.setDiseaseStrains(
                    new ArrayList<Strain>() {{
                        add(new WAWStrain("g1", "n1", "ills/1.waw"));
                        add(new WAWStrain("g1", "n2", "ills/2.waw"));
                        add(new WAWStrain("g2", "n3", "ills/3.waw"));
                        add(new WAWStrain("g2", "n4", "ills/4.waw"));
                        add(new WAWStrain("g3", "n5", "ills/5.waw"));
                        add(new WAWStrain("g3", "n6", "ills/6.waw"));
                    }}
            );
            final double[][] sample = SoundIO.readAllFrames(
                    AudioSystem.getAudioInputStream(new File("test.wav")));
            machine.scan(Analyzer.summarize(sample[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
