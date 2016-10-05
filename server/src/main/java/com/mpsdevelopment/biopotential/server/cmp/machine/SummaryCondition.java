package com.mpsdevelopment.biopotential.server.cmp.machine;

import com.mps.analyzer.AnalysisSummary;
import com.mps.machine.*;

public interface SummaryCondition {
    boolean test(com.mps.machine.Strain strain, AnalysisSummary summary);
}
