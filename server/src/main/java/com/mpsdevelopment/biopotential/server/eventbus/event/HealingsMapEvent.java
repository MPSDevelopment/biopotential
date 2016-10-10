package com.mpsdevelopment.biopotential.server.eventbus.event;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.eventbus.Event;

import java.util.Map;

public class HealingsMapEvent extends Event {

    private Map<Pattern, AnalysisSummary> map;

    public HealingsMapEvent(Map<Pattern, AnalysisSummary> map) {
        this.map = map;
    }

    public Map<Pattern, AnalysisSummary> getMap() {
        return map;
    }

    public void setMap(Map<Pattern, AnalysisSummary> map) {
        this.map = map;
    }
}
