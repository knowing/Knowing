package de.lmu.ifi.dbs.elki.math.linearalgebra.pca;

import java.util.Collection;
import java.util.Iterator;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.distance.distancefunction.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.PrimitiveDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.pca.weightfunctions.ConstantWeight;
import de.lmu.ifi.dbs.elki.math.linearalgebra.pca.weightfunctions.WeightFunction;
import de.lmu.ifi.dbs.elki.utilities.DatabaseUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

/**
 * {@link AbstractCovarianceMatrixBuilder} with weights.
 * 
 * This builder uses a weight function to weight points differently during build
 * a covariance matrix. Covariance can be canonically extended with weights, as
 * shown in the article
 * 
 * A General Framework for Increasing the Robustness of PCA-Based Correlation
 * Clustering Algorithms Hans-Peter Kriegel and Peer Kr&ouml;ger and Erich
 * Schubert and Arthur Zimek In: Proc. 20th Int. Conf. on Scientific and
 * Statistical Database Management (SSDBM), 2008, Hong Kong Lecture Notes in
 * Computer Science 5069, Springer
 * 
 * @author Erich Schubert
 * 
 * @param <V> Vector class to use
 * @param <D> Distance type
 */
@Title("Weighted Covariance Matrix / PCA")
@Description("A PCA modification by using weights while building the covariance matrix, to obtain more stable results")
@Reference(authors = "H.-P. Kriegel, P. Kröger, E. Schubert, A. Zimek", title = "A General Framework for Increasing the Robustness of PCA-based Correlation Clustering Algorithms", booktitle="Proceedings of the 20th International Conference on Scientific and Statistical Database Management (SSDBM), Hong Kong, China, 2008", url="http://dx.doi.org/10.1007/978-3-540-69497-7_27")
public class WeightedCovarianceMatrixBuilder<V extends NumberVector<? extends V, ?>, D extends NumberDistance<D, ?>> extends AbstractCovarianceMatrixBuilder<V, D> {
  /**
   * OptionID for {@link #WEIGHT_PARAM}
   */
  public static final OptionID WEIGHT_ID = OptionID.getOrCreateOptionID("pca.weight", "Weight function to use in weighted PCA.");

  /**
   * Parameter to specify the weight function to use in weighted PCA, must
   * implement
   * {@link de.lmu.ifi.dbs.elki.math.linearalgebra.pca.weightfunctions.WeightFunction}
   * .
   * <p>
   * Key: {@code -pca.weight}
   * </p>
   */
  private final ObjectParameter<WeightFunction> WEIGHT_PARAM = new ObjectParameter<WeightFunction>(WEIGHT_ID, WeightFunction.class, ConstantWeight.class);

  /**
   * Holds the weight function.
   */
  public WeightFunction weightfunction;

  /**
   * Holds the distance function used for weight calculation
   */
  // TODO: make configureable
  private PrimitiveDistanceFunction<? super V, DoubleDistance> weightDistance = EuclideanDistanceFunction.STATIC;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public WeightedCovarianceMatrixBuilder(Parameterization config) {
    super();
    config = config.descend(this);
    if (config.grab(WEIGHT_PARAM)) {
      weightfunction = WEIGHT_PARAM.instantiateClass(config);
    }
  }

  /**
   * Weighted Covariance Matrix for a set of IDs. Since we are not supplied any
   * distance information, we'll need to compute it ourselves. Covariance is
   * tied to Euclidean distance, so it probably does not make much sense to add
   * support for other distance functions?
   */
  @Override
  public Matrix processIds(DBIDs ids, Database<? extends V> database) {
    int dim = database.dimensionality();
    // collecting the sums in each dimension
    double[] sums = new double[dim];
    // collecting the products of any two dimensions
    double[][] squares = new double[dim][dim];
    // for collecting weights
    double weightsum = 0.0;
    // get centroid
    V centroid = DatabaseUtil.centroid(database, ids);

    // find maximum distance
    double maxdist = 0.0;
    double stddev = 0.0;
    {
      for(Iterator<DBID> it = ids.iterator(); it.hasNext();) {
        V obj = database.get(it.next());
        double distance = weightDistance.distance(centroid, obj).doubleValue();
        stddev += distance * distance;
        if(distance > maxdist) {
          maxdist = distance;
        }
      }
      if(maxdist == 0.0) {
        maxdist = 1.0;
      }
      // compute standard deviation.
      stddev = Math.sqrt(stddev / ids.size());
    }

    int i = 0;
    for(Iterator<DBID> it = ids.iterator(); it.hasNext(); i++) {
      V obj = database.get(it.next());
      // TODO: hard coded distance... make parameterizable?
      double distance = 0.0;
      for(int d = 0; d < dim; d++) {
        double delta = centroid.doubleValue(d + 1) - obj.doubleValue(d + 1);
        distance += delta * delta;
      }
      distance = java.lang.Math.sqrt(distance);
      double weight = weightfunction.getWeight(distance, maxdist, stddev);
      for(int d1 = 0; d1 < dim; d1++) {
        /* We're exploiting symmetry here, start with d2 == d1 */
        for(int d2 = d1; d2 < dim; d2++) {
          squares[d1][d2] += obj.doubleValue(d1 + 1) * obj.doubleValue(d2 + 1) * weight;
        }
        sums[d1] += obj.doubleValue(d1 + 1) * weight;
      }
      weightsum += weight;
    }
    return new Matrix(finishCovarianceMatrix(sums, squares, weightsum));
  }

  /**
   * Compute Covariance Matrix for a QueryResult Collection
   * 
   * By default it will just collect the ids and run processIds
   * 
   * @param results a collection of QueryResults
   * @param database the database used
   * @param k number of elements to process
   * @return Covariance Matrix
   */
  @Override
  public Matrix processQueryResults(Collection<DistanceResultPair<D>> results, Database<? extends V> database, int k) {
    int dim = database.dimensionality();
    // collecting the sums in each dimension
    double[] sums = new double[dim];
    // collecting the products of any two dimensions
    double[][] squares = new double[dim][dim];
    // for collecting weights
    double weightsum = 0.0;

    // avoid bad parameters
    if(k > results.size()) {
      k = results.size();
    }

    // find maximum distance
    double maxdist = 0.0;
    double stddev = 0.0;
    {
      int i = 0;
      for(Iterator<DistanceResultPair<D>> it = results.iterator(); it.hasNext() && i < k; i++) {
        DistanceResultPair<D> res = it.next();
        double dist = res.getDistance().doubleValue();
        stddev += dist * dist;
        if(dist > maxdist) {
          maxdist = dist;
        }
      }
      if(maxdist == 0.0) {
        maxdist = 1.0;
      }
      stddev = Math.sqrt(stddev / k);
    }

    // calculate weighted PCA
    int i = 0;
    for(Iterator<DistanceResultPair<D>> it = results.iterator(); it.hasNext() && i < k; i++) {
      DistanceResultPair<D> res = it.next();
      V obj = database.get(res.getID());
      double weight = weightfunction.getWeight(res.getDistance().doubleValue(), maxdist, stddev);
      for(int d1 = 0; d1 < dim; d1++) {
        /* We're exploiting symmetry here, start with d2 == d1 */
        for(int d2 = d1; d2 < dim; d2++) {
          squares[d1][d2] += obj.doubleValue(d1 + 1) * obj.doubleValue(d2 + 1) * weight;
        }
        sums[d1] += obj.doubleValue(d1 + 1) * weight;
      }
      weightsum += weight;
    }
    return new Matrix(finishCovarianceMatrix(sums, squares, weightsum));
  }

  /**
   * Finish the Covariance matrix in array "squares".
   * 
   * @param sums Sums of values.
   * @param squares Sums of squares. Contents are destroyed and replaced with
   *        Covariance Matrix!
   * @param weightsum Sum of weights.
   * @return modified squares array
   */
  private double[][] finishCovarianceMatrix(double[] sums, double[][] squares, double weightsum) {
    if(weightsum > 0) {
      // reasonable weights - finish up matrix.
      for(int d1 = 0; d1 < sums.length; d1++) {
        for(int d2 = d1; d2 < sums.length; d2++) {
          squares[d1][d2] = squares[d1][d2] - sums[d1] * sums[d2] / weightsum;
          // use symmetry
          squares[d2][d1] = squares[d1][d2];
        }
      }
    }
    else {
      // No weights = no data. Use identity.
      // TODO: Warn about a bad weight function? Fail?
      for(int d1 = 0; d1 < sums.length; d1++) {
        for(int d2 = d1 + 1; d2 < sums.length; d2++) {
          squares[d1][d2] = 0;
        }
        squares[d1][d1] = 1;
      }
    }
    return squares;
  }
}
