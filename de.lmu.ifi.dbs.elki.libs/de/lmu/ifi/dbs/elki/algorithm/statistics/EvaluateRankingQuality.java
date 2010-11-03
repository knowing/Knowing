package de.lmu.ifi.dbs.elki.algorithm.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.AbstractDistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.ByLabelClustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.evaluation.roc.ROC;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.math.AggregatingHistogram;
import de.lmu.ifi.dbs.elki.math.MathUtil;
import de.lmu.ifi.dbs.elki.math.MeanVariance;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.result.CollectionResult;
import de.lmu.ifi.dbs.elki.result.HistogramResult;
import de.lmu.ifi.dbs.elki.utilities.DatabaseUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.FCPair;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Evaluate a distance function with respect to kNN queries. For each point, the
 * neighbors are sorted by distance, then the ROC AUC is computed. A score of 1
 * means that the distance function provides a perfect ordering of relevant
 * neighbors first, then irrelevant neighbors. A value of 0.5 can be obtained by
 * random sorting. A value of 0 means the distance function is inverted, i.e. a
 * similarity.
 * 
 * TODO: Allow fixed binning range, configurable
 * 
 * TODO: Add sampling
 * 
 * @author Erich Schubert
 * @param <V> Vector type
 * @param <D> Distance type
 */
@Title("Evaluate Ranking Quality")
@Description("Evaluates the effectiveness of a distance function via the obtained rankings.")
public class EvaluateRankingQuality<V extends NumberVector<V, ?>, D extends NumberDistance<D, ?>> extends AbstractDistanceBasedAlgorithm<V, D, CollectionResult<DoubleVector>> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(EvaluateRankingQuality.class);
  
  /**
   * Option to configure the number of bins to use.
   */
  public static final OptionID HISTOGRAM_BINS_ID = OptionID.getOrCreateOptionID("rankqual.bins", "Number of bins to use in the histogram");

  /**
   * Constructor.
   * 
   * @param distanceFunction
   * @param numbins
   */
  public EvaluateRankingQuality(DistanceFunction<? super V, D> distanceFunction, int numbins) {
    super(distanceFunction);
    this.numbins = numbins;
  }

  /**
   * Number of bins to use.
   */
  int numbins = 20;

  /**
   * Run the algorithm.
   */
  @Override
  protected HistogramResult<DoubleVector> runInTime(Database<V> database) throws IllegalStateException {
    DistanceQuery<V, D> distFunc = getDistanceFunction().instantiate(database);

    // local copy, not entirely necessary. I just like control, guaranteed
    // sequences and stable+efficient array index -> id lookups.
    ArrayModifiableDBIDs ids = DBIDUtil.newArray(database.getIDs());
    int size = ids.size();

    if(logger.isVerbose()) {
      logger.verbose("Preprocessing clusters...");
    }
    // Cluster by labels
    ByLabelClustering<V> splitter = new ByLabelClustering<V>();
    Collection<Cluster<Model>> split = splitter.run(database).getAllClusters();

    // Compute cluster averages and covariance matrix
    HashMap<Cluster<?>, V> averages = new HashMap<Cluster<?>, V>(split.size());
    HashMap<Cluster<?>, Matrix> covmats = new HashMap<Cluster<?>, Matrix>(split.size());
    for(Cluster<?> clus : split) {
      averages.put(clus, DatabaseUtil.centroid(database, clus.getIDs()));
      covmats.put(clus, DatabaseUtil.covarianceMatrix(database, clus.getIDs()));
    }

    AggregatingHistogram<MeanVariance, Double> hist = AggregatingHistogram.MeanVarianceHistogram(numbins, 0.0, 1.0);

    if(logger.isVerbose()) {
      logger.verbose("Processing points...");
    }
    FiniteProgress rocloop = logger.isVerbose() ? new FiniteProgress("Computing ROC AUC values", size, logger) : null;

    // sort neighbors
    for(Cluster<?> clus : split) {
      ArrayList<FCPair<Double, DBID>> cmem = new ArrayList<FCPair<Double, DBID>>(clus.size());
      Vector av = averages.get(clus).getColumnVector();
      Matrix covm = covmats.get(clus);

      for(DBID i1 : clus.getIDs()) {
        Double d = MathUtil.mahalanobisDistance(covm, av.minus(database.get(i1).getColumnVector()));
        cmem.add(new FCPair<Double, DBID>(d, i1));
      }
      Collections.sort(cmem);

      for(int ind = 0; ind < cmem.size(); ind++) {
        DBID i1 = cmem.get(ind).getSecond();
        List<DistanceResultPair<D>> knn = database.kNNQueryForID(i1, size, distFunc);
        double result = ROC.computeROCAUCDistanceResult(size, clus, knn);

        hist.aggregate(((double) ind) / clus.size(), result);

        if(rocloop != null) {
          rocloop.incrementProcessed(logger);
        }
      }
    }
    if(rocloop != null) {
      rocloop.ensureCompleted(logger);
    }
    // Collections.sort(results);

    // Transform Histogram into a Double Vector array.
    Collection<DoubleVector> res = new ArrayList<DoubleVector>(size);
    for(Pair<Double, MeanVariance> pair : hist) {
      DoubleVector row = new DoubleVector(new double[] { pair.getFirst(), pair.getSecond().getCount(), pair.getSecond().getMean(), pair.getSecond().getVariance() });
      res.add(row);
    }
    return new HistogramResult<DoubleVector>("Ranking Quality Histogram", "ranking-histogram", res);
  }

  /**
   * Factory method for {@link Parameterizable}
   * 
   * @param config Parameterization
   * @return KNN outlier detection algorithm
   */
  public static <V extends NumberVector<V, ?>, D extends NumberDistance<D, ?>> EvaluateRankingQuality<V, D> parameterize(Parameterization config) {
    int bins = getParameterBins(config);

    DistanceFunction<V, D> distanceFunction = getParameterDistanceFunction(config);
    if(config.hasErrors()) {
      return null;
    }
    return new EvaluateRankingQuality<V, D>(distanceFunction, bins);
  }

  /**
   * Get the number of bins parameter
   * 
   * @param config Parameterization
   * @return bins parameter
   */
  protected static int getParameterBins(Parameterization config) {
    final IntParameter param = new IntParameter(HISTOGRAM_BINS_ID, new GreaterEqualConstraint(2), 20);
    if(config.grab(param)) {
      return param.getValue();
    }
    return -1;
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}