package com.mps.machine;

import com.mps.analyzer.AnalysisSummary;
import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;

import java.util.*;

public class Machine {
    public static Map<Strain, AnalysisSummary> summarizeStrains(
            SummaryCondition         condition,
            Collection<ChunkSummary> sample_summary,
            StrainDB                 strains) {
        final Map<Strain, AnalysisSummary> summaries = new HashMap<>();
        while (strains.hasNext()) {
            final Strain strain = strains.next();
            final AnalysisSummary summary =
                Analyzer.compare(sample_summary, strain.getSummary());
            if (condition.test(strain, summary)) {
                summaries.put(strain, summary);
            }
        }
        return summaries;
    }

    public static Map<String, Integer> filterKinds(
            KindCondition condition, Map<Strain, AnalysisSummary> summaries) {
        final Map<String, Integer> counters = new HashMap<>();
        final Map<String, Integer> result   = new HashMap<>();
        summaries.forEach((k, v) ->
            counters.put(k.getKind(),
                counters.getOrDefault(k.getKind(), 0) + 1));
        counters.forEach((k, v) -> {
            if (condition.test(k, v)) {
                result.put(k, v);
            }
        });
        return result;
    }
}
