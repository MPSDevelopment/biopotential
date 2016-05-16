package com.mps.machine;

import java.util.Collection;

public class Machine {
    public Machine() {
    }

    public void setDiseaseStrains(Collection<Strain> strains) {
        diseaseStrains = strains;
    }

    public void setCorrectionStrains(Collection<Strain> strains) {
        correctionStrains = strains;
    }

    // TODO: REMOVEME
    public void scan() throws MachineException {
        if (diseaseStrains == null) {
            throw new MachineException("Disease strains are not set");
        }

        if (correctionStrains == null) {
            throw new MachineException("Correction strains are not set");
        }
    }

    private Collection<Strain> diseaseStrains;
    private Collection<Strain> correctionStrains;
}
