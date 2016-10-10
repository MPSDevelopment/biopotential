package com.mpsdevelopment.biopotential.server.cmp.machine;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;

import java.util.*;
import java.util.function.BiConsumer;

public class Machine {
    private static Pattern pattern;

    public static Map<Pattern, AnalysisSummary> summarizePatterns(SummaryCondition condition, List<ChunkSummary> sampleSummary, PatternDB patterns) {
        final Map<Pattern, AnalysisSummary> summaries = new HashMap<>();
        while ((pattern = patterns.next()) != null) {
            final AnalysisSummary summary = Analyzer.compare(sampleSummary, pattern.getSummary());
            if (summary != null && condition.test(pattern, summary)) {
                summaries.put(pattern, summary);
            }
        }
        return summaries;
    }

    public static Map<String, Integer> filterKinds(KindCondition condition, Map<Pattern, AnalysisSummary> summaries) {
        final Map<String, Integer> counters = new HashMap<>();
        final Map<String, Integer> result = new HashMap<>();
        summaries.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern k, AnalysisSummary v) {
                counters.put(k.getKind(), counters.getOrDefault(k.getKind(), 0) + 1);
            }
        });
        counters.forEach((k, v) -> {
            if (condition.test(k, v)) {
                result.put(k, v);
            }
        });
        return result;
    }
}
