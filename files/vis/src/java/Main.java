package vis;

import javafx.application.Application;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.imageio.ImageIO;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;


public class Main extends Application {
    private BufferedImage plotGraph(final double[] peaks, Viewport view) {
        // TODO: Test with multichannel audio, various sample rates, LE, BE

        final BufferedImage graph = new BufferedImage(
                view.getTargetW(),
                view.getTargetH(),
                BufferedImage.TYPE_BYTE_INDEXED);

        final Graphics2D gfx = graph.createGraphics();
        gfx.setRenderingHints(
                new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON));
        gfx.setColor(Color.GREEN);

        final int w = graph.getWidth();
        final int h = graph.getHeight();
        final int interval = Math.max((view.getX1() - view.getX0()) / w, 1);

        int prevX = 0;
        int prevY = h / 2;
        for (int i = Math.max(view.getX0(), 0);
             i < Math.min(view.getX1(), peaks.length);
             i += interval) {
            // Scale x and y to fit buffer
            final int x = (int) (((double) (i - view.getX0())
                    / (double) (view.getX1() - view.getX0())) * (double) w);
            final int y = (h / 2) - (int) (peaks[i]
                    * (double) h / (2.0 * view.getPadding()));

            // First vertex has no links going before it
            if (i > view.getX0()) {
                gfx.drawLine(prevX, prevY, x, y);
            }

            prevX = x;
            prevY = y;
        }

        gfx.dispose();
        return graph;
    }

    private BufferedImage plotSpectrogram(final double[] peaks, Viewport view,
                                          int frameRate) {
        final BufferedImage spectrogram = new BufferedImage(
                view.getTargetW(),
                view.getTargetH(),
                BufferedImage.TYPE_BYTE_INDEXED);

        final Graphics2D gfx = spectrogram.createGraphics();
        // Do not use antialiasing for spectrogram

        final int w = spectrogram.getWidth();
        final int h = spectrogram.getHeight();
        final int frameInterval = Math.max((view.getX1() - view.getX0()) / w, 30); /* Math.min(Math.max(
                (view.getX1() - view.getX0()) / w,
                frameRate * 30 / 1000),
                frameRate) ; */

        for (int i = Math.max(view.getX0(), 0);
             i < Math.min(view.getX1(), peaks.length);
             i += (view.getX1() - view.getX0()) / w) {
//            double[] fft = new double[frameInterval * 2];
//            System.arraycopy(peaks, i,
//                    fft, 0,
//                    Math.min(frameInterval, peaks.length - i));
//            new DoubleFFT_1D(frameInterval).realForward(fft);

//            for (int j = 0; j < fft.length; j += 2) {
//                double real = fft[j];
//                double im   = fft[j+1];
//                double mag  = Math.sqrt(real*real + im*im);
//            }
        }

        gfx.dispose();
        return spectrogram;
    }

    @Override
    public void start(final Stage stage) throws Exception {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("All files", "*"));

        final File audioFile = fileChooser.showOpenDialog(stage);
        if (audioFile == null) {
            return;
        }

        final AudioInputStream audioStream =
                AudioSystem.getAudioInputStream(audioFile);

        final double[][] peaks = SoundIO.readAllFrames(audioStream);

        final BufferedImage graph = plotGraph(peaks[0],
                new Viewport(0, peaks[0].length, 1024, 256, 1.5));

        final BufferedImage spectrogram = plotSpectrogram(peaks[0],
                new Viewport(0, 0, peaks[0].length, 20000,
                        1024, 512, 1.0),
                (int) audioStream.getFormat().getFrameRate());

        // TODO: REMOVEME
        ImageIO.write(graph, "png", new File("graph.png"));
        ImageIO.write(spectrogram, "png", new File("spectrogram.png"));

        stage.setTitle("vis");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
