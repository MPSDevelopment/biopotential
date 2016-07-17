package com.mps.machine;

import com.mps.analyzer.AnalysisSummary;

public interface SummaryCondition {
    boolean test(Strain strain, AnalysisSummary summary);
}
