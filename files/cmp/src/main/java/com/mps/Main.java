package com.mps;

import java.io.File;
import java.util.*;

import com.mps.analyzer.AnalysisSummary;
import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.machine.Machine;
import com.mps.machine.Strain;
import com.mps.machine.dbs.arkdb.ArkDB;
import com.mps.pcm.PCM;

import javax.sound.sampled.AudioSystem;

public class Main {
    public static void main(String[] args) {
        try {
            int i = 0;
            for (ChunkSummary cs : Analyzer.summarize(_SoundIO.readAllFrames(
                    AudioSystem.getAudioInputStream(new File("test3.wav"))))) {
                System.out.printf("%d %f %f\n", i, cs.getMeanDeviation(), cs.getDispersion());
                i += 1;
            }

            ArkDB db = new ArkDB("test.arkdb");
            db.setHealingFolders(Collections.singletonList(2287));
            db.setDiseaseFolders(Collections.singletonList(4328));

            System.out.println("start");

            final List<ChunkSummary> sample =
                Analyzer.summarize(PCM.fold(_SoundIO.readAllFrames(
                    AudioSystem.getAudioInputStream(new File("test3.wav"))),
                    0x10266));

            final Map<Strain, AnalysisSummary> diseases = Machine.summarizeStrains(
                (strain, summary) -> summary.getDegree() == 0,
                sample,
                db.getDiseases());

            final Map<String, Integer> probableKinds = Machine.filterKinds(
                (kind, count) -> count > 0, diseases);

            final Map<Strain, AnalysisSummary> healings = Machine.summarizeStrains(
                (strain, summary) -> probableKinds.containsKey(strain.getKind())
                        && summary.getDegree() == 0,
                sample,
                db.getHealings());

            diseases.forEach((k, v) ->
                System.out.printf("%s\t%d\n", k.getName(), v.getDegree()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
