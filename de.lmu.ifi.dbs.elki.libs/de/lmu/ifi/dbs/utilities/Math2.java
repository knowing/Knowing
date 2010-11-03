package de.lmu.ifi.dbs.utilities;

import java.awt.geom.Point2D;

/**
 * Additional math stuff which is not found in the {@link Math} class
 */
public class Math2 {

    /**
     * Rotate a point around the origin
     *
     * @param x
     * @param y
     * @param deg
     *            in degrees
     * @return
     */
    public static Point2D rotate(int x, int y, double deg) {
        double length = Math.sqrt(x * x + y * y);
        double theta = Math.atan2(y, x);
        theta += Math.toRadians(deg);

        double newX = length * Math.cos(theta);
        double newY = length * Math.sin(theta);

        return new Point2D.Double(newX, newY);
    }

    /**
     * calculate the gaussian derivation in 2d
     *
     * @param x
     * @param y
     * @param sigma
     * @return
     */
    public static double gauss(double x, double y, double sigma) {
        // calculate gauss. mu = 0, sigma1=sigma2=sigma
        double sig2 = 2 * sigma * sigma;
        double g = Math.exp(-(x * x + y * y) / sig2) / (Math.PI * sig2);
        return g;
    }

    /**
     * calculate the gaussian derivation with mu = 0
     *
     * @param x
     * @param sigma
     * @return
     */
    public static double gauss(double x, double sigma) {
        double g = (1 / (sigma * Math.sqrt(2 * Math.PI)))
                * Math.exp(-(x * x) / (2 * sigma * sigma));
        return g;
    }

    /**
     * return a normalized gaussian kernel for the specified array size
     *
     * @param size
     * @return
     */
    public static double[] getGauss(int size) {
        assert size % 2 == 1 : "mask size must be uneven";
        double[] k = new double[size];

        int offset = k.length / 2;
        double sum = 0;
        for (int i = 0; i < k.length; i++) {
            k[i] = Math2.gauss(i - offset, 1);
            sum += k[i];
        }

        // normalize
        for (int i = 0; i < k.length; i++) {
            k[i] /= sum;
        }
        return k;
    }

    /**
     *
     * @param min
     * @param value
     * @param max
     * @return bound value between min and max (inclusive) [min, value, max]
     */
    public static double bind(double min, double value, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     *
     * @param min
     * @param v
     * @param max
     * @return true, if v is in [min; max]
     */
    public static boolean isIn(int min, int v, int max) {
        if (v < min) {
            return false;
        }
        if (v > max) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param min
     * @param v
     * @param max
     * @return true, if v is in [min; max]
     */
    public static boolean isIn(double min, double v, double max) {
        if (v < min) {
            return false;
        }
        if (v > max) {
            return false;
        }
        return true;
    }

    /**
     * Transforms the given point from karthesian to polar coordinates.
     *
     * x is then the length to 0,0; y is the angle in the range -Pi;Pi
     *
     * @param p
     */
    public static void toPolar(Point2D p) {
        double x = p.getX();
        double y = p.getY();
        p.setLocation(Math.sqrt(x * x + y * y), Math.atan2(y, x));
    }

    /**
     * Transforms the given point from polar to Karthesian coordinates.
     *
     * @param p
     */
    public static void toKarthesian(Point2D p) {
        double x = p.getX() * Math.cos(p.getY());
        double y = p.getX() * Math.sin(p.getY());
        p.setLocation(x, y);
    }

    /**
     * Computes the cosine of <code>a</code> and <code>b</code> as
     * <code>&lt;obj1,obj2&gt; / (||a|| ||b||)</code>
     *
     * @param a
     * @param b
     * @return The cosine between <code>a</code> and <code>b</code>
     */
    @SuppressWarnings("deprecation")
    public static double cosinus(double[] a, double[] b) {
        return Vectors.cosinus(a, b);
    }

    /**
     * Method that wraps {@link Math#pow(double, double)} and linearizes the
     * calculation of the exponent up to power=64 which was faster on my machine
     * in all tests.
     * 
     * @param a
     * @param b
     * @return
     */
    public static double pow(double a, double b) {
        // on my machine, power=64 marks a limit where Math.pow becomes faster
        if (b == (int) b && ((int) b >> 6) == 0) {
            if (b == 0) {
                return 1;
            } else if (b == 1) {
                return a;
            } else {
                double result = a;
                for (int i = 1; i < b; i++) {
                    result *= a;
                }
                return result;
            }
        }
        return Math.pow(a, b);
    }
}
