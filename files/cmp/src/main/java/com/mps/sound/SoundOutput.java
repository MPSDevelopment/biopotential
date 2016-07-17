package com.mps.sound;

import java.util.Collection;

public interface SoundOutput {
    void writeFrames(Collection<double[]> frames);
}
