package com.mps.machine;

import java.util.Iterator;

public class DemoIter implements StrainDB {
    public DemoIter(Iterator<Strain> iter) {
        this.iter = iter;
    }

    public Strain next() {
        return this.iter.hasNext() ? this.iter.next() : null;
    }

    private final Iterator<Strain> iter;
}
