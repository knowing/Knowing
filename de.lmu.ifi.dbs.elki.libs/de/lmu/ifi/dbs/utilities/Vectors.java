package de.lmu.ifi.dbs.utilities;

public final class Vectors {

    public static double length(double[] in) {
        double length = 0;
        for (double d : in) {
            length += d * d;
        }
        return Math.sqrt(length);
    }

    public static void normalize(double[] in) {
        Arrays2.div(in, length(in));
    }

    public static double cosinus(double[] a, double[] b) {
        return dot(a, b) / (length(a) * length(b));
    }

    /**
     * computes the dot product
     *
     * @param o1
     * @param o2
     * @return dot product
     * @throws IllegalArgumentException
     *             if o1 and o2 have different dimensions
     * @todo Remove as it is not really a thin of arrays but of vectors
     */
    public static double dot(double[] o1, double[] o2) {
        double result = 0;
        int l1 = o1.length;
        int l2 = o1.length;
        if (l1 != l2) {
            throw new IllegalArgumentException(
                    "dimensions must be equal but were: " + l1 + " <-> " + l2);
        }
        for (int x = 0; x < l1; x++) {
            result += o1[x] * o2[x];
        }
        return result;
    }
}
