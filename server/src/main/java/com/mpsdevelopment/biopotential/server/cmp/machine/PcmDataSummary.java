package com.mpsdevelopment.biopotential.server.cmp.machine;

import java.util.List;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;

public class PcmDataSummary {

	/*private final List<ChunkSummary> summary;
	private final List<Float> pcmData;

	public PcmDataSummary(List<Float> pcmData, List<ChunkSummary> summary) {
		this.pcmData = pcmData;
		this.summary = summary;
	}

	public List<ChunkSummary> getSummary() {
		return summary;
	}

	public List<Float> getPcmData() {
		return pcmData;
	}*/

	private final List<ChunkSummary> summary;
	private final /*List<Float>*/float[] pcmData;

	public PcmDataSummary(/*List<Float>*/float[] pcmData, List<ChunkSummary> summary) {
		this.pcmData = pcmData;
		this.summary = summary;
	}

	public List<ChunkSummary> getSummary() {
		return summary;
	}

	public /*List<Float>*/float[] getPcmData() {
		return pcmData;
	}

}
