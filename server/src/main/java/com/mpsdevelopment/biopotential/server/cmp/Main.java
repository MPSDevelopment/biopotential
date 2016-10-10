package com.mpsdevelopment.biopotential.server.cmp;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.KindCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.SummaryCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DB;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.gui.analysis.AnalysisPanel;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;


import javax.sound.sampled.AudioSystem;

public class Main {

    private static final Logger LOGGER = LoggerUtil.getLogger(Main.class);


    public static void main(String[] args) {
        try {
//            final ArkDB db = new ArkDB("test.arkdb");
//            db.setHealingFolders(Arrays.asList(490, 959, 2483));
//            db.setDiseaseFolders(Collections.singletonList(4328));
            final H2DB db = new H2DB("./data/database", "", "sa");
//            db.setDiseaseFolderIds(Collections.singletonList("1545990528770768896"));

            LOGGER.info("start");
            // read input *.wav file, convert to AudioInputStream -> get list of double -> summarize, get ChunkSummary
            final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("test3.wav"))));

            // sample include 13 List<ChunkSummary> after wavelet transform
            // next step compare sample with all desease patterns from db and take with filter Max
            final Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(new SummaryCondition() {
                @Override
                public boolean test(Pattern strain, AnalysisSummary summary) {
                    return summary.getDegree() == 0/*|| summary.getDispersion() == -21*/;
                }
            }, sample, db.getDiseases());

            /*HashMap<Pattern, AnalysisSummary> diesease = new HashMap<>();
            PatternDB iter = db.getDiseases();
            while (true) {
                Pattern strain = iter.next();
                if (strain == null) {
                    break;
                }
                AnalysisSummary comparison = Analyzer.compare(sample, strain.getSummary());
                if (comparison.getDispersion() == 0) {
                    diseases.put(strain, comparison);
                }
            }*/


            diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                @Override
                public void accept(Pattern k, AnalysisSummary v) {
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
                    final Map<Pattern, AnalysisSummary> healings = Machine.summarizePatterns(
                            new SummaryCondition() {
                                @Override
                                public boolean test(Pattern strain, AnalysisSummary summary) { // и потом берутся только те которые summary.getDispersion() == 0 т.е. MAx
                                    return summary.getDegree() == 0;
                                }
                            },
                            sample,
                            db.getIterForFolder(((EDXPattern) dk).getCorrectingFolder())); // вытягиваются папка с коректорами для конкретной болезни BAC -> FL BAC
                    healings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                        @Override
                        public void accept(Pattern hk, AnalysisSummary hv) {
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
