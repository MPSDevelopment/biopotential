package com.mpsdevelopment.biopotential.server.eventbus.event;


import com.mpsdevelopment.biopotential.server.eventbus.Event;

public class EnableButtonEvent extends Event {

    private boolean progress;

    public EnableButtonEvent(boolean progress) {
        this.progress = progress;
    }

    public boolean getProgress() {
        return progress;
    }
}
