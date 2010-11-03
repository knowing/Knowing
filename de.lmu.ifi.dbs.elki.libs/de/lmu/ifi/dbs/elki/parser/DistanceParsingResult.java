package de.lmu.ifi.dbs.elki.parser;

import java.util.List;
import java.util.Map;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Provides a list of database objects and labels associated with these objects
 * and a cache of precomputed distances between the database objects.
 * 
 * @author Elke Achtert
 * @param <O> object type
 * @param <D> distance type
 */
public class DistanceParsingResult<O extends DatabaseObject, D extends Distance<D>> extends ParsingResult<O> {
  /**
   * The cache of precomputed distances between the database objects.
   */
  private final Map<Pair<DBID, DBID>, D> distanceCache;

  /**
   * Provides a list of database objects, a list of label objects associated
   * with these objects and cached distances between these objects.
   * 
   * @param objectAndLabelList the list of database objects and labels
   *        associated with these objects
   * @param distanceCache the cache of precomputed distances between the
   *        database objects
   */
  public DistanceParsingResult(List<Pair<O, List<String>>> objectAndLabelList, Map<Pair<DBID, DBID>, D> distanceCache) {
    super(objectAndLabelList, null);
    this.distanceCache = distanceCache;
  }

  /**
   * Returns the cache of precomputed distances between the database objects.
   * 
   * @return the cache of precomputed distances between the database objects
   */
  public Map<Pair<DBID, DBID>, D> getDistanceCache() {
    return distanceCache;
  }
}