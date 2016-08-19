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
            final ArkDB db = new ArkDB("test.arkdb");
            db.setHealingFolders(Arrays.asList(490, 959, 2483));
            db.setDiseaseFolders(Collections.singletonList(4328));

            System.out.println("start");

            final List<ChunkSummary> sample =
                Analyzer.summarize(_SoundIO.readAllFrames(
                    AudioSystem.getAudioInputStream(new File("test.wav"))));

            final Map<Strain, AnalysisSummary> diseases = Machine.summarizeStrains(
                (strain, summary) -> summary.getDegree() == 0,
                sample,
                db.getDiseaseIds());

//            final Map<String, Integer> probableKinds = Machine.filterKinds(
//                (kind, count) -> count > 0, diseases);
//
//            final Map<Strain, AnalysisSummary> healings = Machine.summarizeStrains(
//                (strain, summary) -> probableKinds.containsKey(strain.getKind()) && probableKinds.get(strain.getKind()) > 0 && summary.getDegree() == 0,
//                sample,
//                db.getHealingIds());

            diseases.forEach((k, v) ->
                System.out.printf("d: %s\t%d\n", k.getName(), v.getDegree()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
