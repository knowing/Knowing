package de.lmu.ifi.dbs.utilities.distances;

public class Euclidean extends EuclideanSquared {

    @Override
    public double dist(double[] a, double[] b) {
        return Math.sqrt(super.dist(a, b));
    }
}
