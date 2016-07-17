package com.mps.machine;

import com.mps.analyzer.ChunkSummary;

import java.util.Collection;

public interface Strain {
    String getKind();
    String getName();
    String getDescription();
    Collection<Double> getPCMData();
    Collection<ChunkSummary> getSummary();
}
