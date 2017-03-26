package com.mpsdevelopment.biopotential.server.eventbus.event;


import com.mpsdevelopment.biopotential.server.eventbus.Event;

public class EnableButtonEvent extends Event {

    private boolean progress;
    private String timeOfConvert;

    public EnableButtonEvent(boolean progress, String timeOfConvert) {
        this.progress = progress;
        this.timeOfConvert = timeOfConvert;
    }

    public boolean getProgress() {
        return progress;
    }

    public String getTimeOfConvert() {
        return timeOfConvert;
    }
}
