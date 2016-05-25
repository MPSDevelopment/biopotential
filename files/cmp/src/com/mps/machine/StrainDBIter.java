package com.mps.machine;

import java.util.Iterator;

public class StrainDBIter implements StrainDB {
    public StrainDBIter(Iterator<Strain> iter) {
        this.iter = iter;
    }

    public boolean hasNext() {
        return this.iter.hasNext();
    }

    public Strain next() {
        return this.iter.next();
    }

    private final Iterator<Strain> iter;
}
