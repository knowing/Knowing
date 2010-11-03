package de.lmu.ifi.dbs.elki.distance.distancefunction;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.database.query.PrimitiveDistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;

/**
 * AbstractDistanceFunction provides some methods valid for any extending class.
 * 
 * @author Arthur Zimek
 * @param <O> the type of DatabaseObject to compute the distances in between
 * @param <D> the type of Distance used
 */
public abstract class AbstractPrimitiveDistanceFunction<O extends DatabaseObject, D extends Distance<D>> implements PrimitiveDistanceFunction<O, D> {
  /**
   * Provides an abstract DistanceFunction.
   */
  public AbstractPrimitiveDistanceFunction() {
    // EMPTY
  }

  @Override
  abstract public D distance(O o1, O o2);

  @Override
  abstract public D getDistanceFactory();

  @Override
  public boolean isSymmetric() {
    // Assume symmetric by default!
    return true;
  }

  @Override
  public boolean isMetric() {
    // Do NOT assume triangle equation by default!
    return false;
  }

  /**
   * Instantiate with a database to get the actual distance query.
   * 
   * @param database
   * @return Actual distance query.
   */
  @Override
  public <T extends O> DistanceQuery<T, D> instantiate(Database<T> database) {
    return new PrimitiveDistanceQuery<T, D>(database, this);
  }
}