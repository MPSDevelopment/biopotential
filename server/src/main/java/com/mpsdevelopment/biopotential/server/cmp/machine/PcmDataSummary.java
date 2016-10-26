package com.mpsdevelopment.biopotential.server.cmp.machine;

import java.util.List;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;

public class PcmDataSummary {

	private final List<ChunkSummary> summary;
	private final List<Double> pcmData;

	public PcmDataSummary(List<Double> pcmData, List<ChunkSummary> summary) {
		this.pcmData = pcmData;
		this.summary = summary;
	}

	public List<ChunkSummary> getSummary() {
		return summary;
	}

	public List<Double> getPcmData() {
		return pcmData;
	}

}
