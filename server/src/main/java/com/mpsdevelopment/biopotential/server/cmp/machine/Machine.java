package com.mpsdevelopment.biopotential.server.cmp.machine;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

class EDXSection {
	String name;
	int offset;
	int length;
	byte[] contents;
}

public class Machine {

	private static String edxFileFolder;

    public static void setEdxFileFolder(String edxFileFolder) {
        Machine.edxFileFolder = edxFileFolder;
    }

    private static final Logger LOGGER = LoggerUtil.getLogger(Machine.class);

	private static final String EDX_FILE_FOLDER = "data/edxfiles/";
//	private static final String EDX_FILE_FOLDER = "D:/MPS/IDEA/Biopotential material's/база автомат/my_super_puper_db_Storage/";

	public static Map<Pattern, AnalysisSummary> summarizePatterns(List<ChunkSummary> sampleSummary, List<EDXPattern> patterns, int degree) {
		final Map<Pattern, AnalysisSummary> summaries = new HashMap<>();
		AnalysisSummary summary;
		for (Pattern pattern : patterns) {
//			long t1 = System.currentTimeMillis();
			summary = Analyzer.compare(sampleSummary, pattern.getSummary());
//			LOGGER.info("Operation compare took %d ms", System.currentTimeMillis() - t1);
//            LOGGER.info("summary getDegree %s",summary.getDegree());
            if (summary != null && summary.getDegree() == degree) {
				summaries.put(pattern, summary);
			}
		}
		return summaries;
	}

	public static Map<String, Integer> filterKinds(KindCondition condition, Map<Pattern, AnalysisSummary> summaries) {
		final Map<String, Integer> counters = new HashMap<>();
		final Map<String, Integer> result = new HashMap<>();
		summaries.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
			@Override
			public void accept(Pattern k, AnalysisSummary v) {
				counters.put(k.getKind(), counters.getOrDefault(k.getKind(), 0) + 1);
			}
		});
		counters.forEach(new BiConsumer<String, Integer>() {
			@Override
			public void accept(String k, Integer v) {
				if (condition.test(k, v)) {
					result.put(k, v);
				}
			}
		});
		return result;
	}

	public static PcmDataSummary getPcmData(String fileName) throws IOException {

		HashMap<String, EDXSection> sects = new HashMap<>();
		List<ChunkSummary> summary;
		List<Float> pcmData;

        if (edxFileFolder == null) {
            edxFileFolder = EDX_FILE_FOLDER;
        }

		try (RandomAccessFile in = new RandomAccessFile(new File(edxFileFolder + fileName), "r")) {
			final byte[] hdr = new byte[4];
			if (in.read(hdr) != 4 || !new String(hdr).equals("EDXI")) {
				throw new IOException("not EDX");
			}
			// version number + .offs string
			if (in.skipBytes(4 + 8) != (4 + 8)) {
				throw new IOException("not EDX");
			}

			final int len = readi32le(in);
			final int count = len / 16; // 16 = name[8] + offs + len
			for (int i = 0; i < count; i += 1) {
				final EDXSection sect = new EDXSection();

				final byte[] sect_name = new byte[8];
				in.read(sect_name);

				sect.name = new String(sect_name);
				sect.offset = readi32le(in);
				sect.length = readi32le(in);
				sect.contents = new byte[sect.length];

				final long cur = in.getFilePointer();
				in.seek(sect.offset + 12); // 12 bytes of useless junk
				in.read(sect.contents, 0, sect.contents.length);
				in.seek(cur);

				sects.put(sect.name, sect);
			}
		} catch (IOException e) {
			throw e;
		}

		if (sects.containsKey(".orig   ")) {
			pcmData = new ArrayList<>();
			for (byte b : sects.get(".orig   ").contents) {
				pcmData.add((float) ((float) (byte) (b ^ 0x80) / 128.0));
			}
//			long t1 = System.currentTimeMillis();
//			summary = Analyzer.summarize(pcmData);
			summary = null;
//			LOGGER.info("Time for get summurize %d ms", System.currentTimeMillis() - t1);

		} else {
			pcmData = null;
			summary = null;
		}

		return new PcmDataSummary(pcmData, summary);
	}

	private static int readi32le(RandomAccessFile in) {
		try {
			byte[] raw = new byte[4];
			in.read(raw);
			return (raw[0] & 0xff) | ((raw[1] & 0xff) << 8) | ((raw[2] & 0xff) << 16) | ((raw[3] & 0xff) << 24);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
