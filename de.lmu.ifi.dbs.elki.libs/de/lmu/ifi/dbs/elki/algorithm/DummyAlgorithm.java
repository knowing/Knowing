package de.lmu.ifi.dbs.elki.algorithm;

import java.util.Iterator;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * Dummy Algorithm, which just iterates over all points once, doing a 10NN query
 * each. Useful in testing e.g. index structures and as template for custom
 * algorithms.
 * 
 * @author Erich Schubert
 * @param <V> Vector type
 */
@Title("Dummy Algorithm")
@Description("The algorithm executes a 10NN query on all data points, and can be used in unit testing")
public class DummyAlgorithm<V extends NumberVector<V, ?>> extends AbstractAlgorithm<V, Result> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(DummyAlgorithm.class);

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   */
  public DummyAlgorithm() {
    super();
  }

  /**
   * Iterates over all points in the database.
   */
  @Override
  protected Result runInTime(Database<V> database) throws IllegalStateException {
    DistanceQuery<V, DoubleDistance> distanceQuery = EuclideanDistanceFunction.STATIC.instantiate(database);
    for(Iterator<DBID> iter = database.iterator(); iter.hasNext();) {
      DBID id = iter.next();
      database.get(id);
      // run a 10NN query for each point.
      database.kNNQueryForID(id, 10, distanceQuery);
    }
    return null;
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }

  /**
   * Factory method for {@link Parameterizable}
   * 
   * @param config Parameterization
   * @return KNN outlier detection algorithm
   */
  public static <V extends NumberVector<V, ?>> DummyAlgorithm<V> parameterize(Parameterization config) {
    if(config.hasErrors()) {
      return null;
    }
    return new DummyAlgorithm<V>();
  }
}