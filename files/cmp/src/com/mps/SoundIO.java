package com.mps;

import java.io.IOException;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;

public class SoundIO {
    public static double[][] readAllFrames(final AudioInputStream audioStream)
            throws IOException {
        final AudioFormat format = audioStream.getFormat();

        // TODO?: Make it work with more encodings
        if (format.getEncoding() != Encoding.PCM_UNSIGNED) {
            throw new IOException("Bad encoding");
        }

        final long frameLength = audioStream.getFrameLength();
        final long frameSize   = format.getFrameSize();

        final byte[] rawData = new byte[(int)(frameLength * frameSize)];
        audioStream.read(rawData);

        final int channels = format.getChannels();
        final double frameRes = Math.pow(2.0, (double)frameSize * 8.0) / 2.0;
        final boolean isBigEndian = format.isBigEndian();

        final double[][] peaks = new double[channels][(int)frameLength];
        int peak   = 0;
        int rawPtr = 0;
        while (rawPtr < (int)frameLength) {
            for (int chan = 0; chan < channels; chan += 1) {
                // Performance note: highly biased branches are ok
                final long frameData = isBigEndian
                        ? readFrameBE(rawData, rawPtr, frameSize)
                        : readFrameLE(rawData, rawPtr, frameSize);
                rawPtr += frameSize;

                peaks[chan][peak] = (double) (frameData ^ 0x80) / frameRes;
            }
            peak += 1;
        }

        return peaks;
    }

    // Hopefully, jit will inline these methods
    private static long readFrameBE(byte[] rawData,
                                    int  rawPtr,
                                    long frameSize) {
        long frameData = 0;
        for (int i = 0; i < frameSize; i += 1) {
            frameData |= rawData[rawPtr + i] << (frameSize - i - 1)*8;
        }

        return frameData;
    }

    private static long readFrameLE(byte[] rawData,
                                    int  rawPtr,
                                    long frameSize) {
        long frameData = 0;
        for (int i = 0; i < frameSize; i += 1) {
            frameData |= rawData[rawPtr + i] << i*8;
        }

        return frameData;
    }
}
