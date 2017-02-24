package com.mpsdevelopment.biopotential.server.gui.service;

import com.google.gson.reflect.TypeToken;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.controller.DiseaseController;
import com.mpsdevelopment.biopotential.server.db.pojo.DataTable;
import com.mpsdevelopment.biopotential.server.httpclient.BioHttpClient;
import com.mpsdevelopment.biopotential.server.httpclient.HttpClientFactory;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import javafx.collections.ObservableList;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;

public class AnalyzeService {

    private static final String HOST = "localhost";
    private static final int PORT = 8098;
    private static double alWeight = 0, viWeight = 0, caWeight = 0, deWeight = 0, enWeight = 0, gaWeight = 0, imWeight = 0, neWeight = 0, orWeight = 0, spWeight = 0, stWeight = 0, urWeight = 0;

    private static final Logger LOGGER = LoggerUtil.getLogger(DiseaseController.class);

    /**
     *
     * @param url1  string url to controller with degree1
     * @param url2  string url to controller with degree2
     * @param file  input audio file
     * @return   Map<Pattern, AnalysisSummary>
     */
    public static Map<Pattern, AnalysisSummary> getDiseases(String url1, String url2, File file) {

        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();

        String json = bioHttpClient.executePostRequest(url1 , file);

        Map<Pattern, AnalysisSummary> diseases = new HashMap<>();
        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();

        Map<Pattern, AnalysisSummary> diseasesMax = JsonUtils.fromJson(typeOfHashMap, json);

        json = bioHttpClient.executePostRequest(url2 , file);
        Map<Pattern, AnalysisSummary> diseasesPo = JsonUtils.fromJson(typeOfHashMap, json);
        diseases.putAll(diseasesMax);
//        diseases.putAll(diseasesPo);
        return diseases;
    }

    /**
     * get diseases from controller by one degree value
     * @param url
     * @param file
     * @return
     */
    public static Map<Pattern, AnalysisSummary> getDiseasesByDegree(String url, File file) {
        Map<Pattern, AnalysisSummary> diseases = getPostRequest(url, file);
        return diseases;
    }

    /**
     *
     * @param urlMax url with Max degree
     * @param urlPo url with Po degree
     * @param diseaseMax map with diseases by Max degree
     * @param diseasePo map with diseases by Po degree
     * @return  healings map Map<Pattern, AnalysisSummary> with healings by Max and Po degree's
     */
    public static Map<Pattern, AnalysisSummary> getHealings(String urlMax, String urlPo, Map<Pattern, AnalysisSummary> diseaseMax, Map<Pattern, AnalysisSummary> diseasePo) {

        Map<Pattern, AnalysisSummary> healingsMax = getPostRequest(urlMax, diseaseMax);
//        Map<Pattern, AnalysisSummary> healingsPo = getPostRequest(urlPo, diseasePo);
        Map<Pattern, AnalysisSummary> healings = new HashMap<>();
        healings.putAll(healingsMax);
//        healings.putAll(healingsPo);
        return healings;
    }

    /**
     * Execute PostRequest with Map to DiseasController
     * @param url
     * @param disease
     * @return
     */
    private static Map<Pattern, AnalysisSummary> getPostRequest(String url, Map<Pattern, AnalysisSummary> disease) {
        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();
        String heal = bioHttpClient.executePostRequest(url,disease);
        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();

        Map<Pattern, AnalysisSummary> healings = JsonUtils.fromJson(typeOfHashMap, heal);
        return healings;
    }

    /**
     * execute PostRequest and return Map<Pattern, AnalysisSummary> with disease's by one url
     * @param url
     * @param file
     * @return
     */
    private static Map<Pattern, AnalysisSummary> getPostRequest(String url, File file) {
        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();

        String json = bioHttpClient.executePostRequest(url , file);
        Type typeOfHashMap = new TypeToken<Map<EDXPattern, AnalysisSummary>>() { }.getType();
        return JsonUtils.fromJson(typeOfHashMap, json);
    }

    public static void getPatternsSize() {
        clearSystemsWeight();

        Type type = new TypeToken<Map<String, Integer>>() { }.getType();
        BioHttpClient bioHttpClient = HttpClientFactory.getInstance();

        String url = String.format("http://%s:%s%s", HOST, PORT, ControllerAPI.PATTERNS_CONTROLLER + ControllerAPI.PATTERNS_CONTROLLER_GET_PATTERNS_SIZE);
        String size = bioHttpClient.executeGetRequest(url);

        Map<String,Integer> sizeMap = JsonUtils.fromJson(type,size);

        sizeMap.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer weight) {
                if (s.contains("Al ALLERGY")) {
                    alWeight = (double) 100/weight;
                }
                if (s.contains("Ca CARDIO")) {
                    caWeight = (double) 100/weight;
                }
                if (s.contains("De DERMA")) {
                    deWeight = (double) 100/weight;
                }
                if (s.contains("En ENDOKRIN")) {
                    enWeight = (double) 100/weight;
                }
                if (s.contains("Ga GASTRO")) {
                    gaWeight = (double) 100/weight;
                }
                if (s.contains("Im IMMUN")) {
                    imWeight = (double) 100/weight;
                }
                if (s.contains("Ne NEURAL")) {
                    neWeight = (double) 100/weight;
                }
                if (s.contains("Or ORTHO")) {
                    orWeight = (double) 100/weight;
                }
                if (s.contains("Sp SPIRITUS")) {
                    spWeight = (double) 100/weight;
                }
                if (s.contains("St STOMAT")) {
                    stWeight = (double) 100/weight;
                }
                if (s.contains("Ur UROLOG")) {
                    urWeight = (double) 100/weight;
                }
                if (s.contains("Vi VISION")) {
                    viWeight = (double) 100/weight;
                }
            }
        });

//        return sizeMap;
    }

    private static void clearSystemsWeight() {
        alWeight = 0;
        viWeight = 0;
        caWeight = 0;
        deWeight = 0;
        enWeight = 0;
        gaWeight = 0;
        imWeight = 0;
        neWeight = 0;
        orWeight = 0;
        spWeight = 0;
        stWeight = 0;
        urWeight = 0;
    }

    /**
     *
     * @param map input Map<Pattern, AnalysisSummary> with deseases
     * @param analysisData output ObservableList<DataTable> for javafx tables
     * @return ObservableList<DataTable>
     */
    public static ObservableList<DataTable> diseasToAnalysisData(Map<Pattern, AnalysisSummary> map, ObservableList<DataTable> analysisData) {

        map.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern k, AnalysisSummary v) {
                LOGGER.info("d: %s\t%f\n", k.getName(), v.getDispersion());
                analysisData.add(DataTable.createDataTableObject(k, v));
            }
        });
        return analysisData;
    }

    /**
     * sort Map<Pattern, AnalysisSummary> with disease's only for pattern's which belong's to system folder's
     * @param diseasesSystems
     */
    public static void sortBySystem(Map<Pattern, AnalysisSummary> diseasesSystems) {
        Map<Pattern, AnalysisSummary> diseasesTemp = new HashMap<>();

        diseasesSystems.forEach(new BiConsumer<Pattern, AnalysisSummary>() {
            @Override
            public void accept(Pattern pattern, AnalysisSummary analysisSummary) {
                if (!(pattern.getKind().equals("Dt DETOKC")) &&!(pattern.getKind().equals("Bо Физ кодиции")) && !(pattern.getKind().equals("FL Ac Acariasis")) && !(pattern.getKind().equals("FL Ba Bacteria")) && !(pattern.getKind().equals("FL El Elementary")) &&
                        !(pattern.getKind().equals("FL He Helminths")) && !(pattern.getKind().equals("FL My Mycosis")) && !(pattern.getKind().equals("FL Vi Virus")) && !(pattern.getKind().equals("Fe Femely")) &&
                        !(pattern.getKind().equals("Ma Man"))) {
                    diseasesTemp.put(pattern, analysisSummary);
                }
            }
        });
        diseasesSystems.clear();
        diseasesSystems.putAll(diseasesTemp);
    }

    /**
     * Get system folder map with keys and values to display on barchart
     * @param sortedSelectedItems sorted Set with diseases
     * @return Map<String, Double>
     */
    public static Map<String, Double> getSystemMap(Set<DataTable> sortedSelectedItems) {

        Map<String,Double> systemMap = new HashMap<>();
        systemMap.put("AL",0d);
        systemMap.put("CA",0d);
        systemMap.put("DE",0d);
        systemMap.put("En",0d);
        systemMap.put("GA",0d);
        systemMap.put("IM",0d);
        systemMap.put("ME",0d);
        systemMap.put("NE",0d);
        systemMap.put("OR",0d);
        systemMap.put("SP",0d);
        systemMap.put("St",0d);
        systemMap.put("UR",0d);
        systemMap.put("VI",0d);

        // decode diseas names to system's name's
        for (DataTable dataTable: sortedSelectedItems) {
            for (int i = 0 ; i < dataTable.getName().length(); i++) {
                if ((dataTable.getName().charAt(i) == '♥') || (dataTable.getName().charAt(i) == 'ლ') || (dataTable.getName().charAt(i) == '♋') || (dataTable.getName().charAt(i) == '⌘')
                        || (dataTable.getName().charAt(i) == '☂') || (dataTable.getName().charAt(i) == '☺') || (dataTable.getName().charAt(i) == '♕') || (dataTable.getName().charAt(i) == '☤')
                        || (dataTable.getName().charAt(i) == '✽') || (dataTable.getName().charAt(i) == '〲') || (dataTable.getName().charAt(i) == 'Ü') || (dataTable.getName().charAt(i) == '☄')) {
                    break;
                }
            }

            switch (dataTable.getName().substring(0, 2)) {
                case "AL":
                    systemMap.put("AL",systemMap.get("AL")+alWeight);
                    break;
                case "Ca":
                    systemMap.put("CA",systemMap.get("CA")+caWeight);
                    break;
                case "De":
                    systemMap.put("DE",systemMap.get("DE")+deWeight);
                    break;
                case "En":
                    systemMap.put("En",systemMap.get("En")+deWeight);
                    break;
                case "Ga":
                    systemMap.put("GA",systemMap.get("GA")+gaWeight);
                    break;
                case "Im":
                    systemMap.put("IM",systemMap.get("IM")+imWeight);
                    break;
                case "Ne":
                    systemMap.put("NE",systemMap.get("NE")+neWeight);
                    break;
                case "Or":
                    systemMap.put("OR",systemMap.get("OR")+orWeight);
                    break;
                case "Sp":
                    systemMap.put("SP",systemMap.get("SP")+spWeight);
                    break;
                case "St":
                    systemMap.put("St",systemMap.get("St")+stWeight);
                    break;
                case "Ur":
                    systemMap.put("UR",systemMap.get("UR")+urWeight);
                    break;
                case "Vi":
                    systemMap.put("VI",systemMap.get("VI")+viWeight);
                    break;
            }
        }
        return systemMap;
    }

    /**
     * SortedSelected set which contains system's with system disease appear > than 1 time
     * @param data
     * @return
     */
    public static Set<DataTable> sortSelected(ObservableList<DataTable> data) {
        Set<DataTable> sortedSelectedItems = new HashSet<>();
        char [] str = new char[5];
        char [] cmp = new char[5];
        // check how many disease from same system have been received from analysis
        for (DataTable dataTable: data) {
            int count=0;
            dataTable.getName().getChars(0,4,str,0);
            for (DataTable temp: data) {
                temp.getName().getChars(0,4,cmp,0);
                if(Arrays.equals(str,cmp)){
                    count++;
                }
//                if (count > 1) {  // uncomment this string to able appear > than 1 time
                sortedSelectedItems.add(dataTable);
//                }
            }
        }
        return sortedSelectedItems;
    }
}
