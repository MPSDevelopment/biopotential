package com.mpsdevelopment.biopotential.server.cmp.sound;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class WaveInputStream extends AudioInputStream implements SoundInput {
    public WaveInputStream(URL url)
            throws IOException, UnsupportedAudioFileException {
        this(new BufferedInputStream(url.openStream()));
    }

    public WaveInputStream(File file)
            throws IOException, UnsupportedAudioFileException {
        this(new BufferedInputStream(new FileInputStream(file)));
    }

    public WaveInputStream(InputStream stream)
            throws IOException, UnsupportedAudioFileException {
        super(stream,
            AudioSystem.getAudioFileFormat(stream).getFormat(),
            AudioSystem.getAudioFileFormat(stream).getFrameLength());

        // TODO: Add support for more
        if (getFormat().getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED) {
            throw new IOException("Bad encoding");
        }
        if (getFormat().getChannels() != 1) {
            throw new IOException("Bad number of channels");
        }
        if (getFormat().getFrameSize() != 1) {
            throw new IOException("Bad frame size");
        }
    }

    public long getLength() {
        return this.getFrameLength()
            / this.getFormat().getFrameSize()
            / this.getFormat().getChannels();
    }

    public synchronized Collection<double[]> readFrames(int n)
            throws IOException {
        final Collection<double[]> frames = new ArrayList<>();
        // FIXME: Only works with 1 byte frames
        final byte rawBuf[] = new byte[n];
        if (read(rawBuf) == rawBuf.length) {
            for (int i = 0; i < rawBuf.length; i += 1) {
                double[] frameBundle = new double[1];
                frameBundle[0] = (double) rawBuf[i] / 128.0;
                frames.add(frameBundle);
            }
        } else {
            throw new IOException("readLen != rawBuf.length");
        }
        return frames;
    }
}
