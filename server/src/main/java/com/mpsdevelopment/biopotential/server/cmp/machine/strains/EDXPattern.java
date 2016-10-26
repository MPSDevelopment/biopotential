package com.mpsdevelopment.biopotential.server.cmp.machine.strains;


import com.mpsdevelopment.biopotential.server.cmp.analyzer.Analyzer;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.ChunkSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class EDXSection {
    String name;
    int offset;
    int length;
    byte[] contents;
}

public class EDXPattern implements Pattern {

    final private HashMap<String, EDXSection> sects;
    final private List<ChunkSummary> summary;
    final private List<Double> pcmData;
    final private String correctingFolder;
    final private String kind;
    final private String name;
    final private String desc;
    final private String fileName;

    public EDXPattern(String kind, String name, String desc, String fileName) throws IOException {
        this(kind, name, desc, fileName, null);
    }

    public EDXPattern(String kind, String name, String desc, String fileName, String correctingFolder) throws IOException {
        this.kind = kind;
        this.name = name;
        this.desc = desc;
        this.sects = new HashMap<>();
        this.correctingFolder = correctingFolder;
        this.fileName = fileName;

        try (RandomAccessFile in = new RandomAccessFile(new File(fileName), "r")) {
            final byte[] hdr = new byte[4];
            if (in.read(hdr) != 4 || !new String(hdr).equals("EDXI")) {
                throw new IOException("not EDX");
            }
            // version number + .offs string
            if (in.skipBytes(4 + 8) != (4 + 8)) {
                throw new IOException("not EDX");
            }

            final int len = readi32le(in);
            final int count = len / 16; // 16 = name[8] + offs + len
            for (int i = 0; i < count; i += 1) {
                final EDXSection sect = new EDXSection();

                final byte[] sect_name = new byte[8];
                in.read(sect_name);

                sect.name = new String(sect_name);
                sect.offset = readi32le(in);
                sect.length = readi32le(in);
                sect.contents = new byte[sect.length];

                final long cur = in.getFilePointer();
                in.seek(sect.offset + 12); // 12 bytes of useless junk
                in.read(sect.contents, 0, sect.contents.length);
                in.seek(cur);

                this.sects.put(sect.name, sect);
            }
        } catch (IOException e) {
            throw e;
        }

        if (this.sects.containsKey(".orig   ")) {
            this.pcmData = new ArrayList<>();
            for (byte b : this.sects.get(".orig   ").contents) {
                this.pcmData.add((double) (byte) (b ^ 0x80) / 128.0);
            }
            this.summary = Analyzer.summarize(this.pcmData);
        } else {
            this.pcmData = null;
            this.summary = null;
        }
    }

    public boolean hasCorrectingFolder() {
        return this.correctingFolder != null;
    }

    public String getCorrectingFolder() {
        return this.correctingFolder;
    }

    public String getKind() {
        return this.kind;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.desc;
    }

    public List<Double> getPCMData() {
        return this.pcmData;
    }

    public List<ChunkSummary> getSummary() {
        return this.summary;
    }

    public String getFileName() {
        return fileName;
    }

    private int readi32le(RandomAccessFile in) {
        try {
            byte[] raw = new byte[4];
            in.read(raw);
            return (raw[0] & 0xff)
                    | ((raw[1] & 0xff) << 8)
                    | ((raw[2] & 0xff) << 16)
                    | ((raw[3] & 0xff) << 24);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EDXPattern that = (EDXPattern) o;

        if (kind != null ? !kind.equals(that.kind) : that.kind != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        return fileName != null ? fileName.equals(that.fileName) : that.fileName == null;

    }

    @Override
    public int hashCode() {
        int result = kind != null ? kind.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }
}
