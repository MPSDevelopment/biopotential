package com.mps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;

public class _SoundIO {
    public static Collection<Double> readAllFrames()
            throws IOException {
        return readAllFrames();
    }

    public static Collection<Double> readAllFrames(final AudioInputStream audioStream)
            throws IOException {
        final AudioFormat format = audioStream.getFormat();

        if (format.getEncoding() != Encoding.PCM_UNSIGNED) {
            throw new IOException("Bad encoding");
        }
        if (format.getChannels() != 1) {
            throw new IOException("Bad number of channels");
        }
        if (format.getSampleSizeInBits() != 8) {
            throw new IOException("Bad sample rate");
        }

        final long frameLength = audioStream.getFrameLength();
        final long frameSize = format.getFrameSize();

        final byte[] rawData = new byte[(int) (frameLength * frameSize)];
        audioStream.read(rawData);

//        final int channels = format.getChannels();
//        final double frameRes = Math.pow(2.0, (double) frameSize * 8.0);
//        final boolean isBigEndian = format.isBigEndian();

        //final double[][] peaks = new double[(int) frameLength];
        List<Double> peaks = new ArrayList<>();
        for (int i = 0; i < (int) frameLength; i += 1) {
            // Performance note: highly biased branches are ok
//          final long frameData = isBigEndian
//              ? readFrameBE(rawData, rawPtr, frameSize)
//              : readFrameLE(rawData, rawPtr, frameSize);
            //peaks[0][i] = (double) (byte) (rawData[i] ^ 0x80) / 128.0;
            peaks.add((double) (byte) (rawData[i] ^ 0x80) / 128.0);
        }

        return peaks;
    }

//    private static long readFrameBE(byte[] rawData,
//                                    int rawPtr,
//                                    long frameSize) {
//        long frameData = 0;
//        for (int i = 0; i < frameSize; i += 1) {
//            // Gotta make horrible hacks because Java doesn't support unsigned
//            frameData |= ((long) rawData[rawPtr + i] & 0xff)
//                    << (frameSize - i - 1) * 8;
//        }
//        return frameData ^ (0x80 << (frameSize - 1)*8);
//    }
//
//    private static long readFrameLE(byte[] rawData,
//                                    int rawPtr,
//                                    long frameSize) {
//        long frameData = 0;
//        for (int i = 0; i < frameSize; i += 1) {
//            frameData |= ((long) rawData[rawPtr + i] & 0xff) << i * 8;
//        }
//        return frameData ^ (0x80);
//    }
}
