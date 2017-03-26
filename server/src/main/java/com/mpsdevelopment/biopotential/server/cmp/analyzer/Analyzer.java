package com.mpsdevelopment.biopotential.server.cmp.analyzer;


import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.function.ToDoubleFunction;

public class Analyzer {
    // TODO: Make use of new sound API
    public static List<ChunkSummary> summarize(/*List<Float>*/float[] frames) {
        final List<ChunkSummary> summaries = new ArrayList<>();

        // Somewhat resembling wavelet transform
        // convert List<Double> to double[]
        /*double[] buffer = frames.stream().mapToDouble(new ToDoubleFunction<Float>() {
            @Override
            public double applyAsDouble(Double aDouble) {
                return aDouble.doubleValue();
            }
        }).toArray();*/

//        float[] buffer = ArrayUtils.toPrimitive((Float[]) frames.toArray(new Float[0]), 0.0F);
        float[] buffer = ArrayUtils.clone(frames);

        int count = (frames.length - 5) / 2;
        while (count > 0) {
            ;
            final float[] sum = new float[count];
            final float[] diff = new float[count];
            for (int j = 0; j < count; j++) {
                sum[j] = (float) (buffer[j * 2] * 0.0352262918821
                                        + buffer[j * 2 + 1] * 0.08544127388224
                                        + buffer[j * 2 + 2] * -0.13501102001039
                                        + buffer[j * 2 + 3] * -0.45987750211933
                                        + buffer[j * 2 + 4] * 0.80689150931334
                                        + buffer[j * 2 + 5] * -0.33267055295096);

                diff[j] = (float) (buffer[j * 2] * 0.33267055295096
                                        + buffer[j * 2 + 1] * 0.80689150931334
                                        + buffer[j * 2 + 2] * 0.45987750211933
                                        + buffer[j * 2 + 3] * -0.13501102001039
                                        + buffer[j * 2 + 4] * -0.08544127388224
                                        + buffer[j * 2 + 5] * 0.0352262918821);
            }
            final float meanDeviation = computeMeanDeviation(sum); // среднее отклонение, считается для каждого чанка
            final double dispersion = computeDispersion(sum, meanDeviation); // дисперсия
            summaries.add(new ChunkSummary(meanDeviation, dispersion)); // add Deviation and Dispersion to List<ChunkSummary>

            buffer = diff;
            count = (count - 5) / 2;
        }
        return summaries;
    }

    public static List<ChunkSummary> readSummaryFromWAW(InputStream file) {
        final int total = readStreamLE(file, 2);
        final List<ChunkSummary> summaries = new ArrayList<>();
        for (int i = 0; i < total; i += 1) {
            final int count = readStreamLE(file, 4);
            final float[] sum = new float[count];
            for (int j = 0; j < count; j += 1) {
                sum[j] = (float) Float.intBitsToFloat(readStreamLE(file, 4));
            }

            final float meanDeviation = computeMeanDeviation(sum);
            final float dispersion = computeDispersion(sum, meanDeviation);
            summaries.add(new ChunkSummary(meanDeviation, dispersion));
        }
        return summaries;
    }

    public static AnalysisSummary compare(List<ChunkSummary> first, List<ChunkSummary> second) {
        if (first == null || second == null) {
            return null;
        }

        final int size = Math.min(first.size(), second.size());

        double meanDeviation = 0.0;
        double dispersion = 0.0;

        for (int i=0; i<size; i++) {
            meanDeviation += Math.abs(first.get(i).getMeanDeviation() - second.get(i).getMeanDeviation());
            dispersion += Math.abs(first.get(i).getDispersion() - second.get(i).getDispersion());
        }

        meanDeviation = meanDeviation / size;
        dispersion = dispersion / size;

        return new AnalysisSummary(meanDeviation, dispersion, Float.floatToIntBits((float) dispersion) << 29); // старшые два бита это уровень
    }

    private static float computeMeanDeviation(float[] sum) {
        float result = 0.0f;
        for (float num : sum) {
            result += num;
        }
        return result / (float) sum.length;
    }

    private static float computeDispersion(float[] sum,
                                            float meanDeviation) {
        float result = 0.0f;
        for (float num : sum) {
            float delta = num - meanDeviation;
            result += delta * delta;
        }
        return (float) Math.sqrt(result / (float) sum.length);
    }

    private static int readStreamLE(InputStream stream, int length) {
        if (length <= 4) {
            try {
                int result = 0;
                for (int i = 0; i < length; i += 1) {
                    result |= (stream.read() & 0xff) << i * 8;
                }
                return result;
            } catch (IOException e) {
                return -1;
            }
        } else {
            return -1;
        }
    }
}
