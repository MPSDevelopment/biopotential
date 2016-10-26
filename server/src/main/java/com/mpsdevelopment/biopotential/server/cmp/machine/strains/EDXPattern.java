package com.mpsdevelopment.biopotential.server.cmp.machine.strains;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;

import java.io.*;
import java.util.List;

public class EDXPattern implements Pattern {

	private List<ChunkSummary> summary;
	private List<Double> pcmData;
	final private String correctingFolder;
	final private String kind;
	final private String name;
	final private String desc;
	final private String fileName;

	public EDXPattern(String kind, String name, String desc, String fileName) throws IOException {
		this(kind, name, desc, fileName, null);
	}

	public EDXPattern(String kind, String name, String desc, String fileName, String correctingFolder) throws IOException {
		this.kind = kind;
		this.name = name;
		this.desc = desc;
		this.correctingFolder = correctingFolder;
		this.fileName = fileName;

		// this.pcmData = Machine.getPcmData(fileName).getPcmData();
		// this.summary = Machine.getPcmData(fileName).getSummary();
	}

	public boolean hasCorrectingFolder() {
		return this.correctingFolder != null;
	}

	public String getCorrectingFolder() {
		return this.correctingFolder;
	}

	public String getKind() {
		return this.kind;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.desc;
	}

	public List<Double> getPCMData() {
		if (this.pcmData == null) {
			try {
				this.pcmData = Machine.getPcmData(fileName).getPcmData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.pcmData;
	}

	public List<ChunkSummary> getSummary() {
		if (this.summary == null) {
			try {
				this.summary = Machine.getPcmData(fileName).getSummary();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.summary;
	}

	public String getFileName() {
		return fileName;
	}

}
