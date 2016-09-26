package com.mps.pcm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PCM {
    // TODO: FIXME
    public static List<Double> fold(List<Double> input, int len) {
        final List<Double> result = new ArrayList<>(Collections.nCopies(len, 0.0));
//        for (int i = 0; i < input.size(); i += 1) {
//            result.set(i % len, result.get(i % len) + input.get(i));
//        }
//        for (int j = 0; j < len; j += 1) {
//            result.set(j, result.get(j) / (double) len);
//        }
        return result;
    }

    public static List<Double> merge(Collection<List<Double>> input) {
        return input
            .stream()
            .reduce(new ArrayList<>(), (list1, list2) -> {
                for (int i = 0; i < list2.size(); i += 1) {
                    if (i >= list1.size()) {
                        list1.add(list2.get(i));
                    } else {
                        list1.set(i, list1.get(i) + list2.get(i));
                    }
                }
                return list1;
            })
            .stream()
            .map((x) -> x / input.size())
            .collect(Collectors.toList());
    }
}
