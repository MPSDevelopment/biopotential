package com.mpsdevelopment.biopotential.server.cmp.sound;

import java.util.Collection;

public interface SoundOutput {
    void writeFrames(Collection<double[]> frames);
}
