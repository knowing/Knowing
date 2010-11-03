package de.lmu.ifi.dbs.utilities.distances;

public interface Distance {

    double dist(double[] a, double[] b);

    short dist(short[] a, short[] b);

    float dist(float[] a, float[] b);

//    long dist(long[] a, long[] b);

//    int dist(int[] a, int[] b);

//    byte dist(byte[] a, byte[] b);
}
