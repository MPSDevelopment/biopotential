class CustomChunk {
    public double getDeviation() {
        return deviation;
    }

    public double getDeviation() {
        return deviation;
    }

    private static double computeDeviation(double raw_data[]) {
        double result = 0.0;
        for (int i = 0; i < raw_data.length; i++) {
            result += raw_data[i];
        }

        return result;
    }

    private static double computeDispersion(double raw_data[],
                                            double deviation) {
        double result = 0.0;
        for (int i = 0; i < raw_data.length; i++) {
            double delta = raw_data[i] - deviation;
            result += delta * delta;
        }

        return Math.sqrt(result);
    }

    void compute() {
        deviation  = computeDeviation(raw_data);
        dispersion = computeDispersion(raw_data, deviation);
    }

    double raw_data[];
    private double deviation;
    private double dispersion;
}