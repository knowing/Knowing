package de.lmu.ifi.dbs.utilities.distances;

public class EuclideanSquared extends DistanceAdapter {

    @Override
    public double dist(double[] a, double[] b) {
        assert a != null : "a is null";
        assert b != null : "b is null";
        assert a.length == b.length : "arrays have different length: " + a.length + " <> " + b.length;
//        if (a.length != b.length) {
//            throw new IllegalArgumentException("arrays have different length: " + a.length + " <> " + b.length);
//        }

        double sum = 0;
        double tmp = 0;
        for (int x = 0; x < a.length; x++) {
            assert !Double.isNaN(a[x]) : "a contains a NaN at index " + x;
            assert !Double.isNaN(b[x]) : "b contains a NaN at index " + x;
            tmp = a[x] - b[x];
            sum += tmp * tmp;
        }
        return sum;
    }
}
