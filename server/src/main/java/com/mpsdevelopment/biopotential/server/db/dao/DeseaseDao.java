package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.cmp._SoundIO;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.KindCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.SummaryCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DB;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DBException;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.pojo.Visit;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

import static org.terracotta.modules.ehcache.ToolkitInstanceFactoryImpl.LOGGER;

public class DeseaseDao {
    Map<Pattern, AnalysisSummary> allHealings = new HashMap<Pattern, AnalysisSummary>();

    public Map<Pattern, AnalysisSummary> getDeseases(/*Map<Pattern, AnalysisSummary> desease,*/ File file) throws IOException, UnsupportedAudioFileException, H2DBException {
        final H2DB db = new H2DB("./data/database", "", "sa");

        final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));
        final Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(new SummaryCondition() {
            @Override
            public boolean test(Pattern strain, AnalysisSummary summary) {
                return summary.getDegree() == 0 /*|| summary.getDispersion() == -21*/;
            }
        }, sample, db.getDiseases());
//        db.close();
        return diseases;
    }

    public Map<Pattern, AnalysisSummary> getHealings (Map<Pattern, AnalysisSummary> diseases, File file) throws H2DBException, IOException, UnsupportedAudioFileException {
        final H2DB db = new H2DB("./data/database", "", "sa");
        final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));


        final Map<String, Integer> probableKinds = Machine.filterKinds(new KindCondition() {
            @Override
            public boolean test(String kind, int count) {
                return count > 0;
            }
        }, diseases);

        Collection lists = new ArrayList();

        diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern dk, AnalysisSummary dv) {
                System.out.printf("heals for %s %s\n", dk.getKind(), dk.getName());
                if (probableKinds.containsKey(dk.getKind())) {
                    final Map<Pattern, AnalysisSummary>
                            healings = Machine.summarizePatterns(new SummaryCondition() {
                        @Override
                        public boolean test(Pattern pattern, AnalysisSummary summary) { // и потом берутся только те которые summary.getDispersion() == 0 т.е. MAx
                            return summary.getDegree() == 0;
                        }
                    }, sample, db.getIterForFolder(((EDXPattern) dk).getCorrectingFolder())); // вытягиваются папка с коректорами для конкретной болезни BAC -> FL BAC


                    healings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
                        @Override
                        public void accept(Pattern hk, AnalysisSummary hv) {
//                            hk.getPCMData()
                            LOGGER.info("%s %s\n", hk.getKind(), hk.getName(), hv.getDispersion());

                        }
                    });
                    allHealings.putAll(healings);
                }
            }

        });

        allHealings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                List<Double> pcmData = pattern.getPCMData();
                lists.add(pcmData);
            }
        });

        return allHealings;
    }

    }

