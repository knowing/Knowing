package de.lmu.ifi.dbs.elki.math.linearalgebra.pca.weightfunctions;

/**
 * Linear weight function, scaled using the maximum such that it goes from 1.0
 * to 0.1
 * 
 * 1 - 0.9 * (distance/max)
 * 
 * @author Erich Schubert
 */
public final class LinearWeight implements WeightFunction {
  /**
   * Linear decreasing weight, from 1.0 to 0.1. Stddev is ignored.
   */
  @Override
  public double getWeight(double distance, double max, @SuppressWarnings("unused") double stddev) {
    if(max <= 0) {
      return 1.0;
    }
    double relativedistance = distance / max;
    return 1 - relativedistance * .9;
  }
}
