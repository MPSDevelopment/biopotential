package com.mps.machine;

import com.mps.analyzer.AnalysisSummary;
import com.mps.analyzer.Analyzer;
import com.mps.analyzer.AnalyzerException;
import com.mps.analyzer.ChunkSummary;

import java.util.Collection;
import java.util.HashMap;

public class Machine {
    public Machine() {
    }

    public void setDiseaseStrains(Collection<Strain> strains) {
        this.diseaseStrains = strains;
    }

    public void setCorrectionStrains(Collection<Strain> strains) {
        this.correctionStrains = strains;
    }

//    public void setCondition(MachineCondition condition) {
//        this.condition = condition;
//    }

    // TODO: REMOVEME
    public void scan(Collection<ChunkSummary> sample_summary)
            throws MachineException {
        if (this.diseaseStrains == null) {
            throw new MachineException("Disease strains are not set");
        }

//        if (this.correctionStrains == null) {
//            throw new MachineException("Correction strains are not set");
//        }

//        if (this.condition == null) {
//            throw new MachineException("Condition is not set");
//        }

        HashMap<String, Integer> counter = new HashMap<>();
        for (Strain disease : this.diseaseStrains) {
            try {
                AnalysisSummary summary = Analyzer.compare(
                        sample_summary, disease.getSummary());
                if (summary.getDegree() == 0) {
                    String kind = disease.getKind();
                    Integer val = counter.get(kind);
                    counter.put(kind, (val != null ? val : 0) + 1);
                }
            } catch (AnalyzerException e) {
                e.printStackTrace();
            }
        }
    }

//    private MachineCondition condition;
    private Collection<Strain> diseaseStrains;
    private Collection<Strain> correctionStrains;
}
