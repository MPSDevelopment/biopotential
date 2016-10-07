package com.mpsdevelopment.biopotential.server.cmp;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.KindCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Strain;
import com.mpsdevelopment.biopotential.server.cmp.machine.SummaryCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DB;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXStrain;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;


import javax.sound.sampled.AudioSystem;

public class Main {
    public static void main(String[] args) {
        try {
//            final ArkDB db = new ArkDB("test.arkdb");
//            db.setHealingFolders(Arrays.asList(490, 959, 2483));
//            db.setDiseaseFolders(Collections.singletonList(4328));
            final H2DB db = new H2DB("./data/database", "", "sa");
//            db.setDiseaseFolderIds(Collections.singletonList("1545990528770768896"));

            System.out.println("start");

            final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("test3.wav"))));

            final Map<Strain, AnalysisSummary> diseases = Machine.summarizeStrains(new SummaryCondition() {
                        @Override
                        public boolean test(Strain strain, AnalysisSummary summary) {
                            return summary.getDegree() == 0/*|| summary.getDispersion() == -21*/
;
                        }
                    },sample, db.getDiseases());

/*HashMap<Strain, AnalysisSummary> diesease = new HashMap<>();
            StrainDB iter = db.getDiseases();
            while (true) {
                Strain strain = iter.next();
                if (strain == null) {
                    break;
                }
                AnalysisSummary comparison = Analyzer.compare(sample, strain.getSummary());
                if (comparison.getDispersion() == 0) {
                    diseases.put(strain, comparison);
                }
            }*/


            diseases.forEach(new BiConsumer<Strain, AnalysisSummary>() {
                @Override
                public void accept(Strain k, AnalysisSummary v) {
                    System.out.printf("%s\t%f\n",
                            k.getName(),
                            v.getDispersion());
                }
            });

            final Map<String, Integer> probableKinds = Machine.filterKinds(new KindCondition() {
                        @Override
                        public boolean test(String kind, int count) {
                            return count > 0;
                        }
                    }, diseases);


            diseases.forEach((dk, dv) -> {
                System.out.printf("heals for %s %s\n", dk.getKind(), dk.getName());
                if (probableKinds.containsKey(dk.getKind())) {
                    final Map<Strain, AnalysisSummary> healings = Machine.summarizeStrains(
                            new SummaryCondition() {
                                @Override
                                public boolean test(Strain strain, AnalysisSummary summary) { // и потом берутся только те которые summary.getDispersion() == 0 т.е. MAx
                                    return summary.getDegree() == 0;
                                }
                            },
                        sample,
                        db.getIterForFolder(((EDXStrain) dk).getCorrectingFolder())); // вытягиваются папка с коректорами для конкретной болезни BAC -> FL BAC
                    healings.forEach(new BiConsumer<Strain, AnalysisSummary>() {
                        @Override
                        public void accept(Strain hk, AnalysisSummary hv) {
//                            hk.getPCMData()
                            System.out.printf("%s %s %s\n",
                                    hk.getKind(), hk.getName(), hv.getDispersion());
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
