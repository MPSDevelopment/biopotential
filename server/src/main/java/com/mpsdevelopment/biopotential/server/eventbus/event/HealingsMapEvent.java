package com.mpsdevelopment.biopotential.server.eventbus.event;


import com.mps.analyzer.AnalysisSummary;
import com.mps.machine.Strain;
import com.mpsdevelopment.biopotential.server.eventbus.Event;

import java.io.File;
import java.util.Map;

public class HealingsMapEvent extends Event {

    private Map<Strain, AnalysisSummary> map;

    public HealingsMapEvent(Map<Strain, AnalysisSummary> map) {
        this.map = map;
    }

    public Map<Strain, AnalysisSummary> getMap() {
        return map;
    }

    public void setMap(Map<Strain, AnalysisSummary> map) {
        this.map = map;
    }
}
