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
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.h2db.H2DBIter;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.pojo.Visit;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

import static org.terracotta.modules.ehcache.ToolkitInstanceFactoryImpl.LOGGER;

public class DiseaseDao {

	private static final Logger LOGGER = LoggerUtil.getLogger(DiseaseDao.class);

	private H2DB db;

	public DiseaseDao() throws H2DBException {
		db = new H2DB("./data/database", "", "sa");
	}

	public Map<Pattern, AnalysisSummary> getDeseases(
			/* Map<Pattern, AnalysisSummary> desease, */ File file) throws IOException, UnsupportedAudioFileException, H2DBException {

		final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));
		final Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(sample, db.getDiseases());
		// db.close();
		return diseases;
	}

	public Map<Pattern, AnalysisSummary> getHealings(Map<Pattern, AnalysisSummary> diseases, File file) throws H2DBException, IOException, UnsupportedAudioFileException {
		final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));

		final Map<String, Integer> probableKinds = getProbableKinds(diseases);

		List<List<Double>> lists = new ArrayList<>();

		HashMap<Pattern, AnalysisSummary> allHealings = new HashMap<Pattern, AnalysisSummary>();

		// long t1 = System.currentTimeMillis();
		getHealings(diseases, sample, probableKinds, allHealings);
		// LOGGER.info("Healing has been found for %d ms",
		// System.currentTimeMillis() - t1);

		allHealings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
			@Override
			public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
				List<Double> pcmData = pattern.getPCMData();
				lists.add(pcmData);
			}
		});

		return allHealings;
	}

	private void getHealings(Map<Pattern, AnalysisSummary> diseases, final List<ChunkSummary> sample, final Map<String, Integer> probableKinds,
			HashMap<Pattern, AnalysisSummary> allHealings) {
		diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
			@Override
			public void accept(Pattern dk, AnalysisSummary dv) {

				System.out.printf("heals for %s %s\n", dk.getKind(), dk.getName());

				if (probableKinds.containsKey(dk.getKind())) {

					long t1 = System.currentTimeMillis();

					
					H2DBIter iterForFolder = db.getIterForFolder(((EDXPattern) dk).getCorrectingFolder());
					LOGGER.info("iterForFolder took %d ms", System.currentTimeMillis() - t1);
					
					final Map<Pattern, AnalysisSummary> healings = Machine.summarizePatterns(sample, iterForFolder); // вытягиваются
					// папка
					// с
					// коректорами
					// для
					// конкретной
					// болезни
					// BAC
					// ->
					// FL
					// BAC

					LOGGER.info("SummarizePatterns took %d ms", System.currentTimeMillis() - t1);

					/*
					 * healings.forEach(new BiConsumer<Pattern,
					 * AnalysisSummary>() {
					 * 
					 * @Override public void accept(Pattern hk, AnalysisSummary
					 * hv) { // hk.getPCMData() LOGGER.info("%s %s\n",
					 * hk.getKind(), hk.getName(), hv.getDispersion());
					 * 
					 * } });
					 */
					allHealings.putAll(healings);
				}
			}

		});
	}

	private Map<String, Integer> getProbableKinds(Map<Pattern, AnalysisSummary> diseases) {
		return Machine.filterKinds(new KindCondition() {
			@Override
			public boolean test(String kind, int count) {
				return count > 0;
			}
		}, diseases);
	}

}
