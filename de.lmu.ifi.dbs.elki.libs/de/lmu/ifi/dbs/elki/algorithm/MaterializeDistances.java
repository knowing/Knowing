package de.lmu.ifi.dbs.elki.algorithm;

import java.util.ArrayList;
import java.util.Collection;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.result.CollectionResult;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.pairs.CTriple;

/**
 * <p>
 * Algorithm to materialize all the distances in a data set.
 * </p>
 * 
 * <p>
 * The result can then be used with the DoubleDistanceParser and
 * MultipleFileInput to use cached distances.
 * </p>
 * 
 * <p>
 * Symmetry is assumed.
 * </p>
 * 
 * @author Erich Schubert
 * @param <O> Object type
 * @param <D> Distance type
 */
@Title("MaterializeDistances")
@Description("Materialize all distances in the data set to use as cached/precalculated data.")
public class MaterializeDistances<O extends DatabaseObject, D extends NumberDistance<D, ?>> extends AbstractDistanceBasedAlgorithm<O, D, CollectionResult<CTriple<DBID, DBID, Double>>> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(MaterializeDistances.class);
  
  /**
   * Constructor.
   * 
   * @param distanceFunction Parameterization
   */
  public MaterializeDistances(DistanceFunction<? super O, D> distanceFunction) {
    super(distanceFunction);
  }

  /**
   * Iterates over all points in the database.
   */
  @Override
  protected CollectionResult<CTriple<DBID, DBID, Double>> runInTime(Database<O> database) throws IllegalStateException {
    DistanceQuery<O, D> distFunc = getDistanceFunction().instantiate(database);
    int size = database.size();

    Collection<CTriple<DBID, DBID, Double>> r = new ArrayList<CTriple<DBID, DBID, Double>>(size * (size + 1) / 2);

    for(DBID id1 : database.getIDs()) {
      for(DBID id2 : database.getIDs()) {
        // skip inverted pairs
        if(id2.compareTo(id1) > 0) {
          continue;
        }
        double d = distFunc.distance(id1, id2).doubleValue();
        r.add(new CTriple<DBID, DBID, Double>(id1, id2, d));
      }
    }
    return new CollectionResult<CTriple<DBID, DBID, Double>>("Distance Matrix", "distance-matrix", r);
  }

  /**
   * Factory method for {@link Parameterizable}
   * 
   * @param config Parameterization
   * @return KNN outlier detection algorithm
   */
  public static <O extends DatabaseObject, D extends NumberDistance<D, ?>> MaterializeDistances<O, D> parameterize(Parameterization config) {
    DistanceFunction<O, D> distanceFunction = getParameterDistanceFunction(config);
    if(config.hasErrors()) {
      return null;
    }
    return new MaterializeDistances<O, D>(distanceFunction);
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}