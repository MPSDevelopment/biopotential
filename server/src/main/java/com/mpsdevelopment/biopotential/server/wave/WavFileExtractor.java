package com.mpsdevelopment.biopotential.server.wave;

import java.io.File;

public class WavFileExtractor implements WaveformExtractor {

    @Override
    public double[] extract(File in) {
        try {
            WaveFile waveFile = WaveFile.openWavFile(in);

            double[] result = new double[Integer.valueOf(String.valueOf(waveFile.getNumFrames()))];
            waveFile.readFrames(result, result.length);

            waveFile.close();

            return result;
        } catch (Exception e) {
            System.out.println("Error reading audio file: " + e.getMessage());
            return new double[0];
        }
    }
}
