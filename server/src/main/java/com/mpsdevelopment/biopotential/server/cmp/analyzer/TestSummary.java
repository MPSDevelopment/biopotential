package com.mpsdevelopment.biopotential.server.cmp.analyzer;


public class TestSummary {
    final private String kind;
    final private String name;
    final private String desc;
//    final private String fileName;
    private final double dispersion;

    public TestSummary(String kind, String name, String desc, /*String fileName,*/ double dispersion) {
        this.kind = kind;
        this.name = name;
        this.desc = desc;
//        this.fileName = fileName;
        this.dispersion = dispersion;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    /*public String getFileName() {
        return fileName;
    }*/

    public Double getDispersion() {
        return dispersion;
    }
}
