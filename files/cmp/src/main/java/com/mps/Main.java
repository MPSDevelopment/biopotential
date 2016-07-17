package com.mps;

import java.io.File;

import com.mps.machine.Strain;
import com.mps.machine.dbs.arkdb.ArkDB;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main {
//    @Override
//    public void start(Stage stage) {
//        final FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().addAll(
//            new FileChooser.ExtensionFilter("RIFF WAVE", "*.wav"),
//            new FileChooser.ExtensionFilter("Any", "*"));
//        final File audioFile = fileChooser.showOpenDialog(stage);
//        if (audioFile == null) {
//            return;
//        }
//    }

    public static void main(String[] args) {
        try {
//            Collection<Strain> diseaseStrains = new ArrayList<Strain>() {{
//                  add(new WAWStrain("g1", "n0101", "ills/k210101.waw"));
//                  add(new WAWStrain("g1", "n0102", "ills/k210102.waw"));
//                  add(new WAWStrain("g1", "n0103", "ills/k210103.waw"));
//                  add(new WAWStrain("g1", "n0104", "ills/k210104.waw"));
//                  add(new WAWStrain("g1", "n0105", "ills/k210105.waw"));
//                  add(new WAWStrain("g2", "n0106", "ills/k210106.waw"));
//                  add(new WAWStrain("g2", "n0107", "ills/k210107.waw"));
//                  add(new WAWStrain("g2", "n0108", "ills/k210108.waw"));
//                  add(new WAWStrain("g2", "n0109", "ills/k210109.waw"));
//                  add(new WAWStrain("g2", "n0110", "ills/k210110.waw"));
//                  add(new WAWStrain("g2", "n0201", "ills/k210201.waw"));
//                  add(new WAWStrain("g2", "n0202", "ills/k210202.waw"));
//                  add(new WAWStrain("g2", "n0203", "ills/k210203.waw"));
//                  add(new WAWStrain("g2", "n0204", "ills/k210204.waw"));
//                  add(new WAWStrain("g2", "n0205", "ills/k210205.waw"));
//                  add(new WAWStrain("g2", "n0206", "ills/k210206.waw"));
//            }};

//            Collection<Strain> healStrains = new ArrayList<Strain>() {{
//                add(new WAWStrain("g2", "n0203", "ills/k210203.waw"));
//            }};

            ArkDB db = new ArkDB("test.arkdb");

//            try (WaveInputStream si = new WaveInputStream(new File("test.wav"))) {
//                for (double[] frameBundle : si.readFrames(2)) {
//                    System.out.println(frameBundle[0]);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            final Collection<ChunkSummary> sample =
//                Analyzer.summarize(_SoundIO.readAllFrames(
//                    AudioSystem.getAudioInputStream(new File("test.wav")))[0]);
//
//            Map<Strain, AnalysisSummary> summaries = Machine.summarizeStrains(
//                (strain, summary) -> summary.getDegree() == 0,
//                sample,
//                new DemoIter(diseaseStrains.iterator()));
//
//            Map<String, Integer> probableKinds = Machine.filterKinds(
//                (kind, count) -> count > 0, summaries);
//
//            Map<Strain, AnalysisSummary> healings = Machine.summarizeStrains(
//                (strain, summary) -> probableKinds.containsKey(strain.getKind())
//                        && summary.getDegree() == 0,
//                sample,
//                new DemoIter(healStrains.iterator()));
//
//            healings.forEach((k, v) ->
//                System.out.printf("%s\n", k.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
