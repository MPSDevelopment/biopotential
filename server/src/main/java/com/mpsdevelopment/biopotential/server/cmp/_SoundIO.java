package com.mpsdevelopment.biopotential.server.cmp;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED;

public class _SoundIO {

    static class EDXSection {
        String name;
        int offset;
        int length;
        byte[] contents;
    }

    // TODO: REPLACEME
//    public static List<Double> readAllFrames()
//            throws IOException {
//        return readAllFrames();
//    }

    public static List<Double> readAllFrames(final AudioInputStream audioStream)
            throws IOException {
        final AudioFormat format = audioStream.getFormat();

        if (format.getEncoding() != PCM_UNSIGNED) { // если кодировка ИКМ беззнаковая
            throw new IOException("Bad encoding");
        }
        if (format.getChannels() != 1) { // если не 1 канал
            throw new IOException("Bad number of channels");
        }
        if (format.getSampleSizeInBits() != 8) { // Величина амплитуды в момент оцифровки
            throw new IOException("Bad sample rate");
        }

        final long frameLength = audioStream.getFrameLength();
        final long frameSize = format.getFrameSize();

        final byte[] rawData = new byte[(int) (frameLength * frameSize)];
        audioStream.read(rawData); // получает целые знаковые числа, так как мы получаем signed data то если число 9F это отрицательное то и десятичное отрицательное

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
            peaks.add((double) (byte) (rawData[i] ^ 0x80) / 128.0); // усреднение.. привести все к амплитуде 1 + делает инверсию
                                                                    // (byte) (rawData[i] ^ 0x80) приводит знаковый byte к беззнаковому
        }

        return peaks;
    }

    public static List<ChunkSummary> getPcmData(String fileName) throws IOException {

        HashMap<String, EDXSection> sects = new HashMap<>();
        List<ChunkSummary> summary;
        List<Double> pcmData;

        try (RandomAccessFile in = new RandomAccessFile(new File(fileName), "r")) {

                final EDXSection sect = new EDXSection();

                final byte[] sect_name = new byte[8];
                in.read(sect_name);

                sect.name = new String(".data");
//                sect.offset = readi32le(in);
//                sect.length = readi32le(in);
                sect.contents = new byte[(int) (in.length() - 44)];

//                final long cur = in.getFilePointer();
                in.seek(0x2C); // 12 bytes of useless junk
                in.read(sect.contents, 0, sect.contents.length);
//                in.seek(cur);

                sects.put(sect.name, sect);

        } catch (IOException e) {
            throw e;
        }


            pcmData = new ArrayList<>();
            for (byte b : sects.get(".data").contents) {
                pcmData.add((double) (byte) (b ^ 0x80) / 128.0);
            }

        /*for (int i = 0; i < sects.get(".data").contents.length-2; i=i+2) {
            if (i == 467) {
                System.out.println();
            }
            pcmData.add((double)(short)((sects.get(".data").contents[i] & 0x00FF)
                    | (short)(sects.get(".data").contents[i+1] << 8) & 0xFFFF)/32768.0);
        }*/
            summary = Analyzer.summarize(pcmData);



        return summary;
    }

    public static double[] extractACT(String fileName) throws IOException {
        HashMap<String, EDXSection> sects = new HashMap<>();
        List<ChunkSummary> summary;
        List<Double> pcmData;

        try (RandomAccessFile in = new RandomAccessFile(new File(fileName), "r")) {

            final EDXSection sect = new EDXSection();

            final byte[] sect_name = new byte[8];
            in.read(sect_name);

            sect.name = new String(".data");
            sect.contents = new byte[(int) (in.length() - 44)];

            in.seek(0x2C); // 12 bytes of useless junk
            in.read(sect.contents, 0, sect.contents.length);

            sects.put(sect.name, sect);

        } catch (IOException e) {
            throw e;
        }


        pcmData = new ArrayList<>();

        for (int i = 0; i < sects.get(".data").contents.length-2; i=i+2) {
            if (i == 467) {
                System.out.println();
            }
            pcmData.add((double)(short)((sects.get(".data").contents[i] & 0x00FF)
                    | (short)(sects.get(".data").contents[i+1] << 8) & 0xFFFF)/32768.0);
        }
        double[] array = new double[pcmData.size()];
//        pcmData.toArray(array);

        for (int i = 0; i < pcmData.size(); i++) {
            array[i] = pcmData.get(i);
        }

        return array;
    }

    public static void writeFramesAsWave(final FileOutputStream outstream,
                                         final List<Double> frames)
            throws IOException {
        outstream.write("RIFF".getBytes());
        outstream.write(int2le(4, 36 + frames.size())); // Container size minus first 8 bytes
        outstream.write("WAVE".getBytes());
        outstream.write("fmt ".getBytes());
        outstream.write(int2le(4, 10)); // Size of WAVE section
        outstream.write(int2le(2, 1)); // Use PCM_UNSIGNED
        outstream.write(int2le(2, 1)); // Mono
        outstream.write(int2le(4, 22050)); // Quantization freq
        outstream.write(int2le(4, 22050)); // Bytes/s
        outstream.write(int2le(2, 1)); // Block alignment
        outstream.write(int2le(2, 8)); // Bits/sample
        outstream.write("data".getBytes()); // Bits/sample
        outstream.write(int2le(4, frames.size()));
        for (Double v : frames) {
            // if (v > 1.0 || v < 0.0) {
            //     throw ...
            // }
            outstream.write((byte) (v * 128.0) ^ 0x80);
        }
    }

    private static byte[] int2le(int size, int value) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i += 1) {
            result[i] = (byte) (value << i*8);
        }
        return result;
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
