package de.lmu.ifi.dbs.elki.distance.distancefunction.timeseries;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;

/**
 * Provides the Edit Distance on Real Sequence distance for FeatureVectors.
 * 
 * @author Thomas Bernecker
 */
@Title("Edit Distance on Real Sequence")
@Reference(authors = "L. Chen and M. T. Özsu and V. Oria", title = "Robust and fast similarity search for moving object trajectories", booktitle = "SIGMOD '05: Proceedings of the 2005 ACM SIGMOD international conference on Management of data", url = "http://dx.doi.org/10.1145/1066157.1066213")
public class EDRDistanceFunction extends AbstractEditDistanceFunction {
  /**
   * OptionID for {@link #DELTA_PARAM}
   */
  public static final OptionID DELTA_ID = OptionID.getOrCreateOptionID("edr.delta", "the delta parameter (similarity threshold) for EDR (positive number)");

  /**
   * DELTA parameter
   */
  private final DoubleParameter DELTA_PARAM = new DoubleParameter(DELTA_ID, new GreaterEqualConstraint(0), 1.0);

  /**
   * Keeps the currently set delta.
   */
  private double delta;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public EDRDistanceFunction(Parameterization config) {
    super(config);
    config = config.descend(this);
    if(config.grab(DELTA_PARAM)) {
      delta = DELTA_PARAM.getValue();
    }
  }

  /**
   * Provides the Edit Distance on Real Sequence distance between the given two
   * vectors.
   * 
   * @return the Edit Distance on Real Sequence distance between the given two
   *         vectors as an instance of {@link DoubleDistance DoubleDistance}.
   */
  @Override
  public DoubleDistance distance(NumberVector<?,?> v1, NumberVector<?,?> v2) {

    double[][] matrix = new double[v1.getDimensionality()][v2.getDimensionality()];
    Step[][] steps = new Step[v1.getDimensionality()][v2.getDimensionality()];

    // size of edit distance band
    // bandsize is the maximum allowed distance to the diagonal
    int band = (int) Math.ceil(v2.getDimensionality() * bandSize);

    // System.out.println("len1: " + features1.length + ", len2: " +
    // features2.length + ", band: " + band);
    final double deltaValue = delta;

    for(int i = 0; i < v1.getDimensionality(); i++) {
      int l = i - (band + 1);
      if(l < 0) {
        l = 0;
      }
      int r = i + (band + 1);
      if(r > (v2.getDimensionality() - 1)) {
        r = (v2.getDimensionality() - 1);
      }

      for(int j = l; j <= r; j++) {
        if(Math.abs(i - j) <= band) {
          // compute squared distance
          double val1 = v1.doubleValue(i + 1);
          double val2 = v2.doubleValue(j + 1);
          double diff = (val1 - val2);
          final double d = Math.sqrt(diff * diff);

          final double cost;
          final Step step;

          final double subcost = (d <= deltaValue) ? 0 : 1;

          if((i + j) != 0) {
            if((i == 0) || ((j != 0) && (((matrix[i - 1][j - 1] + subcost) > (matrix[i][j - 1] + 1)) && ((matrix[i][j - 1] + 1) < (matrix[i - 1][j] + 1))))) {
              // del
              cost = matrix[i][j - 1] + 1;
              step = Step.DEL;
            }
            else if((j == 0) || ((i != 0) && (((matrix[i - 1][j - 1] + subcost) > (matrix[i - 1][j] + 1)) && ((matrix[i - 1][j] + 1) < (matrix[i][j - 1] + 1))))) {
              // ins
              cost = matrix[i - 1][j] + 1;
              step = Step.INS;
            }
            else {
              // match
              cost = matrix[i - 1][j - 1] + subcost;
              step = Step.MATCH;
            }
          }
          else {
            cost = 0;
            step = Step.MATCH;
          }

          matrix[i][j] = cost;
          steps[i][j] = step;
        }
        else {
          matrix[i][j] = Double.POSITIVE_INFINITY; // outside band
        }
      }
    }

    return new DoubleDistance(matrix[v1.getDimensionality() - 1][v2.getDimensionality() - 1]);
  }
}