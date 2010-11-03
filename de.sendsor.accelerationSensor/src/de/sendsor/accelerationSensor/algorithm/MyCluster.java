package de.sendsor.accelerationSensor.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

public class MyCluster {

    private final int numChildren;
    private final double[] centroid;
    private final HashMap<String, Double> labels;
    private final String label;

    public MyCluster(double[] centroid, HashMap<String, Double> labels, int clusterCount) {
        this.centroid = centroid;
        this.labels = labels;
        this.numChildren = clusterCount;

        String topLabel = null;
        Double topProbability = 0d;
        for (Entry<String, Double> entry : labels.entrySet()) {
            if (entry.getValue() > topProbability) {
                topLabel = entry.getKey();
                topProbability = entry.getValue();
            }
        }
        this.label = topLabel;
    }

    public double[] getCentroid() {
        return centroid;
    }

    public String getLabel() {
        return label;
    }

    public HashMap<String, Double> getLabels() {
        return labels;
    }

    public int getNumChildren() {
        return numChildren;
    }

    @Override
    public String toString() {
        List<Entry<String, Double>> parts = new ArrayList<Entry<String, Double>>(labels.entrySet());
        Collections.sort(parts, new Comparator<Entry<String, Double>>() {

            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return -o1.getValue().compareTo(o2.getValue()); // hightest on top
            }
        });

        String labelString = "";
        for (int i = 0; i < parts.size(); i++) {
            Entry<String, Double> e = parts.get(i);
            labelString += String.format(Locale.US, "%s: %.2f", e.getKey(), e.getValue());
            if (i < parts.size() - 1) {
                labelString += ", ";
            }
        }
        return "MyCluster{label=" + label + ", instances=" + numChildren + ", labels=" + labelString + '}';
    }
}
