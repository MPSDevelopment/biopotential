package com.mps.machine.strains;

import com.mps.analyzer.Analyzer;
import com.mps.analyzer.ChunkSummary;
import com.mps.machine.Strain;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

class EDXSection {
    String name;
    int offset;
    int length;
    byte[] contents;
}

public class EDXStrain implements Strain {
    public EDXStrain(String kind,
                     String name,
                     String desc,
                     String fileName) throws IOException {
        this.kind = kind;
        this.name = name;
        this.desc = desc;
        this.sects = new HashMap<String, EDXSection>();

        try (BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(new File(fileName)))) {
            byte[] hdr = new byte[4];
            if (in.read(hdr) != 4 || !new String(hdr).equals("EDXI")) {
                throw new IOException("not EDX");
            }

            // version number + .offs string
            if (in.skip(4 + 8) != (4 + 8)) {
                throw new IOException("not EDX");
            }

            int len = readi32le(in);
            int count = len / 16; // 16 = name[8] + offs + len
            for (int i = 0; i < count; i += 1) {
                EDXSection sect = new EDXSection();

                byte[] sect_name = new byte[8];
                in.read(sect_name);

                sect.name = new String(sect_name);
                sect.offset = readi32le(in);
                sect.length = readi32le(in);
                in.skip(12); // Skip junk at the beginning
                in.read(sect.contents, 0, sect.length);

                this.sects.put(sect.name, sect);
            }
        } catch (IOException e) {
            throw e;
        }

        this.pcmData = new ArrayList<>();
        for (byte b : this.sects.get(".orig   ").contents) {
            this.pcmData.add((double) b / 255.0);
        }

        this.summary = Analyzer.summarize(this.pcmData);
    }

    public String getKind() {
        return this.kind;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return "";
    }

    public Collection<Double> getPCMData() {
        return this.pcmData;
    }

    public Collection<ChunkSummary> getSummary() {
        return null;
    }

    private int readi32le(InputStream in) {
        try {
            return (in.read() & 0xff)
                | ((in.read() & 0xff) << 8)
                | ((in.read() & 0xff) << 16)
                | ((in.read() & 0xff) << 24);
        } catch (IOException e) {
            return 0;
        }
    }

    private HashMap<String, EDXSection> sects;
    private Collection<ChunkSummary> summary;
    private Collection<Double> pcmData;
    private String kind;
    private String name;
    private String desc;
}
