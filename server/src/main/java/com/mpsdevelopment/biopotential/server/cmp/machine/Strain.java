package com.mpsdevelopment.biopotential.server.cmp.machine;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;

import java.util.List;

public interface Strain {
    String getKind();
    String getName();
    String getDescription();
    List<Double> getPCMData();
    List<ChunkSummary> getSummary();
}
