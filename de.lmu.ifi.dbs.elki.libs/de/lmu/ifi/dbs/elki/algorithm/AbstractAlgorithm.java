package de.lmu.ifi.dbs.elki.algorithm;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.IndexDatabase;
import de.lmu.ifi.dbs.elki.database.query.KNNQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.result.AnyResult;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ChainedParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

/**
 * <p>
 * AbstractAlgorithm sets the values for flags verbose and time.
 * </p>
 * <p/>
 * <p>
 * This class serves also as a model of implementing an algorithm within this
 * framework. Any Algorithm that makes use of these flags may extend this class.
 * </p>
 * 
 * @author Arthur Zimek
 * @param <O> the type of DatabaseObjects handled by this Algorithm
 * @param <R> the type of result to retrieve from this Algorithm
 */
public abstract class AbstractAlgorithm<O extends DatabaseObject, R extends AnyResult> implements Algorithm<O, R> {
  /**
   * The kNN query type to use
   */
  public static final OptionID KNNQUERY_ID = OptionID.getOrCreateOptionID("knnquery", "kNN query class to use");
  
  /**
   * Constructor.
   */
  protected AbstractAlgorithm() {
    super();
  }

  /**
   * Calls the runInTime()-method of extending classes. Measures and prints the
   * runtime and, in case of an index based database, the I/O costs of this
   * method.
   * 
   * @param database the database to run the algorithm on
   * @return the Result computed by this algorithm
   */
  @Override
  public final R run(Database<O> database) throws IllegalStateException {
    long start = System.currentTimeMillis();
    R res = runInTime(database);
    long end = System.currentTimeMillis();
    if(getLogger().isVerbose()) {
      long elapsedTime = end - start;
      getLogger().verbose(this.getClass().getName() + " runtime  : " + elapsedTime + " milliseconds.");

    }
    if(database instanceof IndexDatabase<?> && getLogger().isVerbose()) {
      IndexDatabase<?> db = (IndexDatabase<?>) database;
      StringBuffer msg = new StringBuffer();
      msg.append(getClass().getName()).append(" physical read access : ").append(db.getPhysicalReadAccess()).append("\n");
      msg.append(getClass().getName()).append(" physical write access : ").append(db.getPhysicalWriteReadAccess()).append("\n");
      msg.append(getClass().getName()).append(" logical page access : ").append(db.getLogicalPageAccess()).append("\n");
      getLogger().verbose(msg.toString());
    }
    return res;
  }

  /**
   * The run method encapsulated in measure of runtime. An extending class needs
   * not to take care of runtime itself.
   * 
   * @param database the database to run the algorithm on
   * @return the Result computed by this algorithm
   * @throws IllegalStateException if the algorithm has not been initialized
   *         properly (e.g. the setParameters(String[]) method has been failed
   *         to be called).
   */
  protected abstract R runInTime(Database<O> database) throws IllegalStateException;

  /**
   * Get the (STATIC) logger for this class.
   * 
   * @return the static logger
   */
  abstract protected Logging getLogger();

  /**
   * Grab the distance configuration option.
   * 
   * @param <F> Distance function type
   * @param config Parameterization
   * @return Parameter value or null.
   */
  protected static <F extends DistanceFunction<?, ?>> F getParameterDistanceFunction(Parameterization config) {
    // Do NOT call the full getParameterDistanceFunctions, since this leads to JavaDoc compiler errors!
    final ObjectParameter<F> param = new ObjectParameter<F>(AbstractDistanceBasedAlgorithm.DISTANCE_FUNCTION_ID, DistanceFunction.class, EuclideanDistanceFunction.class);
    if(config.grab(param)) {
      return param.instantiateClass(config);
    }
    return null;
  }

  /**
   * Grab the distance function configuration option
   * 
   * @param <F> Distance function type
   * @param config Parameterization
   * @param defaultDistanceFunction Default value
   * @param restriction Restriction class
   * @return distance function
   */
  protected static <F extends DistanceFunction<?, ?>> F getParameterDistanceFunction(Parameterization config, Class<?> defaultDistanceFunction, Class<?> restriction) {
    final ObjectParameter<F> param = new ObjectParameter<F>(AbstractDistanceBasedAlgorithm.DISTANCE_FUNCTION_ID, restriction, defaultDistanceFunction);
    if(config.grab(param)) {
      return param.instantiateClass(config);
    }
    return null;
  }

  /**
   * Get a kNN query object
   * 
   * @param <O> Database object type
   * @param <D> Distance type
   * @param config Parameterization
   * @param k k parameter
   * @param distanceFunction distance function to use
   * @return kNN query object
   */
  protected static <O extends DatabaseObject, D extends Distance<D>> KNNQuery<O, D> getParameterKNNQuery(Parameterization config, int k, DistanceFunction<O, D> distanceFunction, Class<?> defaultClass) {
    KNNQuery<O, D> knnQuery = null;
    final ClassParameter<KNNQuery<O, D>> param = new ClassParameter<KNNQuery<O, D>>(KNNQUERY_ID, KNNQuery.class, defaultClass);
    // configure kNN query
    if(config.grab(param) && distanceFunction != null) {
      ListParameterization knnParams = new ListParameterization();
      knnParams.addParameter(KNNQuery.K_ID, k);
      if (distanceFunction != null) {
        knnParams.addParameter(KNNQuery.DISTANCE_FUNCTION_ID, distanceFunction);
      }
      ChainedParameterization chain = new ChainedParameterization(knnParams, config);
      chain.errorsTo(config);
      knnQuery = param.instantiateClass(chain);
      knnParams.reportInternalParameterizationErrors(config);
    }
    return knnQuery;
  }
}