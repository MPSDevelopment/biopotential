package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.cmp._SoundIO;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.KindCondition;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class DiseaseDao {

	private static final Logger LOGGER = LoggerUtil.getLogger(DiseaseDao.class);

	@Autowired
	private PatternsDao patternsDao;

    @Autowired
    private FoldersDao foldersDao;

	public DiseaseDao() {
	}

	public Map<Pattern, AnalysisSummary> getDeseases(File file, int degree) throws IOException, UnsupportedAudioFileException, SQLException {

		final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));
		/*
		get all patterns from database which have id correctors folders
		 */
        Folder stressAnalys = foldersDao.getByName("Stress Analys");
        Folder destruction = foldersDao.getByName("Di Деструкция");
        Folder metabolism = foldersDao.getByName("Me Метаболизм");
        Folder physCond = foldersDao.getByName("Bо Физ кодиции");

		List<EDXPattern> patterns = patternsDao.getFromDatabase();

		List<EDXPattern> patternsStressAnalys = patternsDao.getPatternsFromFolders(stressAnalys);
		List<EDXPattern> patternsDestruction = patternsDao.getPatternsFromFolders(destruction);
		List<EDXPattern> patternsMetabolism = patternsDao.getPatternsFromFolders(metabolism);
		List<EDXPattern> patternsPhysCond = patternsDao.getPatternsFromFolders(physCond);

//		List<EDXPattern> patternsNull = patternsDao.getFromDatabase(0);

		// TODO Split summarizePatterns to 2 methods for decease and pattern
		final Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(sample, patterns, degree);

		final Map<Pattern, AnalysisSummary> diseasesStressAnalys = Machine.summarizePatterns(sample, patternsStressAnalys, degree);
		final Map<Pattern, AnalysisSummary> diseasesDestruction = Machine.summarizePatterns(sample, patternsDestruction, degree);
		final Map<Pattern, AnalysisSummary> diseasesMetabolism = Machine.summarizePatterns(sample, patternsMetabolism, degree);
		final Map<Pattern, AnalysisSummary> diseasesPhysCond = Machine.summarizePatterns(sample, patternsPhysCond, degree);

        diseases.putAll(diseasesStressAnalys);
        diseases.putAll(diseasesDestruction);
        diseases.putAll(diseasesMetabolism);
        diseases.putAll(diseasesPhysCond);

        return diseases;
	}

	/**
	 * HashMap<Pattern, AnalysisSummary> allHealings contain's only unique keys define by hashcode method in EDXPattern class
	 */
	public Map<Pattern, AnalysisSummary> getHealings(Map<Pattern, AnalysisSummary> diseases, File file, int level) throws IOException, UnsupportedAudioFileException {

		final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));

		final Map<String, Integer> probableKinds = getProbableKinds(diseases);

		HashMap<Pattern, AnalysisSummary> allHealings = new HashMap<>();

		long t1 = System.currentTimeMillis();
        if (level != -2147483648) {
            getHealings(diseases, sample, probableKinds, allHealings, level);
        }
		LOGGER.info("Healing has been found for %d ms",	System.currentTimeMillis() - t1);

		/*long t2 = System.currentTimeMillis();
		allHealings.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
			@Override
			public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
				List<Double> pcmData = pattern.getPcmData();
				lists.add(pcmData);
			}
		});
		LOGGER.info("Time took read pcm data %d ms",	System.currentTimeMillis() - t2);*/
		return allHealings;
	}

	private void getHealings(Map<Pattern, AnalysisSummary> diseases, final List<ChunkSummary> sample, final Map<String, Integer> probableKinds,
			HashMap<Pattern, AnalysisSummary> allHealings, int level) {
		diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
			@Override
			public void accept(Pattern dk, AnalysisSummary dv) {

				LOGGER.info("heals for %s %s\n", dk.getKind(), dk.getName());

				if (probableKinds.containsKey(dk.getKind())) {

					long t1 = System.currentTimeMillis();

					List<EDXPattern> patternsEn;
					List<EDXPattern> patternsEx;
					try {
                        /*if (level == -2147483648) {
                            patternsEn = null;
                            patternsEx = null;
                        }
                        else {*/
                            patternsEn = patternsDao.getFromDatabase(((EDXPattern) dk).getCorrectingFolderEn());
                            patternsEx = patternsDao.getFromDatabase(((EDXPattern) dk).getCorrectingFolderEx());
//                        }

						LOGGER.info("iterForFolder took %d ms", System.currentTimeMillis() - t1);

						/**
						 * вытягиваются папка с коректорами для конкретной
						 * болезни BAC -> FL BAC
						 */
						final Map<Pattern, AnalysisSummary> healings = Machine.summarizePatterns(sample, patternsEn, level);
						final Map<Pattern, AnalysisSummary> healingsEx = Machine.summarizePatterns(sample, patternsEx, -2147483648);
						LOGGER.info("SummarizePatterns took %d ms", System.currentTimeMillis() - t1);

						allHealings.putAll(healings);
						allHealings.putAll(healingsEx);
						LOGGER.info("Healing size %d patternsEn",allHealings.size());

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
