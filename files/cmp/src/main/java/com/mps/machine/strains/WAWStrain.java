package com.mps.machine.strains;

import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.machine.Strain;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

// Implemented for legacy reasons
public class WAWStrain implements Strain {
    public WAWStrain(String kind, String name, String fileName)
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

    public Collection<ChunkSummary> getSummary() {
        return this.summary;
    }

    public Collection<Double> getPCMData() {
        return null;
    }

    public String getDescription() {
        return "";
    }

    private final String kind;
    private final String name;
    private final Collection<ChunkSummary> summary;
}
