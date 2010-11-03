package de.lmu.ifi.dbs.elki.algorithm.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.lmu.ifi.dbs.elki.algorithm.AbstractPrimitiveDistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.model.MeanModel;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.distance.distancefunction.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.PrimitiveDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.normalization.AttributeWiseMinMaxNormalization;
import de.lmu.ifi.dbs.elki.normalization.NonNumericFeaturesException;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.EmptyParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * Provides the k-means algorithm.
 * <p>
 * Reference: J. MacQueen: Some Methods for Classification and Analysis of
 * Multivariate Observations. <br>
 * In 5th Berkeley Symp. Math. Statist. Prob., Vol. 1, 1967, pp 281-297.
 * </p>
 * 
 * @author Arthur Zimek
 * @param <D> a type of {@link Distance} as returned by the used distance
 *        function
 * @param <V> a type of {@link NumberVector} as a suitable datatype for this
 *        algorithm
 */
@Title("K-Means")
@Description("Finds a partitioning into k clusters.")
@Reference(authors = "J. MacQueen", title = "Some Methods for Classification and Analysis of Multivariate Observations", booktitle = "5th Berkeley Symp. Math. Statist. Prob., Vol. 1, 1967, pp 281-297", url = "http://projecteuclid.org/euclid.bsmsp/1200512992")
public class KMeans<V extends NumberVector<V, ?>, D extends Distance<D>> extends AbstractPrimitiveDistanceBasedAlgorithm<V, D, Clustering<MeanModel<V>>> implements ClusteringAlgorithm<Clustering<MeanModel<V>>, V> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(KMeans.class);
  
  /**
   * Parameter to specify the number of clusters to find, must be an integer
   * greater than 0.
   */
  public static final OptionID K_ID = OptionID.getOrCreateOptionID("kmeans.k", "The number of clusters to find.");

  /**
   * Parameter to specify the number of clusters to find, must be an integer
   * greater or equal to 0, where 0 means no limit.
   */
  public static final OptionID MAXITER_ID = OptionID.getOrCreateOptionID("kmeans.maxiter", "The maximum number of iterations to do. 0 means no limit.");

  /**
   * Holds the value of {@link #K_ID}.
   */
  private int k;

  /**
   * Holds the value of {@link #MAXITER_ID}.
   */
  private int maxiter;

  /**
   * Constructor.
   * 
   * @param distanceFunction distance function
   * @param k k parameter
   * @param maxiter Maxiter parameter
   */
  public KMeans(PrimitiveDistanceFunction<? super V, D> distanceFunction, int k, int maxiter) {
    super(distanceFunction);
    this.k = k;
    this.maxiter = maxiter;
  }

  /**
   * Performs the k-means algorithm on the given database.
   */
  @Override
  protected Clustering<MeanModel<V>> runInTime(Database<V> database) throws IllegalStateException {
    Random random = new Random();
    if(database.size() > 0) {
      // needs normalization to ensure the randomly generated means
      // are in the same range as the vectors in the database
      // XXX perhaps this can be done more conveniently?
      V randomBase = database.get(database.iterator().next());
      EmptyParameterization parameters = new EmptyParameterization();
      AttributeWiseMinMaxNormalization<V> normalization = new AttributeWiseMinMaxNormalization<V>(parameters);
      for(ParameterException e : parameters.getErrors()) {
        logger.warning("Error in internal parameterization: " + e.getMessage());
      }
      List<V> list = new ArrayList<V>(database.size());
      for(DBID id : database) {
        list.add(database.get(id));
      }
      try {
        normalization.normalize(list);
      }
      catch(NonNumericFeaturesException e) {
        logger.warning(e.getMessage());
      }
      List<V> means = new ArrayList<V>(k);
      List<V> oldMeans;
      List<? extends ModifiableDBIDs> clusters;
      if(logger.isVerbose()) {
        logger.verbose("initializing random vectors");
      }
      for(int i = 0; i < k; i++) {
        V randomVector = randomBase.randomInstance(random);
        try {
          means.add(normalization.restore(randomVector));
        }
        catch(NonNumericFeaturesException e) {
          logger.warning(e.getMessage());
          means.add(randomVector);
        }
      }
      clusters = sort(means, database);
      boolean changed = true;
      int iteration = 1;
      while(changed) {
        if(logger.isVerbose()) {
          logger.verbose("iteration " + iteration);
        }
        oldMeans = new ArrayList<V>(means);
        means = means(clusters, means, database);
        clusters = sort(means, database);
        changed = !means.equals(oldMeans);
        iteration++;

        if(maxiter > 0 && iteration > maxiter) {
          break;
        }
      }
      Clustering<MeanModel<V>> result = new Clustering<MeanModel<V>>("k-Means Clustering", "kmeans-clustering");
      for(int i = 0; i < clusters.size(); i++) {
        DBIDs ids = clusters.get(i);
        MeanModel<V> model = new MeanModel<V>(means.get(i));
        result.addCluster(new Cluster<MeanModel<V>>(ids, model));
      }
      return result;
    }
    else {
      return new Clustering<MeanModel<V>>("k-Means Clustering", "kmeans-clustering");
    }
  }

  /**
   * Returns the mean vectors of the given clusters in the given database.
   * 
   * @param clusters the clusters to compute the means
   * @param means the recent means
   * @param database the database containing the vectors
   * @return the mean vectors of the given clusters in the given database
   */
  protected List<V> means(List<? extends ModifiableDBIDs> clusters, List<V> means, Database<V> database) {
    List<V> newMeans = new ArrayList<V>(k);
    for(int i = 0; i < k; i++) {
      ModifiableDBIDs list = clusters.get(i);
      V mean = null;
      for(Iterator<DBID> clusterIter = list.iterator(); clusterIter.hasNext();) {
        if(mean == null) {
          mean = database.get(clusterIter.next());
        }
        else {
          mean = mean.plus(database.get(clusterIter.next()));
        }
      }
      if(list.size() > 0) {
        assert mean != null;
        mean = mean.multiplicate(1.0 / list.size());
      }
      else {
        mean = means.get(i);
      }
      newMeans.add(mean);
    }
    return newMeans;
  }

  /**
   * Returns a list of clusters. The k<sup>th</sup> cluster contains the ids of
   * those FeatureVectors, that are nearest to the k<sup>th</sup> mean.
   * 
   * @param means a list of k means
   * @param database the database to cluster
   * @return list of k clusters
   */
  protected List<? extends ModifiableDBIDs> sort(List<V> means, Database<V> database) {
    List<ArrayModifiableDBIDs> clusters = new ArrayList<ArrayModifiableDBIDs>(k);
    for(int i = 0; i < k; i++) {
      clusters.add(DBIDUtil.newArray());
    }

    for(DBID id : database) {
      List<D> distances = new ArrayList<D>(k);
      V fv = database.get(id);
      int minIndex = 0;
      for(int d = 0; d < k; d++) {
        distances.add(getDistanceFunction().distance(fv, means.get(d)));
        if(distances.get(d).compareTo(distances.get(minIndex)) < 0) {
          minIndex = d;
        }
      }
      clusters.get(minIndex).add(id);
    }
    
    for(ArrayModifiableDBIDs cluster : clusters) {
      Collections.sort(cluster);
    }
    return clusters;
  }

  /**
   * Factory method for {@link Parameterizable}
   * 
   * @param config Parameterization
   * @return Clustering Algorithm
   */
  public static <D extends Distance<D>, V extends NumberVector<V, ?>> KMeans<V, D> parameterize(Parameterization config) {
    PrimitiveDistanceFunction<V, D> distanceFunction = getParameterDistanceFunction(config, EuclideanDistanceFunction.class, PrimitiveDistanceFunction.class);
    int k = 0;
    final IntParameter K_PARAM = new IntParameter(K_ID, new GreaterConstraint(0));
    if(config.grab(K_PARAM)) {
      k = K_PARAM.getValue();
    }
    int maxiter = 0;
    final IntParameter MAXITER_PARAM = new IntParameter(MAXITER_ID, new GreaterEqualConstraint(0), 0);
    if(config.grab(MAXITER_PARAM)) {
      maxiter = MAXITER_PARAM.getValue();
    }
    if(config.hasErrors()) {
      return null;
    }
    return new KMeans<V, D>(distanceFunction, k, maxiter);
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}