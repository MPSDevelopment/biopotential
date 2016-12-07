package com.mpsdevelopment.biopotential.server.eventbus.event;


import com.mpsdevelopment.biopotential.server.eventbus.Event;

import java.io.File;

public class ProgressBarEvent extends Event {

    private double progress;

    public ProgressBarEvent(double progress) {
        this.progress = progress;
    }

    public double getProgress() {
        return progress;
    }
}
