package com.mpsdevelopment.biopotential.server.eventbus.event;


import com.mpsdevelopment.biopotential.server.db.pojo.CorrectorTable;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.eventbus.Event;

import java.util.Set;

public class SelectCorrectorEvent extends Event {

    private Set<DataTable> map;

    public SelectCorrectorEvent(Set<DataTable> map) {
        this.map = map;
    }

    public Set<DataTable> getMap() {
        return map;
    }

    public void setMap(Set<DataTable> map) {
        this.map = map;
    }
}
