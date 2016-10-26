package com.mpsdevelopment.biopotential.server.cmp.machine;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;

class EDXSection {
	String name;
	int offset;
	int length;
	byte[] contents;
}

public class Machine {

	public static Map<Pattern, AnalysisSummary> summarizePatterns(List<ChunkSummary> sampleSummary, List<EDXPattern> patterns) {
		final Map<Pattern, AnalysisSummary> summaries = new HashMap<>();
		AnalysisSummary summary;
		for (Pattern pattern : patterns) {
			summary = Analyzer.compare(sampleSummary, pattern.getSummary());
			if (summary != null && summary.getDegree() == 0) {
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
		counters.forEach((k, v) -> {
			if (condition.test(k, v)) {
				result.put(k, v);
			}
		});
		return result;
	}

	public static PcmDataSummary getPcmData(String fileName) throws IOException {

		HashMap<String, EDXSection> sects = new HashMap<>();
		List<ChunkSummary> summary = new ArrayList<>();
		List<Double> pcmData = new ArrayList<>();

		try (RandomAccessFile in = new RandomAccessFile(new File(fileName), "r")) {
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
				pcmData.add((double) (byte) (b ^ 0x80) / 128.0);
			}
			summary = Analyzer.summarize(pcmData);
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
