package com.mps.sound;

import java.io.IOException;
import java.util.Collection;

public interface SoundInput {
    long getLength();
    Collection<double[]> readFrames(int n) throws IOException;
}
