package com.mpsdevelopment.biopotential.server.cmp.machine;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;

import java.util.List;

public interface Pattern {
    String getKind();
    String getName();
    String getDescription();
    double[] getPcmData();
    List<ChunkSummary> getSummary();
    String getFileName();
    void setName(String str);
}
