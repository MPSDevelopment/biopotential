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

	public Map<Pattern, AnalysisSummary> getDeseases(File file, int degree, String fetch, String gender) throws IOException, UnsupportedAudioFileException, SQLException {
        final List<ChunkSummary> sample;
        if (file.getName().contains(".wav")) {
            sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(file)));
        }
        else {
            sample = _SoundIO.getPcmData(file.getAbsolutePath());
        }
		/*
		get all patterns from database which have id correctors folders
		 */
//		List<EDXPattern> patternsNull = patternsDao.getFromDatabase(0);
		// TODO Split summarizePatterns to 2 methods for decease and pattern

        switch (fetch) {
            case "corNotNull": {
                List<EDXPattern> patterns = patternsDao.getFromDatabase();
                Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(sample, patterns, degree);
                return diseases;
            }

            case "stress": {

                Folder stressAnalys = foldersDao.getByName("Stress Analyze");
                Folder destruction = foldersDao.getByName("Di Деструкция");
                Folder metabolism = foldersDao.getByName("Me Метаболизм");
                Folder physCond = foldersDao.getByName("Bо Физ кодиции");
                Folder detokc = foldersDao.getByName("Dt DETOKC");

                List<EDXPattern> patternsStressAnalys = patternsDao.getPatternsFromFoldersCorIsNull(stressAnalys);
                List<EDXPattern> patternsDestruction = patternsDao.getPatternsFromFoldersCorIsNull(destruction);
                List<EDXPattern> patternsMetabolism = patternsDao.getPatternsFromFoldersCorIsNull(metabolism);
                List<EDXPattern> patternsPhysCond = patternsDao.getPatternsFromFoldersCorNotNull(physCond);
                List<EDXPattern> patternsDetokc = patternsDao.getPatternsFromFoldersCorNotNull(detokc);

                Map<Pattern, AnalysisSummary> diseasesStressAnalys = Machine.summarizePatterns(sample, patternsStressAnalys, degree);
                Map<Pattern, AnalysisSummary> diseasesDestruction = Machine.summarizePatterns(sample, patternsDestruction, degree);
                Map<Pattern, AnalysisSummary> diseasesMetabolism = Machine.summarizePatterns(sample, patternsMetabolism, degree);
                Map<Pattern, AnalysisSummary> diseasesPhysCond = Machine.summarizePatterns(sample, patternsPhysCond, degree);
                Map<Pattern, AnalysisSummary> diseasesDetokc = Machine.summarizePatterns(sample, patternsDetokc, degree);

                Map<Pattern, AnalysisSummary> diseases = new HashMap<>();
                diseases.putAll(diseasesStressAnalys);
                diseases.putAll(diseasesDestruction);
                diseases.putAll(diseasesMetabolism);
                diseases.putAll(diseasesPhysCond);
                diseases.putAll(diseasesDetokc);
                return diseases;
            }

            case "hidden": {

                Folder acariasis = foldersDao.getByName("FL Ac Acariasis");
                Folder bacteria = foldersDao.getByName("FL Ba Bacteria");
                Folder elementary = foldersDao.getByName("FL El Elementary");
                Folder helminths = foldersDao.getByName("FL He Helminths");
                Folder mycosis = foldersDao.getByName("FL My Mycosis");
                Folder virus = foldersDao.getByName("FL Vi Virus");
                Folder femely = null;
                Folder man = null;
                if (gender.equals("Man")) {
                    man = foldersDao.getByName("Ma Man");
                }
                else if (gender.equals("Woman")) {
                    femely = foldersDao.getByName("Fe Femely");
                }

                List<EDXPattern> patternsAcariasis = patternsDao.getPatternsFromFoldersCorNotNull(acariasis);
                List<EDXPattern> patternsBacteria = patternsDao.getPatternsFromFoldersCorNotNull(bacteria);
                List<EDXPattern> patternsElementary = patternsDao.getPatternsFromFoldersCorNotNull(elementary);
                List<EDXPattern> patternsHelminths = patternsDao.getPatternsFromFoldersCorNotNull(helminths);
                List<EDXPattern> patternsMycosis = patternsDao.getPatternsFromFoldersCorNotNull(mycosis);
                List<EDXPattern> patternsVirus = patternsDao.getPatternsFromFoldersCorNotNull(virus);
                List<EDXPattern> patternsFemely = patternsDao.getPatternsFromFoldersCorNotNull(femely);
                List<EDXPattern> patternsMan = patternsDao.getPatternsFromFoldersCorNotNull(man);

                Map<Pattern, AnalysisSummary> diseasesAcariasis = Machine.summarizePatterns(sample, patternsAcariasis, degree);
                Map<Pattern, AnalysisSummary> diseasesBacteria = Machine.summarizePatterns(sample, patternsBacteria, degree);
                Map<Pattern, AnalysisSummary> diseasesElementary = Machine.summarizePatterns(sample, patternsElementary, degree);
                Map<Pattern, AnalysisSummary> diseasesHelminths = Machine.summarizePatterns(sample, patternsHelminths, degree);
                Map<Pattern, AnalysisSummary> diseasesMycosis = Machine.summarizePatterns(sample, patternsMycosis, degree);
                Map<Pattern, AnalysisSummary> diseasesVirus = Machine.summarizePatterns(sample, patternsVirus, degree);
                Map<Pattern, AnalysisSummary> diseasesFemely = Machine.summarizePatterns(sample, patternsFemely, degree);
                Map<Pattern, AnalysisSummary> diseasesMan = Machine.summarizePatterns(sample, patternsMan, degree);

                Map<Pattern, AnalysisSummary> diseases = new HashMap<>();
                diseases.putAll(diseasesAcariasis);
                diseases.putAll(diseasesBacteria);
                diseases.putAll(diseasesElementary);
                diseases.putAll(diseasesHelminths);
                diseases.putAll(diseasesMycosis);
                diseases.putAll(diseasesVirus);
                diseases.putAll(diseasesFemely);
                diseases.putAll(diseasesMan);
                return diseases;
            }
            default: return null;

        }

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
