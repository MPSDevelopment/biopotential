package vis;

import java.io.IOException;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

// TODO: Replace with something better
public class Vis {
    public static double[][] getWaveForm(AudioInputStream audioStream) throws IOException {
        // TODO?: Make it work with any encoding
        if (audioStream.getFormat().getEncoding() != Encoding.PCM_UNSIGNED) {
            throw new IOException("Bad sample rate");
        }

        // TODO?: Make it work with any number of channels
        if (audioStream.getFormat().getChannels() != 1) {
            throw new IOException("Bad number of channels");
        }

        // TODO?: Make it work with any sample rate
        if (audioStream.getFormat().getSampleSizeInBits() != 8) {
            throw new IOException("Bad sample rate");
        }

        long frameLength = audioStream.getFrameLength();
        long frameSize   = audioStream.getFormat().getFrameSize();

        byte[] rawData = new byte[(int)(frameLength * frameSize)];
        audioStream.read(rawData);

        // FIXME: This only works with 8 bit samples
        double[][] peaks = new double[1][(int)frameLength];
        int peak = 0;

        for (int frame = 0; frame < (int)frameLength; frame += 1) {
            peaks[0][peak] = rawData[frame];
            peak += 1;
        }

        return peaks;
    }
}
