package de.lmu.ifi.dbs.elki.math.linearalgebra.pca;

import java.util.Collection;
import java.util.Iterator;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;

/**
 * Abstract class with the task of computing a Covariance matrix to be used in PCA.
 * Mostly the specification of an interface.
 * 
 * @author Erich Schubert
 *
 * @param <V> Vector class in use
 * @param <D> Distance type
 */
public abstract class AbstractCovarianceMatrixBuilder<V extends NumberVector<?, ?>, D extends NumberDistance<D,?>> implements Parameterizable {
  /**
   * Compute Covariance Matrix for a complete database
   * 
   * @param database the database used
   * @return Covariance Matrix
   */
  public Matrix processDatabase(Database<? extends V> database) {
    return processIds(database.getIDs(), database);
  }

  /**
   * Compute Covariance Matrix for a collection of database IDs
   * 
   * @param ids a collection of ids
   * @param database the database used
   * @return Covariance Matrix
   */
  public abstract Matrix processIds(DBIDs ids, Database<? extends V> database);

  /**
   * Compute Covariance Matrix for a QueryResult Collection
   * 
   * By default it will just collect the ids and run processIds
   * 
   * @param results a collection of QueryResults
   * @param database the database used
   * @param k the number of entries to process
   * @return Covariance Matrix
   */
  public Matrix processQueryResults(Collection<DistanceResultPair<D>> results, Database<? extends V> database, int k) {
    ModifiableDBIDs ids = DBIDUtil.newArray(k);
    int have = 0;
    for(Iterator<DistanceResultPair<D>> it = results.iterator(); it.hasNext() && have < k; have++) {
      ids.add(it.next().getID());
    }
    return processIds(ids, database);
  }

  /**
   * Compute Covariance Matrix for a QueryResult Collection
   * 
   * By default it will just collect the ids and run processIds
   * 
   * @param results a collection of QueryResults
   * @param database the database used
   * @return Covariance Matrix
   */
  final public Matrix processQueryResults(Collection<DistanceResultPair<D>> results, Database<? extends V> database) {
    return processQueryResults(results, database, results.size());
  }
  
  // TODO: Allow KNNlist to avoid building the DBID array?
}