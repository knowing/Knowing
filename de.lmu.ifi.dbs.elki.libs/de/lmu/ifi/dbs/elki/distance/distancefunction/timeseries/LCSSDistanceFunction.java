package de.lmu.ifi.dbs.elki.distance.distancefunction.timeseries;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.VectorUtil;
import de.lmu.ifi.dbs.elki.distance.distancefunction.AbstractPrimitiveDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.math.DoubleMinMax;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.IntervalConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.IntervalConstraint.IntervalBoundary;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;

/**
 * Provides the Longest Common Subsequence distance for FeatureVectors.
 * 
 * 
 * Adapted for Java, based on Matlab Code by Michalis Vlachos. Original
 * Copyright Notice:
 * 
 * BEGIN COPYRIGHT NOTICE
 * 
 * lcsMatching code -- (c) 2002 Michalis Vlachos
 * (http://www.cs.ucr.edu/~mvlachos)
 * 
 * This code is provided as is, with no guarantees except that bugs are almost
 * surely present. Published reports of research using this code (or a modified
 * version) should cite the article that describes the algorithm:
 * 
 * <p>
 * M. Vlachos, M. Hadjieleftheriou, D. Gunopulos, E. Keogh:<br />
 * Indexing Multi-Dimensional Time-Series with Support for Multiple Distance
 * Measures<br />
 * In Proc. of 9th SIGKDD, Washington, DC, 2003
 * </p>
 * 
 * Comments and bug reports are welcome. Email to mvlachos@cs.ucr.edu I would
 * also appreciate hearing about how you used this code, improvements that you
 * have made to it.
 * 
 * You are free to modify, extend or distribute this code, as long as this
 * copyright notice is included whole and unchanged.
 * 
 * END COPYRIGHT NOTICE
 * 
 * 
 * @author Thomas Bernecker
 */
@Title("Longest Common Subsequence distance function")
@Reference(authors = "M. Vlachos, M. Hadjieleftheriou, D. Gunopulos, E. Keogh", title = "Indexing Multi-Dimensional Time-Series with Support for Multiple Distance Measures", booktitle = "Proceedings of the ninth ACM SIGKDD international conference on Knowledge discovery and data mining", url = "http://dx.doi.org/10.1145/956750.956777")
public class LCSSDistanceFunction<V extends NumberVector<V, ?>> extends AbstractPrimitiveDistanceFunction<V, DoubleDistance> {
  protected enum Step {
    NONE, INS, DEL, MATCH
  }

  /**
   * OptionID for {@link #PDELTA_PARAM}
   */
  public static final OptionID PDELTA_ID = OptionID.getOrCreateOptionID("lcss.pDelta", "the allowed deviation in x direction for LCSS alignment (positive double value, 0 <= pDelta <= 1)");

  /**
   * OptionID for {@link #PEPSILON_PARAM}
   */
  public static final OptionID PEPSILON_ID = OptionID.getOrCreateOptionID("lcss.pEpsilon", "the allowed deviation in y directionfor LCSS alignment (positive double value, 0 <= pEpsilon <= 1)");

  /**
   * PDELTA parameter
   */
  private final DoubleParameter PDELTA_PARAM = new DoubleParameter(PDELTA_ID, new IntervalConstraint(0, IntervalBoundary.CLOSE, 1, IntervalBoundary.CLOSE), 0.1);

  /**
   * PEPSILON parameter
   */
  private final DoubleParameter PEPSILON_PARAM = new DoubleParameter(PEPSILON_ID, new IntervalConstraint(0, IntervalBoundary.CLOSE, 1, IntervalBoundary.CLOSE), 0.05);

  /**
   * Keeps the currently set pDelta.
   */
  private double pDelta;

  /**
   * Keeps the currently set pEpsilon.
   */
  private double pEpsilon;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public LCSSDistanceFunction(Parameterization config) {
    super();
    config = config.descend(this);
    if(config.grab(PDELTA_PARAM)) {
      pDelta = PDELTA_PARAM.getValue();
    }
    if(config.grab(PEPSILON_PARAM)) {
      pEpsilon = PEPSILON_PARAM.getValue();
    }
  }

  /**
   * Provides the Longest Common Subsequence distance between the given two
   * vectors.
   * 
   * @return the Longest Common Subsequence distance between the given two
   *         vectors as an instance of {@link DoubleDistance DoubleDistance}.
   */
  @Override
  public DoubleDistance distance(V v1, V v2) {

    final int delta = (int) Math.ceil(v2.getDimensionality() * pDelta);

    DoubleMinMax extrema1 = VectorUtil.getRangeDouble(v1);
    DoubleMinMax extrema2 = VectorUtil.getRangeDouble(v1);
    double range = Math.max(extrema1.getMax(), extrema2.getMax()) - Math.min(extrema1.getMin(), extrema2.getMin());
    final double epsilon = range * pEpsilon;

    int m = -1;
    int n = -1;
    double[] a, b;

    // put shorter vector first
    if(v1.getDimensionality() < v2.getDimensionality()) {
      m = v1.getDimensionality();
      n = v2.getDimensionality();
      a = new double[m];
      b = new double[n];

      for(int i = 0; i < v1.getDimensionality(); i++) {
        a[i] = v1.doubleValue(i + 1);
      }
      for(int j = 0; j < v2.getDimensionality(); j++) {
        b[j] = v2.doubleValue(j + 1);
      }
    }
    else {
      m = v2.getDimensionality();
      n = v1.getDimensionality();
      a = new double[m];
      b = new double[n];

      for(int i = 0; i < v2.getDimensionality(); i++) {
        a[i] = v2.doubleValue(i + 1);
      }
      for(int j = 0; j < v1.getDimensionality(); j++) {
        b[j] = v1.doubleValue(j + 1);
      }
    }

    double[][] matrix = new double[m + 1][n + 1];
    Step[][] steps = new Step[m + 1][n + 1];

    Step step;

    for(int i = 0; i < m; i++) {
      for(int j = (i - delta); j <= (i + delta); j++) {
        if(j < 0 || j >= n) {
          // do nothing;
        }
        else {
          if((b[j] + epsilon) >= a[i] & (b[j] - epsilon) <= a[i]) // match
          {
            matrix[i + 1][j + 1] = matrix[i][j] + 1;
            step = Step.MATCH;
          }
          else if(matrix[i][j + 1] > matrix[i + 1][j]) // ins
          {
            matrix[i + 1][j + 1] = matrix[i][j + 1];
            step = Step.INS;
          }
          else // del
          {
            matrix[i + 1][j + 1] = matrix[i + 1][j];
            step = Step.DEL;
          }

          steps[i][j] = step;
        }
      }
    }

    // search for maximum in the last line
    double maxEntry = -1;
    for(int i = 1; i < n + 1; i++) {
      if(matrix[m][i] > maxEntry) {
        maxEntry = matrix[m][i];
      }
    }
    double sim = maxEntry / Math.max(m, n); // FIXME: min instead of max????
    return new DoubleDistance(1 - sim);
  }

  @Override
  public Class<? super V> getInputDatatype() {
    return NumberVector.class;
  }

  @Override
  public DoubleDistance getDistanceFactory() {
    return DoubleDistance.FACTORY;
  }
}