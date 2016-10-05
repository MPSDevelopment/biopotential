package com.mpsdevelopment.biopotential.server.cmp.machine;

import com.mps.machine.*;

import java.util.Iterator;

public class DemoIter implements StrainDB {
    public DemoIter(Iterator<com.mps.machine.Strain> iter) {
        this.iter = iter;
    }

    public com.mps.machine.Strain next() {
        return this.iter.hasNext() ? this.iter.next() : null;
    }

    private final Iterator<com.mps.machine.Strain> iter;
}
