package com.mpsdevelopment.biopotential.server.eventbus.event;


import com.mpsdevelopment.biopotential.server.eventbus.Event;

import java.io.File;

public class FileChooserEvent extends Event {

    private File file;

    public FileChooserEvent(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

}
