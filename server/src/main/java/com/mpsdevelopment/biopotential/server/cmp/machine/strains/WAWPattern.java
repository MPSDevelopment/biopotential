package com.mpsdevelopment.biopotential.server.cmp.machine.strains;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

// Implemented for legacy reasons
public class WAWPattern implements Pattern {
    public WAWPattern(String kind, String name, String fileName)
            throws IOException {
        this.kind = kind;
        this.name = name;
        this.summary = Analyzer.readSummaryFromWAW(
                new FileInputStream(fileName));
    }

    public String getKind() {
        return this.kind;
    }

    public String getName() {
        return this.name;
    }

    public List<ChunkSummary> getSummary() {
        return this.summary;
    }

    public String getFileName() {
        return null;
    }

    public void setName(String str) {

    }

    public List<Double> getPcmData() {
        return null;
    }

    public String getDescription() {
        return "";
    }

    private final String kind;
    private final String name;
    private final List<ChunkSummary> summary;
}
