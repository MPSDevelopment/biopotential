package vis;

import javafx.application.Application;
import java.io.File;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("All files", "*.*"));

        File audioFile = fileChooser.showOpenDialog(stage);
        if (audioFile == null) {
            return;
        }

        AudioInputStream audioStream =
                AudioSystem.getAudioInputStream(audioFile);

        double[][] peaks = Vis.getWaveForm(audioStream);

        final XYChart.Series series = new XYChart.Series();

        for (int i = 0; i < peaks[0].length; i += 512) {
            series.getData().add(new XYChart.Data(i, peaks[0][i]));
        }

        // TODO: Find a better way to render this
        final LineChart<Number, Number> waveChart =
                new LineChart<>(new NumberAxis(), new NumberAxis());
        waveChart.getData().add(series);

        final Scene scene = new Scene(waveChart, 640, 480);
        stage.setScene(scene);

        stage.setTitle("vis");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
