package com.mpsdevelopment.biopotential.server.wave;

import java.io.File;

/**
 * Created by Dmitry on 7/6/2014.
 */
public interface WaveformExtractor {

    double[] extract(File in);
}
