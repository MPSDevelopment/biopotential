package com.mps.machine;

import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;

import java.io.FileInputStream;
import java.io.IOException;

// Implemented for legacy reasons
public class WAWStrain implements Strain {
    public WAWStrain(String kind, String name, String fileName)
            throws IOException {
        this.kind = kind;
        this.name = name;
        this.summary = Analyzer.readSummaryFromStream(
                new FileInputStream(fileName));
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public ChunkSummary[] getSummary() {
        return summary;
    }

    public String getDescription() {
        return "";
    }

    private final String kind;
    private final String name;
    private final ChunkSummary[] summary;
}
