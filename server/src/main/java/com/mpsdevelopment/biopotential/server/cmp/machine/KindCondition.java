package com.mpsdevelopment.biopotential.server.cmp.machine;

public interface KindCondition {
    boolean test(String kind, int count);
}
