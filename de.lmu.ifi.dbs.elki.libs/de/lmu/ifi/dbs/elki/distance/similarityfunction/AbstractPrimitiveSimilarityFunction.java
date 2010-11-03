package de.lmu.ifi.dbs.elki.distance.similarityfunction;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.query.PrimitiveSimilarityQuery;
import de.lmu.ifi.dbs.elki.database.query.SimilarityQuery;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;

/**
 * Base implementation of a similarity function.
 * 
 * @author Arthur Zimek
 * @param <O> object type
 * @param <D> distance type
 */
public abstract class AbstractPrimitiveSimilarityFunction<O extends DatabaseObject, D extends Distance<D>> implements PrimitiveSimilarityFunction<O, D> {
  /**
   * Constructor.
   */
  protected AbstractPrimitiveSimilarityFunction() {
    super();
  }

  @Override
  public boolean isSymmetric() {
    // Assume symmetric by default!
    return true;
  }

  @Override
  abstract public Class<? super O> getInputDatatype();

  @Override
  abstract public D similarity(O o1, O o2);

  @Override
  public <T extends O> SimilarityQuery<T, D> instantiate(Database<T> database) {
    return new PrimitiveSimilarityQuery<T, D>(database, this);
  }
}