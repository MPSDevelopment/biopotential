package com.mpsdevelopment.biopotential.server.cmp.pcm;

import com.mpsdevelopment.biopotential.server.db.dao.DiseaseDao;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PCM {

    private static final Logger LOGGER = LoggerUtil.getLogger(PCM.class);

    // TODO: FIXME
    public static List<Double> fold(List<Double> input, int len) {
        final List<Double> result = new ArrayList<>(Collections.nCopies(len, 0.0));
//        final List<Double> result = new ArrayList<>(len);
        for (int i = 0; i < input.size(); i += 1) {
            if (i == 66803) {
                System.out.println();
            }
            if ((result.get(i % len) + input.get(i)) > 1.0) {
                result.set(i % len, 1.0);
//                LOGGER.info("%f", result.get(i % len) + input.get(i));
            } else if ((result.get(i % len) + input.get(i)) < -1.0) {
                result.set(i % len, -1.0);
//                LOGGER.info("%f", result.get(i % len) + input.get(i));

            }
            else {
                result.set(i % len, result.get(i % len) + input.get(i));
//                LOGGER.info("%f", result.get(i % len) + input.get(i));

            }
        }
        for (Double aDouble:result) {
            if (aDouble > 1.0) {
                LOGGER.info("Value greater %f", aDouble);
            }

        }
        /*for (int j = 0; j < len; j += 1) {
            result.set(j, result.get(j) / (double) 128.0);
        }*/

        return result;
    }

    /*public static List<Float> merge(Collection<List<Float>> input) {
        return input.stream().reduce(new ArrayList<>(), new BinaryOperator<List<Float>>() {
            @Override
            public List<Float> apply(List<Float> list1, List<Float> list2) {
                for (int i = 0; i < list2.size(); i++) {
                    if (i >= list1.size()) {
                        list1.add(list2.get(i));
                    } else {
                        list1.set(i, list1.get(i) + list2.get(i));
                    }
                }
                return list1;
            }
        }).stream().map(new Function<Float, Float>() {
            @Override
            public Float apply(Float x) {
                return x *//*//* input.size()*//*;
            }
        }).collect(Collectors.toList());
    }*/

    public static float[] merge(List<float[]> input) {
        int maxsize = 0;

        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).length > maxsize) {
                maxsize = input.get(i).length;
            }
        }

        float[] sizeArray = new float[maxsize];
        float[] temp = input.get(0);

//        sizeArray = Arrays.copyOf(temp, temp.length);
        System.arraycopy(temp,0,sizeArray,0,temp.length);
        for (float[] floats:input) {
            for (int i = 0; i < floats.length; i++) {
                if (i >= sizeArray.length) {
                    LOGGER.info("size array %s ", floats.length);
                }
                else {
                    sizeArray[i] = sizeArray[i] + floats[i];
                }
            }
        }
        return sizeArray;

        /*float[] temp = input.get(0);
        input.forEach(new Consumer<float[]>() {
            @Override
            public void accept(float[] floats) {
                for (int i = 0; i < floats.length; i++) {
                    if (i >= temp.length) {
                        LOGGER.info("size array %s ", floats.length);
                    }
                    else {
                        temp[i] = temp[i] + floats[i];
                    }
                }
//                temp = ArrayUtils.addAll(floats);
            }
        });
//        list.add(temp);
        return temp;*/
    }
}
