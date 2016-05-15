package com.mps.machine;

import com.mps.analyzer.ChunkSummary;

public interface Strain {
    String getKind();
    String getName();
    String getDescription();
    ChunkSummary[] getSummary();
}
