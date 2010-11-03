package de.lmu.ifi.dbs.elki.math.statistics;

import java.util.List;

import de.lmu.ifi.dbs.elki.utilities.pairs.DoubleDoublePair;

/**
 * 
 * @author Arthur Zimek
 */
// TODO: arthur comment
public class LinearRegression {
  private double t;

  private double m;

  /**
   * @param points Points to process
   */
  public LinearRegression(List<DoubleDoublePair> points) {
    double sumy = 0.0;
    double sumx = 0.0;
    double sumyy = 0.0;
    double sumxx = 0.0;
    double sumxy = 0.0;
    int gap = 0;
    for(DoubleDoublePair point : points) {
      sumy += point.getSecond();
      sumyy += point.getSecond() * point.getSecond();
      gap++;
      sumx += point.getFirst();
      sumxx += point.getFirst() * point.getFirst();
      sumxy += point.getFirst() * point.getSecond();
    }
    double Sxy = sumxy - sumx * sumy / gap;
    double Sxx = sumxx - sumx * sumx / gap;
    m = Sxy / Sxx;
    t = (sumy - m * sumx) / gap;
  }

  /**
   * @return m
   */
  public double getM() {
    return this.m;
  }

  /**
   * @return t
   */
  public double getT() {
    return this.t;
  }
}