package de.lmu.ifi.dbs.elki.distance.distancefunction.timeseries;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * Provides the Dynamic Time Warping distance for FeatureVectors.
 * 
 * @author Thomas Bernecker
 */
@Title("Dynamic Time Warping Distance Function")
@Reference(authors = "Berndt, D. and Clifford, J.", title = "Using dynamic time warping to find patterns in time series", booktitle = "AAAI-94 Workshop on Knowledge Discovery in Databases, 1994", url = "http://www.aaai.org/Papers/Workshops/1994/WS-94-03/WS94-03-031.pdf")
public class DTWDistanceFunction extends AbstractEditDistanceFunction {
  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public DTWDistanceFunction(Parameterization config) {
    super(config);
    config = config.descend(this);
  }

  /**
   * Provides the Dynamic Time Warping distance between the given two vectors.
   * 
   * @return the Dynamic Time Warping distance between the given two vectors as
   *         an instance of {@link DoubleDistance DoubleDistance}.
   */
  @Override
  public DoubleDistance distance(NumberVector<?,?> v1, NumberVector<?,?> v2) {

    double[][] matrix = new double[v1.getDimensionality()][v2.getDimensionality()];
    Step[][] steps = new Step[v1.getDimensionality()][v2.getDimensionality()];

    // size of edit distance band
    int band = (int) Math.ceil(v2.getDimensionality() * bandSize);
    // bandsize is the maximum allowed distance to the diagonal

    // System.out.println("len1: " + features1.length + ", len2: " +
    // features2.length + ", band: " + band);

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
          double val1 = v1.doubleValue(i + 1);
          double val2 = v2.doubleValue(j + 1);
          double diff = (val1 - val2);
          //Formally: diff = Math.sqrt(diff * diff);

          double cost = diff * diff;
          final Step step;

          if((i + j) != 0) {
            if((i == 0) || ((j != 0) && ((matrix[i - 1][j - 1] > matrix[i][j - 1]) && (matrix[i][j - 1] < matrix[i - 1][j])))) {
              // del
              cost += matrix[i][j - 1];
              step = Step.DEL;
            }
            else if((j == 0) || ((i != 0) && ((matrix[i - 1][j - 1] > matrix[i - 1][j]) && (matrix[i - 1][j] < matrix[i][j - 1])))) {
              // ins
              cost += matrix[i - 1][j];
              step = Step.INS;
            }
            else {
              // match
              cost += matrix[i - 1][j - 1];
              step = Step.MATCH;
            }
          }
          else {
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

    DoubleDistance result = new DoubleDistance(Math.sqrt(matrix[v1.getDimensionality() - 1][v2.getDimensionality() - 1]));
    return result;
  }
}