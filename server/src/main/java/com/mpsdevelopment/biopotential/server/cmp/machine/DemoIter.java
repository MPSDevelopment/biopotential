package com.mpsdevelopment.biopotential.server.cmp.machine;


import java.util.Iterator;

public class DemoIter implements PatternDB {
    public DemoIter(Iterator<Pattern> iter) {
        this.iter = iter;
    }

    public Pattern next() {
        return this.iter.hasNext() ? this.iter.next() : null;
    }

    private final Iterator<Pattern> iter;
}
