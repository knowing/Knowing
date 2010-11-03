package de.lmu.ifi.dbs.elki.database.ids;

import de.lmu.ifi.dbs.elki.database.ids.integer.TrivialDBIDFactory;

/**
 * Factory interface for generating DBIDs. See {@link #FACTORY} for the static
 * instance to use.
 * 
 * @author Erich Schubert
 */
public interface DBIDFactory {
  /**
   * Static DBID factory to use.
   */
  public static DBIDFactory FACTORY = new TrivialDBIDFactory();
  
  /**
   * Import an integer ID
   * 
   * @param id Integer ID to import
   * @return DBID
   */
  public DBID importInteger(int id);

  /**
   * Generate a single DBID
   * 
   * @return A single DBID
   */
  public DBID generateSingleDBID();

  /**
   * Return a single DBID for reuse.
   * 
   * @param id DBID to deallocate
   */
  public void deallocateSingleDBID(DBID id);

  /**
   * Generate a static DBID range.
   * 
   * @param size Requested size
   * @return DBID range
   */
  public RangeDBIDs generateStaticDBIDRange(int size);

  /**
   * Deallocate a static DBID range.
   * 
   * @param range Range to deallocate
   */
  public void deallocateDBIDRange(RangeDBIDs range);

  /**
   * Make a DBID pair from two existing DBIDs.
   * 
   * @param first first DBID
   * @param second second DBID
   * 
   * @return new pair.
   */
  public DBIDPair makePair(DBID first, DBID second);
  
  /**
   * Make a new (modifiable) array of DBIDs.
   * 
   * @return New array
   */
  public ArrayModifiableDBIDs newArray();
  
  /**
   * Make a new (modifiable) hash set of DBIDs.
   * 
   * @return New hash set
   */
  public HashSetModifiableDBIDs newHashSet();
  
  /**
   * Make a new (modifiable) tree set of DBIDs.
   * 
   * @return New tree set
   */
  public TreeSetModifiableDBIDs newTreeSet();
  
  /**
   * Make a new (modifiable) array of DBIDs.
   * 
   * @param size Size hint
   * @return New array
   */
  public ArrayModifiableDBIDs newArray(int size);
  
  /**
   * Make a new (modifiable) hash set of DBIDs.
   * 
   * @param size Size hint
   * @return New hash set
   */
  public HashSetModifiableDBIDs newHashSet(int size);
  
  /**
   * Make a new (modifiable) tree set of DBIDs.
   * 
   * @param size Size hint
   * @return New tree set
   */
  public TreeSetModifiableDBIDs newTreeSet(int size);
  
  /**
   * Make a new (modifiable) array of DBIDs.
   * 
   * @param existing existing DBIDs to use
   * @return New array
   */
  public ArrayModifiableDBIDs newArray(DBIDs existing);
  
  /**
   * Make a new (modifiable) hash set of DBIDs.
   * 
   * @param existing existing DBIDs to use
   * @return New hash set
   */
  public HashSetModifiableDBIDs newHashSet(DBIDs existing);
  
  /**
   * Make a new (modifiable) tree set of DBIDs.
   * 
   * @param existing existing DBIDs to use
   * @return New tree set
   */
  public TreeSetModifiableDBIDs newTreeSet(DBIDs existing);
}