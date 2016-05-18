package com.mps.machine;

import com.mps.analyzer.AnalysisSummary;

public interface MachineCondition {
    boolean test(AnalysisSummary summary);
}
