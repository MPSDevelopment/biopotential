package com.mpsdevelopment.biopotential.server.cmp.machine.strains;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.PcmDataSummary;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EDXPattern implements Pattern {
	@Expose
	private List<ChunkSummary> summary;
	@Expose
	private List<Double> pcmData;
	/*@Expose
	private Long correctingFolderEn;*/
	@Expose
	private Long correctingFolderEn;
    @Expose
    private Long correctingFolderEx;
	@Expose
	private String kind;
	@Expose
	private String name;
	@Expose
	private String description;
	@Expose
	private String fileName;

    public EDXPattern() {
    }

	public EDXPattern(String kind, String name, String desc, String fileName) throws IOException {
		this(kind, name, desc, fileName, null, null);
	}

	public EDXPattern(String kind, String name, String description, String fileName, Long correctingFolderEn, Long correctingFolderEx) throws IOException {
		this.kind = kind;
		this.name = name;
		this.description = description;
		this.correctingFolderEn = correctingFolderEn;
		this.correctingFolderEx = correctingFolderEx;
		this.fileName = fileName;

		// this.pcmData = Machine.getPcmData(fileName).getPcmData();
		// this.summary = Machine.getPcmData(fileName).getSummary();
	}
	
	private void initializePcmData() {
		try {
			PcmDataSummary pcmData = Machine.getPcmData(fileName);
			this.summary = pcmData.getSummary();
			this.pcmData = pcmData.getPcmData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    // getSummary() for calculate chunk summary values
	/*public List<ChunkSummary> getSummary() {
		if (this.summary == null) {
			initializePcmData();
		}
		return this.summary;
	}*/

	// Override getSummary() for use summaries() method with pre-calculated values
	@Override
	public List<ChunkSummary> getSummary() {
		return summary;
	}
	public List<Double> getPcmData() {
		if (this.pcmData == null) {
			initializePcmData();
		}
		return this.pcmData;
	}

	public Long getCorrectingFolderEn() {
		return correctingFolderEn;
	}

	public void setCorrectingFolderEn(Long correctingFolderEn) {
		this.correctingFolderEn = correctingFolderEn;
	}

    public Long getCorrectingFolderEx() {
        return correctingFolderEx;
    }

    public void setCorrectingFolderEx(Long correctingFolderEx) {
        this.correctingFolderEx = correctingFolderEx;
    }

    public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setSummary(String json) {
        Type listType = new TypeToken<ArrayList<ChunkSummary>>(){}.getType();
		this.summary = JsonUtils.fromJson(listType,json);;
	}

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EDXPattern that = (EDXPattern) o;

		return fileName.equals(that.fileName);

	}

	@Override
	public int hashCode() {
		return fileName.hashCode();
	}

	/*public void setSummary(List<ChunkSummary> summary) {
        this.summary = summary;
    }*/

    //
	// public boolean hasCorrectingFolder() {
	// return this.correctingFolderEn != null;
	// }
	//
	// public String getCorrectingFolderEn() {
	// return this.correctingFolderEn;
	// }
	//
	// public String getKind() {
	// return this.kind;
	// }
	//
	// public String getName() {
	// return this.name;
	// }
	//
	// public String getDescription() {
	// return this.desc;
	// }
	//
	// public List<Double> getPCMData() {
	// if (this.pcmData == null) {
	// try {
	// this.pcmData = Machine.getPcmData(fileName).getPcmData();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return this.pcmData;
	// }
	//
	// public List<ChunkSummary> getSummary() {
	// if (this.summary == null) {
	// try {
	// this.summary = Machine.getPcmData(fileName).getSummary();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return this.summary;
	// }
	//
	// public String getFileName() {
	// return fileName;
	// }
	//
	// public String getDesc() {
	// return desc;
	// }
	//
	// public void setDesc(String desc) {
	// this.desc = desc;
	// }
	//
	// public void setCorrectingFolderEn(String correctingFolderEn) {
	// this.correctingFolderEn = correctingFolderEn;
	// }
	//
	// public void setName(String name) {
	// this.name = name;
	// }
	//
	// public void setFileName(String fileName) {
	// this.fileName = fileName;
	// }

}
