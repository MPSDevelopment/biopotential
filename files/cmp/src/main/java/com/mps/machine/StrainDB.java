package com.mps.machine;

public interface StrainDB {
    // boolean hasNext();
    Strain nextDiseaseStrain();
    Strain nextHealingStrain();
}
