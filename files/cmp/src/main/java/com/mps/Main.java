package com.mps;

import java.io.File;
import java.util.*;

import com.mps.analyzer.AnalysisSummary;
import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.machine.Machine;
import com.mps.machine.Strain;
import com.mps.machine.dbs.arkdb.ArkDB;
import com.mps.machine.dbs.h2db.H2DB;
import com.mps.machine.strains.EDXStrain;
import com.mps.pcm.PCM;

import javax.sound.sampled.AudioSystem;

public class Main {
    public static void main(String[] args) {
        try {
//            final ArkDB db = new ArkDB("test.arkdb");
//            db.setHealingFolders(Arrays.asList(490, 959, 2483));
//            db.setDiseaseFolders(Collections.singletonList(4328));
            final H2DB db = new H2DB("./database", "", "sa");
            db.setDiseaseFolderIds(Collections.singletonList(
                "1545990528770768896"));

            System.out.println("start");

            final List<ChunkSummary> sample =
                Analyzer.summarize(_SoundIO.readAllFrames(
                    AudioSystem.getAudioInputStream(new File("test.wav"))));

            final Map<Strain, AnalysisSummary> diseases = Machine.summarizeStrains(
                (strain, summary) -> summary.getDegree() == 0,
                sample,
                db.getDiseases());

            diseases.forEach((k, v) -> System.out.printf("%s\t%f\n",
                k.getName(),
                v.getDispersion()));

            final Map<String, Integer> probableKinds = Machine.filterKinds(
                (kind, count) -> count > 0, diseases);

            diseases.forEach((dk, dv) -> {
                System.out.printf("heals for %s %s\n", dk.getKind(), dk.getName());
                if (probableKinds.containsKey(dk.getKind())) {
                    final Map<Strain, AnalysisSummary> healings = Machine.summarizeStrains(
                        (strain, summary) -> summary.getDegree() == 0,
                        sample,
                        db.getIterForFolder(((EDXStrain) dk).getCorrectingFolder()));
                    healings.forEach((hk, hv) -> System.out.printf("%s %s\n",
                        hk.getKind(), hk.getName()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
