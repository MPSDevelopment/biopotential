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
import com.sun.media.sound.WaveFileReader;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.sampled.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DiseaseDao {

	private static final Logger LOGGER = LoggerUtil.getLogger(DiseaseDao.class);
    private static File outputFile = new File("data\\out\\conv.wav");

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
//            sample = _SoundIO.getPcmData(file.getAbsolutePath());

            File filetemp = new File("data\\out\\temp");
            extractFile(file.getAbsolutePath(), filetemp);

            final long start = System.currentTimeMillis();
            WaveFileReader reader = new WaveFileReader();

            final AudioInputStream sourceStream = reader.getAudioInputStream(filetemp);
            final AudioFormat sourceFormat = sourceStream.getFormat();
            final AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_UNSIGNED,
                    22050f,
                    8,
                    sourceFormat.getChannels(),
                    1,
                    22050f,
                    sourceFormat.isBigEndian()
            );

            AudioInputStream resampledStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
            AudioSystem.write(resampledStream, AudioFileFormat.Type.WAVE, outputFile);

//            sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(new File("data\\out\\conv.wav"))));
            sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(outputFile)));
        }
		/*
		get all patterns from database which have id correctors folders
		 */
//		List<EDXPattern> patternsNull = patternsDao.getPatternsWhereCorrectorsNotNull(0);
		// TODO Split summarizePatterns to 2 methods for decease and pattern

        switch (fetch) {
            case "corNotNull": {
                List<EDXPattern> patterns = patternsDao.getPatternsWhereCorrectorsNotNull();
                Map<Pattern, AnalysisSummary> diseases = Machine.summarizePatterns(sample, patterns, degree);
                return diseases;
            }

            case "stress": {
                Folder stressAnalys = foldersDao.getByName("Stress Analyze");
                Folder destruction = foldersDao.getByName("Di ????????????????????");
                Folder metabolism = foldersDao.getByName("Me ????????????????????");
                Folder physCond = foldersDao.getByName("B?? ?????? ??????????????");
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
	public Map<Pattern, AnalysisSummary> getHealings(Map<Pattern, AnalysisSummary> diseases, int level) throws IOException, UnsupportedAudioFileException {

		final List<ChunkSummary> sample = Analyzer.summarize(_SoundIO.readAllFrames(AudioSystem.getAudioInputStream(outputFile)));

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

	    List<Folder> folderListEn = foldersDao.getFolders();
	    List<Folder> folderListEx = foldersDao.getFolders();
	    List<Long> longsEn = new ArrayList<>();
	    List<Long> longsEx = new ArrayList<>();

	    folderListEn.forEach(new Consumer<Folder>() {
            @Override
            public void accept(Folder folder) {
                if (folder.getFolderName().contains("en")) {
                    longsEn.add(folder.getId());
                    LOGGER.info("Folder name is %s", folder.getFolderName());
                }
            }
        });

        folderListEx.forEach(new Consumer<Folder>() {
            @Override
            public void accept(Folder folder) {
                if (folder.getFolderName().contains("ex")) {
                    longsEx.add(folder.getId());
                    LOGGER.info("Folder name is %s", folder.getFolderName());
                }
            }
        });

        Set<EDXPattern> edxPatternsEn = new HashSet<>();
        Set<EDXPattern> edxPatternsEx = new HashSet<>();
        long t1 = System.currentTimeMillis();
        LOGGER.info("Start getHealings");

        List<EDXPattern> patternsEn = new ArrayList<>();
        List<EDXPattern> patternsEx = new ArrayList<>();

        try {
            patternsEn = patternsDao.getPatternsFromListFolders(longsEn);
            patternsEx = patternsDao.getPatternsFromListFolders(longsEx);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        LOGGER.info("getPatternsWhereCorrectorsNotNull %d ms",	System.currentTimeMillis() - t1);
        /*patternsEn.forEach(new Consumer<EDXPattern>() {
            @Override
            public void accept(EDXPattern edxPattern) {
                edxPatternsEn.add(edxPattern);
            }
        });

        patternsEx.forEach(new Consumer<EDXPattern>() {
            @Override
            public void accept(EDXPattern edxPattern) {
                edxPatternsEx.add(edxPattern);
            }
        });*/

        LOGGER.info("diseases %d ms",	System.currentTimeMillis() - t1);

       /* diseases.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern dk, AnalysisSummary dv) {

//                LOGGER.info("heals for %s %s\n", dk.getKind(), dk.getName());

                if (probableKinds.containsKey(dk.getKind())) {

					long t1 = System.currentTimeMillis();

					List<EDXPattern> patternsEn;
					List<EDXPattern> patternsEx;
					try {
                        *//*if (level == -2147483648) {
                            patternsEn = null;
                            patternsEx = null;
                        }
                        else {*//*
                            patternsEn = patternsDao.getPatternsWhereCorrectorsNotNull(((EDXPattern) dk).getCorrectingFolderEn());
                            patternsEx = patternsDao.getPatternsWhereCorrectorsNotNull(((EDXPattern) dk).getCorrectingFolderEx());
//                        }

//						LOGGER.info("iterForFolder took %d ms", System.currentTimeMillis() - t1);

						*//**
						 * ???????????????????????? ?????????? ?? ?????????????????????? ?????? ????????????????????
						 * ?????????????? BAC -> FL BAC
						 */

        /*List<EDXPattern> listEn = new ArrayList<>();
        listEn.addAll(edxPatternsEn);

        List<EDXPattern> listEx = new ArrayList<>();
        listEx.addAll(edxPatternsEx);*/

        final Map<Pattern, AnalysisSummary> healings = Machine.summarizePatterns(sample, patternsEn, level);
        final Map<Pattern, AnalysisSummary> healingsEx = Machine.summarizePatterns(sample, patternsEx, -2147483648);
//						LOGGER.info("SummarizePatterns took %d ms", System.currentTimeMillis() - t1);

        allHealings.putAll(healings);
        allHealings.putAll(healingsEx);
//						LOGGER.info("Healing size %d patternsEn",allHealings.size());

        /*} catch (SQLException | IOException e) {
						e.printStackTrace();
					}
				}
			}

		});*/
    }

	private Map<String, Integer> getProbableKinds(Map<Pattern, AnalysisSummary> diseases) {
		return Machine.filterKinds(new KindCondition() {
			@Override
			public boolean test(String kind, int count) {
				return count > 0;
			}
		}, diseases);
	}

    private void extractFile(final String filename, final File file) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
//            in = getClass().getResourceAsStream(filename);
            in = new FileInputStream(filename);
            out = new FileOutputStream(file);
            final byte[] buf = new byte[1024*64];
            int justRead;
            while ((justRead = in.read(buf)) != -1) {
                out.write(buf, 0, justRead);
                System.out.println("justRead " + justRead);
            }
            System.out.println(buf.length);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static byte[] encodePcmToMp3(byte[] pcm) {
        AudioFormat inputFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
                22050,
                8,
                1,
                1,
                22050,
                false);
        LameEncoder encoder = new LameEncoder(inputFormat, 128, MPEGMode.MONO, Lame.QUALITY_HIGHEST, false);

        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

            mp3.write(buffer, 0, bytesWritten);
        }

        encoder.close();
        return mp3.toByteArray();
    }

}
