package de.sendsor.accelerationSensor.util;

import de.lmu.ifi.dbs.utilities.Arrays2;
import java.util.List;

public class LabeledDoubleFeature {

    private final double[] values;
    private final String label;
    private final List<LabeledDoubleFeature> rawVectors;

    public LabeledDoubleFeature(double[] values, String label) {
        this.values = values;
        this.label = label;
        this.rawVectors = null;
    }

    public LabeledDoubleFeature(List<String> stringValues, String label) {
        this.values = new double[stringValues.size()];
        this.label = label;
        for (int i = 0; i < values.length; i++) {
            values[i] = Double.valueOf(stringValues.get(i));
        }
        this.rawVectors = null;
    }

    public LabeledDoubleFeature(double[] newValues, String label, List<LabeledDoubleFeature> rawData) {
        this.values = newValues;
        this.label = label;
        this.rawVectors = rawData;
    }

    public String getLabel() {
        return label;
    }

    public double[] getValues() {
        return values;
    }

    public List<LabeledDoubleFeature> getRawVectors() {
        return rawVectors;
    }

    @Override
    public String toString() {
        return "{" + "values=" + Arrays2.join(values, ",", "%.2f") + ", label=" + label + '}';
    }
}
