package com.mpsdevelopment.biopotential.server.cmp.machine;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;

public interface SummaryCondition {
    boolean test(Strain strain, AnalysisSummary summary);
}
