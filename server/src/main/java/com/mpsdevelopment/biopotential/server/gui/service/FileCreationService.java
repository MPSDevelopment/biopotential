package com.mpsdevelopment.biopotential.server.gui.service;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.biopotential.server.gui.ConverterApplication;
import com.mpsdevelopment.biopotential.server.gui.converter.ConverterPanelController;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.function.Consumer;

public class FileCreationService extends Service<Void> {

    private static final Logger LOGGER = LoggerUtil.getLogger(ConverterPanelController.class);
    private DatabaseCreator databaseCreator;
    private UserDao userDao;
    private File file;
    Map<Pattern, AnalysisSummary> allHealings;
    Set<Pattern> sortedSelectedHealings = new HashSet<>();

    public FileCreationService(Map<Pattern, AnalysisSummary> allHealings) {
        this.allHealings = allHealings;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws IOException, MalformedURLException {
                LOGGER.info("Void call() method ");

                File file = new File("./data/out/out.mp3");
                if (file.exists()) {
                    file.delete();
                }

                AnalyzeService analyzeService = BioApplication.APP_CONTEXT.getBean(AnalyzeService.class);


                allHealings.forEach((pattern, analysisSummary) -> {
                    LOGGER.info("%s %s\n", pattern.getKind(), pattern.getName(), analysisSummary.getDispersion());
//            correctorsData.add(DataTable.createDataTableObject(pattern,analysisSummary));
                    sortedSelectedHealings.add(pattern);
                });

                List<double[]> floatArrayListWithPCMData = new ArrayList<>();
                sortedSelectedHealings.forEach(new Consumer<Pattern>() {
                    @Override
                    public void accept(Pattern pattern) {
                        floatArrayListWithPCMData.add(pattern.getPcmData(false));
                    }
                });
                floatArrayListWithPCMData.removeIf(o -> o == null);

                try {
                    analyzeService.merge(floatArrayListWithPCMData);
                } catch (UnsupportedAudioFileException e) {
                    LOGGER.printStackTrace(e);
                }

                return null;
            }
        };

    }

    /*@Handler
    public void handleMessage(ProgressBarEvent event) throws Exception {
        LOGGER.info(" Get delta from convert ");
        updateProgress(event.getProgress(), 1);
        LOGGER.info(" updateProgress %f ", event.getProgress());
    }*/


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
