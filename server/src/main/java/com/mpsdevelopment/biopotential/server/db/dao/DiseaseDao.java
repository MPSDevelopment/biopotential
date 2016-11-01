package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.cmp._SoundIO;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.KindCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.SummaryCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.pojo.Visit;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;

public class DiseaseDao {

	private static final Logger LOGGER = LoggerUtil.getLogger(DiseaseDao.class);

	@Autowired
	private PatternsDao patternsDao;

	public DiseaseDao() {
	}

	public Map<Pattern, AnalysisSummary> getDeseases(
			/* Map<Pattern, AnalysisSummary> desease, */ File file) throws IOException, UnsupportedAudioFileException, SQLException {

		final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));

		List<EDXPattern> patterns = patternsDao.getFromDatabase();

		// TODO Split summarizePatterns to 2 methods for decease and pattern
		final Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(sample, patterns);
        return diseases;
	}

	public Map<Pattern, AnalysisSummary> getHealings(Map<Pattern, AnalysisSummary> diseases, File file) throws IOException, UnsupportedAudioFileException {
		final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));

		final Map<String, Integer> probableKinds = getProbableKinds(diseases);

		List<List<Double>> lists = new ArrayList<>();

		HashMap<Pattern, AnalysisSummary> allHealings = new HashMap<Pattern, AnalysisSummary>();

		 long t1 = System.currentTimeMillis();
		getHealings(diseases, sample, probableKinds, allHealings);
		 LOGGER.info("Healing has been found for %d ms",
		 System.currentTimeMillis() - t1);

		allHealings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
			@Override
			public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
				List<Double> pcmData = pattern.getPcmData();
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

				LOGGER.info("heals for %s %s\n", dk.getKind(), dk.getName());

				if (probableKinds.containsKey(dk.getKind())) {

					long t1 = System.currentTimeMillis();

					List<EDXPattern> patterns;
					try {
						patterns = patternsDao.getFromDatabase(((EDXPattern) dk).getCorrectingFolder());

						LOGGER.info("iterForFolder took %d ms", System.currentTimeMillis() - t1);

						/**
						 * вытягиваются папка с коректорами для конкретной
						 * болезни BAC -> FL BAC
						 */
						final Map<Pattern, AnalysisSummary> healings = Machine.summarizePatterns(sample, patterns);

						LOGGER.info("SummarizePatterns took %d ms", System.currentTimeMillis() - t1);
						allHealings.putAll(healings);
					} catch (SQLException | IOException e) {
						e.printStackTrace();
					}
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
