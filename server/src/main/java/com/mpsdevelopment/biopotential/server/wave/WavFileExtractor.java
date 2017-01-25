package com.mpsdevelopment.biopotential.server.wave;

import java.io.File;

public class WavFileExtractor implements WaveformExtractor {

    @Override
    public double[] extract(File in) {
        try {
            double[] result = new double[0];
            WaveFile waveFile = WaveFile.openWavFile(in);

            if(waveFile.getNumFrames() > 66150) {
                result = new double[Integer.valueOf(String.valueOf(66150))];
            }
            else {
                result = new double[Integer.valueOf(String.valueOf(waveFile.getNumFrames()))];
            }
            waveFile.readFrames(result, result.length);

            waveFile.close();

            return result;
        } catch (Exception e) {
            System.out.println("Error reading audio file: " + e.getMessage());
            return new double[0];
        }
    }
}
