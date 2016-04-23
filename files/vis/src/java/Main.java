package vis;

import javafx.application.Application;

import java.awt.*;
import java.io.File;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class Main extends Application {
    private BufferedImage plotWaveGraph(final double[][] peaks,
                                        Viewport view) {
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
        final int precision = Math.max((view.getX1() - view.getX0()) / w, 1);

        for (int chan = 0; chan < peaks.length; chan += 1) {
            int prevX = 0;
            int prevY = h / 2;
            for (int i = view.getX0(); i < view.getX1(); i += precision) {
                // Scale x and y to fit buffer
                final int x = (int) (((double) (i - view.getX0())
                        / (double) (view.getX1() - view.getX0())) * (double) w);
                final int y = (h / 2) + (int) (peaks[chan][i]
                        * (double) h / (2.0 * view.getPadding()));

                // First vertex has no links going before it
                if (i > view.getX0()) {
                    gfx.drawLine(prevX, prevY, x, y);
                }

                prevX = x;
                prevY = y;
            }
        }

        gfx.dispose();
        return graph;
    }

    @Override
    public void start(final Stage stage) throws Exception {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("All files", "*.*"));

        final File audioFile = fileChooser.showOpenDialog(stage);
        if (audioFile == null) {
            return;
        }

        final AudioInputStream audioStream =
                AudioSystem.getAudioInputStream(audioFile);

        final double[][] peaks = Vis.getWaveForm(audioStream);

        final BufferedImage waveGraph = plotWaveGraph(peaks,
                new Viewport(0, peaks[0].length, 1024, 256, 1.5));

        // TODO: REMOVEME
        ImageIO.write(waveGraph, "png", new File("graph.png"));

        stage.setTitle("vis");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
