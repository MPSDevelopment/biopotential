package vis;

import javafx.application.Application;
import java.io.File;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class Main extends Application {
    private BufferedImage plotWaveGraph(final double[][] peaks) {
        final BufferedImage graph = new BufferedImage(peaks[0].length, 256,
                BufferedImage.TYPE_BYTE_INDEXED);

        final Graphics2D gfx = graph.createGraphics();

        gfx.setColor(Color.GREEN);
        int lastY = 0;
        for (int i = 1; i < peaks[0].length; i += 1) {
            // Each peak ranges from -1.0 to 1.0, upscale it to buffer size and
            // offset a bit from edges of the buffer.
            final int Y = (int)(peaks[0][i] * graph.getHeight() / 2.2)
                    + (graph.getHeight() / 2);
            gfx.drawLine(i - 1, lastY, i, Y);
            lastY = Y;
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

        final BufferedImage waveGraph = plotWaveGraph(peaks);

        // TODO: REMOVEME
        ImageIO.write(waveGraph, "png", new File("graph.png"));

        stage.setTitle("vis");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
