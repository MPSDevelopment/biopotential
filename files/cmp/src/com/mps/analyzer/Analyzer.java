package com.mps.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

public class Analyzer {
    public static ChunkSummary[] summarize(double[] frames) {
        int count = (frames.length - 5) / 2;
        double[] buffer = frames.clone();

        List summaries = new ArrayList<ChunkSummary>();

        // Somewhat resembling wavelet transform
        while (count > 0) {
            double[] sum  = new double[count];
            double[] diff = new double[count];
            for (int j = 0; j < count; j += 1) {
                sum[j] = buffer[j*2    ] *  0.0352262918821
                       + buffer[j*2 + 1] *  0.08544127388224
                       + buffer[j*2 + 2] * -0.13501102001039
                       + buffer[j*2 + 3] * -0.45987750211933
                       + buffer[j*2 + 4] *  0.80689150931334
                       + buffer[j*2 + 5] * -0.33267055295096;

                diff[j] = buffer[j*2    ] *  0.33267055295096
                        + buffer[j*2 + 1] *  0.80689150931334
                        + buffer[j*2 + 2] *  0.45987750211933
                        + buffer[j*2 + 3] * -0.13501102001039
                        + buffer[j*2 + 4] * -0.08544127388224
                        + buffer[j*2 + 5] *  0.0352262918821;
            }

            final double meanDeviation = computeMeanDeviation(sum);
            final double dispersion    = computeDispersion(sum, meanDeviation);
            summaries.add(new ChunkSummary(meanDeviation, dispersion));

            buffer = diff;
            count  = (count - 5) / 2;
        }
        return (ChunkSummary[]) summaries.toArray(
                new ChunkSummary[summaries.size()]);
    }

    public static ChunkSummary[] readSummaryFromStream(InputStream file) {
        final int total = readStreamLE(file, 2);
        ChunkSummary[] summaries = new ChunkSummary[total];
        for (int i = 0; i < total; i += 1) {
            final int count = readStreamLE(file, 4);
            double[] sum = new double[count];
            for (int j = 0; j < count; j += 1) {
                sum[j] = (double) Float.intBitsToFloat(readStreamLE(file, 4));
            }

            final double meanDeviation = computeMeanDeviation(sum);
            final double dispersion    = computeDispersion(sum, meanDeviation);
            summaries[i] = new ChunkSummary(meanDeviation, dispersion);
        }
        return summaries;
    }

    public static AnalysisSummary compare(ChunkSummary[] first,
                                          ChunkSummary[] second)
            throws AnalyzerException {
        if (first.length != second.length) {
            throw new AnalyzerException("first.length != second.length");
        }

        final int length = first.length;

        double meanDeviation = 0.0;
        double dispersion    = 0.0;

        for (int i = 0; i < length; i += 1) {
            meanDeviation += Math.abs(first[i].getMeanDeviation()
                    - second[i].getMeanDeviation());
            dispersion += Math.abs(first[i].getDispersion()
                    - second[i].getDispersion());
        }

        meanDeviation /= length;
        dispersion    /= length;

        return new AnalysisSummary(meanDeviation, dispersion,
                (int) Double.doubleToLongBits(dispersion));
    }

    private static double computeMeanDeviation(double[] sum) {
        double result = 0.0;
        for (double num : sum) {
            result += num;
        }
        return result / (double) sum.length;
    }

    private static double computeDispersion(double[] sum,
                                            double meanDeviation) {
        double result = 0.0;
        for (double num : sum) {
            double delta = num - meanDeviation;
            result += delta * delta;
        }
        return Math.sqrt(result / (double) sum.length);
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
