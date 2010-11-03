package de.lmu.ifi.dbs.elki.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.lmu.ifi.dbs.elki.data.ClassLabel;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.data.FeatureVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.datastore.WritableDataStore;
import de.lmu.ifi.dbs.elki.database.ids.ArrayDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDFactory;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.TreeSetModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.database.query.PrimitiveDistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.result.AnnotationBuiltins;
import de.lmu.ifi.dbs.elki.result.AnyResult;
import de.lmu.ifi.dbs.elki.result.IDResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.datastructures.KNNHeap;
import de.lmu.ifi.dbs.elki.utilities.exceptions.ExceptionMessages;
import de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Provides a mapping for associations based on a Hashtable and functions to get
 * the next usable ID for insertion, making IDs reusable after deletion of the
 * entry.
 * 
 * @author Arthur Zimek
 * @param <O> the type of DatabaseObject as element of the database
 */
public abstract class AbstractDatabase<O extends DatabaseObject> implements Database<O> {
  /**
   * Map to hold the objects of the database.
   */
  private WritableDataStore<O> content;

  /**
   * Map to hold the object labels
   */
  private WritableDataStore<String> objectlabels = null;

  /**
   * Map to hold the class labels
   */
  private WritableDataStore<ClassLabel> classlabels = null;

  /**
   * Map to hold the external ids
   */
  private WritableDataStore<String> externalids = null;

  /**
   * Holds the listener of this database.
   */
  protected List<DatabaseListener<O>> listenerList = new ArrayList<DatabaseListener<O>>();

  /**
   * IDs of this database
   */
  private TreeSetModifiableDBIDs ids;

  /**
   * Object factory
   */
  private O objectFactory;

  /**
   * Collection of primary results.
   */
  final Collection<AnyResult> primaryResults;

  /**
   * Collection of derived results.
   */
  final Collection<AnyResult> derivedResults;

  /**
   * Abstract database including lots of common functionality.
   */
  protected AbstractDatabase() {
    super();
    this.ids = DBIDUtil.newTreeSet();
    this.content = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_DB, DatabaseObject.class);
    this.primaryResults = new java.util.Vector<AnyResult>(4);
    this.derivedResults = new java.util.Vector<AnyResult>();
    this.primaryResults.add(new IDResult());
  }

  /**
   * Calls for each object {@link #doInsert(Pair)} and notifies the listeners
   * about the new insertions.
   * 
   * @throws UnableToComplyException if database reached limit of storage
   *         capacity
   */
  @Override
  public DBIDs insert(List<Pair<O, DatabaseObjectMetadata>> objectsAndAssociationsList) throws UnableToComplyException {
    if(objectsAndAssociationsList.isEmpty()) {
      return DBIDUtil.EMPTYDBIDS;
    }
    // insert into db
    DBIDs ids = doInsert(objectsAndAssociationsList);
    // notify listeners
    fireObjectsInserted(ids);
    return ids;
  }

  /**
   * Calls {@link #doInsert(Pair)} and notifies the listeners about the new
   * insertion.
   * 
   * @throws UnableToComplyException if database reached limit of storage
   *         capacity
   */
  @Override
  public DBID insert(Pair<O, DatabaseObjectMetadata> objectAndAssociations) throws UnableToComplyException {
    // insert into db
    DBID id = doInsert(objectAndAssociations);
    // notify listeners
    fireObjectsInserted(id);

    return id;
  }

  /**
   * Inserts the given object into the database.
   * 
   * @param objectAndAssociations the object and its associations to be inserted
   * @return the ID assigned to the inserted object
   * @throws UnableToComplyException if database reached limit of storage
   *         capacity
   */
  protected DBID doInsert(Pair<O, DatabaseObjectMetadata> objectAndAssociations) throws UnableToComplyException {
    O object = objectAndAssociations.getFirst();
    // insert object
    DBID id = setNewID(object);
    content.put(id, object);
    ids.add(id);
    // insert associations
    DatabaseObjectMetadata associations = objectAndAssociations.getSecond();
    if(associations != null) {
      if(associations.objectlabel != null) {
        setObjectLabel(id, associations.objectlabel);
      }
      if(associations.classlabel != null) {
        setClassLabel(id, associations.classlabel);
      }
      if(associations.externalid != null) {
        setExternalID(id, associations.externalid);
      }
    }

    return id;
  }

  /**
   * Inserts the given objects into the database.
   * 
   * @param objectsAndAssociationsList the list of objects and their
   *        associations to be inserted
   * @return the IDs assigned to the inserted objects
   * @throws UnableToComplyException if database reached limit of storage
   *         capacity
   */
  protected DBIDs doInsert(List<Pair<O, DatabaseObjectMetadata>> objectsAndAssociationsList) throws UnableToComplyException {
    ModifiableDBIDs ids = DBIDUtil.newArray(objectsAndAssociationsList.size());
    for(Pair<O, DatabaseObjectMetadata> objectAndAssociations : objectsAndAssociationsList) {
      DBID id = doInsert(objectAndAssociations);
      ids.add(id);
    }
    return ids;
  }

  /**
   * Searches for all equal objects in the database and calls {@link #delete}
   * for each.
   */
  @Override
  public void delete(O object) {
    // Try via ID first.
    {
      DBID oid = object.getID();
      O stored = content.get(oid);
      if(stored != null && stored.equals(object)) {
        delete(oid);
        return;
      }
    }
    // Otherwise: scan database.
    for(DBID id : ids) {
      if(content.get(id).equals(object)) {
        delete(id);
      }
    }
  }

  /**
   * Calls {@link #doDelete} and notifies the listeners about the deletion.
   */
  @Override
  public O delete(DBID id) {
    if(get(id) == null) {
      return null;
    }
    O object = doDelete(id);
    // notify listeners
    fireObjectsRemoved(id);
    return object;
  }

  /**
   * Removes and returns the object with the given id from the database.
   * 
   * @param id the id of the object to be removed from the database
   * @return the object that has been removed
   */
  private O doDelete(DBID id) {
    O object = content.get(id);
    ids.remove(id);
    content.delete(id);
    if(objectlabels != null) {
      objectlabels.delete(id);
    }
    if(classlabels != null) {
      classlabels.delete(id);
    }
    if(externalids != null) {
      externalids.delete(id);
    }

    restoreID(id);
    return object;
  }

  @Override
  public final int size() {
    return ids.size();
  }

  @Override
  public O getObjectFactory() {
    if(objectFactory == null) {
      throw new UnsupportedOperationException("No object factory / project was added to the database.");
    }
    return objectFactory;
  }

  @Override
  public void setObjectFactory(O objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  public final O get(DBID id) {
    try {
      return content.get(id);
    }
    catch(RuntimeException e) {
      if(id == null) {
        throw new UnsupportedOperationException("AbstractDatabase.get(null) called!");
      }
      // throw e upwards.
      throw e;
    }
  }

  /**
   * Returns a list of all ids currently in use in the database.
   * 
   * The list is not affected of any changes made to the database in the future
   * nor vice versa.
   * 
   * @see de.lmu.ifi.dbs.elki.database.Database#getIDs()
   */
  @Override
  public DBIDs getIDs() {
    return DBIDUtil.makeUnmodifiable(ids);
  }

  /**
   * Returns an iterator iterating over all keys of the database.
   * 
   * @return an iterator iterating over all keys of the database
   */
  @Override
  public final Iterator<DBID> iterator() {
    return getIDs().iterator();
  }

  /** {@inheritDoc} */
  @Override
  public ClassLabel getClassLabel(DBID id) {
    if(classlabels == null) {
      return null;
    }
    return classlabels.get(id);
  }

  /** {@inheritDoc} */
  @Override
  public String getExternalID(DBID id) {
    if(externalids == null) {
      return null;
    }
    return externalids.get(id);
  }

  /** {@inheritDoc} */
  @Override
  public String getObjectLabel(DBID id) {
    if(objectlabels == null) {
      return null;
    }
    return objectlabels.get(id);
  }

  /** {@inheritDoc} */
  @Override
  public void setClassLabel(DBID id, ClassLabel label) {
    if(classlabels == null) {
      classlabels = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_DB, ClassLabel.class);
      primaryResults.add(new AnnotationBuiltins.ClassLabelAnnotation(this));
    }
    classlabels.put(id, label);
  }

  /** {@inheritDoc} */
  @Override
  public void setExternalID(DBID id, String externalid) {
    if(externalids == null) {
      externalids = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_DB, String.class);
      primaryResults.add(new AnnotationBuiltins.ExternalIDAnnotation(this));
    }
    externalids.put(id, externalid);
  }

  /** {@inheritDoc} */
  @Override
  public void setObjectLabel(DBID id, String label) {
    if(objectlabels == null) {
      objectlabels = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_DB, String.class);
      primaryResults.add(new AnnotationBuiltins.ObjectLabelAnnotation(this));
    }
    objectlabels.put(id, label);
  }

  /**
   * Provides a new id for the specified database object suitable as key for a
   * new insertion and sets this id in the specified database object.
   * 
   * @param object the object for which a new id should be provided
   * @return a new id suitable as key for a new insertion
   * @throws UnableToComplyException if the database has reached the limit and,
   *         therefore, new insertions are not possible
   */
  protected DBID setNewID(O object) throws UnableToComplyException {
    if(object.getID() != null) {
      if(ids.contains(object.getID())) {
        throw new UnableToComplyException("ID " + object.getID() + " is already in use!");
      }
      // TODO: register ID with id manager?
      return object.getID();
    }

    DBID id = DBIDFactory.FACTORY.generateSingleDBID();
    object.setID(id);
    return id;
  }

  /**
   * Makes the given id reusable for new insertion operations.
   * 
   * @param id the id to become reusable
   */
  protected void restoreID(final DBID id) {
    DBIDFactory.FACTORY.deallocateSingleDBID(id);
  }

  @Override
  public Database<O> partition(DBIDs ids) throws UnableToComplyException {
    Map<Integer, DBIDs> partitions = new HashMap<Integer, DBIDs>();
    partitions.put(0, ids);
    return partition(partitions, null, null).get(0);
  }

  @Override
  public Map<Integer, Database<O>> partition(Map<Integer, ? extends DBIDs> partitions) throws UnableToComplyException {
    return partition(partitions, null, null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<Integer, Database<O>> partition(Map<Integer, ? extends DBIDs> partitions, Class<? extends Database<O>> dbClass, Collection<Pair<OptionID, Object>> dbParameters) throws UnableToComplyException {
    if(dbClass == null) {
      dbClass = ClassGenericsUtil.uglyCrossCast(this.getClass(), Database.class);
      dbParameters = getParameters();
    }

    Map<Integer, Database<O>> databases = new Hashtable<Integer, Database<O>>();
    for(Integer partitionID : partitions.keySet()) {
      List<Pair<O, DatabaseObjectMetadata>> objectAndAssociationsList = new ArrayList<Pair<O, DatabaseObjectMetadata>>();
      DBIDs ids = partitions.get(partitionID);
      for(DBID id : ids) {
        O object = get(id);
        DatabaseObjectMetadata associations = new DatabaseObjectMetadata(this, id);
        objectAndAssociationsList.add(new Pair<O, DatabaseObjectMetadata>(object, associations));
      }

      Database<O> database;
      ListParameterization config = new ListParameterization(dbParameters);
      try {
        database = ClassGenericsUtil.tryInstantiate(Database.class, dbClass, config);
      }
      catch(Exception e) {
        throw new UnableToComplyException(e);
      }

      database.insert(objectAndAssociationsList);
      databases.put(partitionID, database);
    }
    return databases;
  }

  protected abstract Collection<Pair<OptionID, Object>> getParameters();

  @Override
  public final DBIDs randomSample(int k, long seed) {
    if(k < 0 || k > this.size()) {
      throw new IllegalArgumentException("Illegal value for size of random sample: " + k);
    }

    ModifiableDBIDs sample = DBIDUtil.newHashSet(k);
    ArrayModifiableDBIDs aids = DBIDUtil.newArray(this.ids);
    Random random = new Random(seed);
    // FIXME: Never sample the same two objects?
    while(sample.size() < k) {
      sample.add(aids.get(random.nextInt(aids.size())));
    }
    return sample;
  }

  @Override
  public int dimensionality() throws UnsupportedOperationException {
    Iterator<DBID> iter = this.iterator();
    if(iter.hasNext()) {
      O entry = this.get(iter.next());
      if(FeatureVector.class.isInstance(entry)) {
        return ((FeatureVector<?, ?>) entry).getDimensionality();
      }
      else {
        throw new UnsupportedOperationException("Database entries are not implementing interface " + NumberVector.class.getName() + ".");
      }
    }
    else {
      throw new UnsupportedOperationException(ExceptionMessages.DATABASE_EMPTY);
    }
  }

  /**
   * Helper method to extract the list of database objects from the specified
   * list of objects and their associations.
   * 
   * @param objectAndAssociationsList the list of objects and their associations
   * @return the list of database objects
   */
  protected List<O> getObjects(List<Pair<O, DatabaseObjectMetadata>> objectAndAssociationsList) {
    List<O> objects = new ArrayList<O>(objectAndAssociationsList.size());
    for(Pair<O, DatabaseObjectMetadata> objectAndAssociations : objectAndAssociationsList) {
      objects.add(objectAndAssociations.getFirst());
    }
    return objects;
  }

  @Override
  public <D extends Distance<D>> DistanceQuery<O, D> getDistanceQuery(DistanceFunction<? super O, D> distanceFunction) {
    return distanceFunction.instantiate(this);
  }

  /**
   * Retrieves the k-nearest neighbors (kNN) for the query object performing a
   * sequential scan on this database. The kNN are determined by trying to add
   * each object to a {@link KNNHeap}.
   */
  protected <D extends Distance<D>> List<DistanceResultPair<D>> sequentialkNNQueryForObject(O queryObject, int k, DistanceQuery<O, D> distanceFunction) {
    KNNHeap<D> heap = new KNNHeap<D>(k);
    for(DBID candidateID : this) {
      O candidate = get(candidateID);
      heap.add(new DistanceResultPair<D>(distanceFunction.distance(queryObject, candidate), candidateID));
    }
    return heap.toSortedArrayList();
  }

  /**
   * Retrieves the k-nearest neighbors (kNN) for the query object performing a
   * sequential scan on this database. The kNN are determined by trying to add
   * each object to a {@link KNNHeap}.
   */
  protected <D extends Distance<D>> List<DistanceResultPair<D>> sequentialkNNQueryForID(DBID id, int k, DistanceQuery<O, D> distanceFunction) {
    KNNHeap<D> heap = new KNNHeap<D>(k);
    for(DBID candidateID : this) {
      heap.add(new DistanceResultPair<D>(distanceFunction.distance(id, candidateID), candidateID));
    }
    return heap.toSortedArrayList();
  }

  /**
   * Retrieves the k-nearest neighbors (kNN) for the query objects performing
   * one sequential scan on this database. For each query id a {@link KNNHeap}
   * is assigned. The kNNs are determined by trying to add each object to all
   * KNNHeap.
   */
  protected <D extends Distance<D>> List<List<DistanceResultPair<D>>> sequentialBulkKNNQueryForID(ArrayDBIDs ids, int k, DistanceQuery<O, D> distanceFunction) {
    List<KNNHeap<D>> heaps = new ArrayList<KNNHeap<D>>(ids.size());
    for(int i = 0; i < ids.size(); i++) {
      heaps.add(new KNNHeap<D>(k));
    }

    if(PrimitiveDistanceQuery.class.isAssignableFrom(distanceFunction.getClass())) {
      // The distance is computed on arbitrary vectors, we can reduce object
      // loading by working on the actual vectors.
      for(DBID candidateID : this) {
        O candidate = get(candidateID);
        Integer index = -1;
        for(DBID id : ids) {
          index++;
          O object = get(id);
          KNNHeap<D> heap = heaps.get(index);
          heap.add(new DistanceResultPair<D>(distanceFunction.distance(object, candidate), candidateID));
        }
      }
    }
    else {
      // The distance is computed on database IDs
      for(DBID candidateID : this) {
        Integer index = -1;
        for(DBID id : ids) {
          index++;
          KNNHeap<D> heap = heaps.get(index);
          heap.add(new DistanceResultPair<D>(distanceFunction.distance(id, candidateID), candidateID));
        }
      }
    }

    List<List<DistanceResultPair<D>>> result = new ArrayList<List<DistanceResultPair<D>>>(heaps.size());
    for(KNNHeap<D> heap : heaps) {
      result.add(heap.toSortedArrayList());
    }
    return result;
  }

  @Override
  public <D extends Distance<D>> List<DistanceResultPair<D>> rangeQuery(DBID id, String epsilon, DistanceQuery<O, D> distanceFunction) {
    return rangeQuery(id, distanceFunction.getDistanceFactory().parseString(epsilon), distanceFunction);
  }

  /**
   * Retrieves the epsilon-neighborhood of the query object performing a
   * sequential scan on this database.
   */
  protected <D extends Distance<D>> List<DistanceResultPair<D>> sequentialRangeQuery(DBID id, D epsilon, DistanceQuery<O, D> distanceFunction) {
    List<DistanceResultPair<D>> result = new ArrayList<DistanceResultPair<D>>();
    for(DBID currentID : this) {
      D currentDistance = distanceFunction.distance(id, currentID);
      if(currentDistance.compareTo(epsilon) <= 0) {
        result.add(new DistanceResultPair<D>(currentDistance, currentID));
      }
    }
    Collections.sort(result);
    return result;
  }

  @Override
  public <D extends Distance<D>> List<DistanceResultPair<D>> rangeQueryForObject(O id, String epsilon, DistanceQuery<O, D> distanceFunction) {
    return rangeQueryForObject(id, distanceFunction.getDistanceFactory().parseString(epsilon), distanceFunction);
  }

  /**
   * Retrieves the epsilon-neighborhood of the query object performing a
   * sequential scan on this database.
   */
  protected <D extends Distance<D>> List<DistanceResultPair<D>> sequentialRangeQueryForObject(O obj, D epsilon, DistanceQuery<O, D> distanceFunction) {
    List<DistanceResultPair<D>> result = new ArrayList<DistanceResultPair<D>>();
    for(DBID currentID : this) {
      D currentDistance = distanceFunction.distance(currentID, obj);
      if(currentDistance.compareTo(epsilon) <= 0) {
        result.add(new DistanceResultPair<D>(currentDistance, currentID));
      }
    }
    Collections.sort(result);
    return result;
  }

  /**
   * Retrieves the reverse k-nearest neighbors (RkNN) for the query object by
   * performing a bulk knn query for all objects. If a query object is an
   * element of the kNN of an object o, o belongs to the particular query
   * result.
   */
  protected <D extends Distance<D>> List<List<DistanceResultPair<D>>> sequentialBulkReverseKNNQueryForID(ArrayDBIDs ids, int k, DistanceQuery<O, D> distanceFunction) {
    List<List<DistanceResultPair<D>>> rNNList = new ArrayList<List<DistanceResultPair<D>>>(ids.size());
    for(int i = 0; i < ids.size(); i++) {
      rNNList.add(new ArrayList<DistanceResultPair<D>>());
    }

    ArrayDBIDs allIDs = DBIDUtil.ensureArray(getIDs());
    List<List<DistanceResultPair<D>>> kNNList = bulkKNNQueryForID(allIDs, k, distanceFunction);

    int i = 0;
    for(DBID qid : allIDs) {
      List<DistanceResultPair<D>> knn = kNNList.get(i);
      for(DistanceResultPair<D> n : knn) {
        int j = 0;
        for(DBID id : ids) {
          if(n.getID() == id) {
            List<DistanceResultPair<D>> rNN = rNNList.get(j);
            rNN.add(new DistanceResultPair<D>(n.getDistance(), qid));
          }
          j++;
        }
      }
      i++;
    }
    for(int j = 0; j < ids.size(); j++) {
      List<DistanceResultPair<D>> rNN = rNNList.get(j);
      Collections.sort(rNN);
    }
    return rNNList;
  }

  /**
   * Adds a listener for the <code>DatabaseEvent</code> posted after the
   * database changes.
   * 
   * @param l the listener to add
   * @see #removeDatabaseListener
   */
  @Override
  public void addDatabaseListener(DatabaseListener<O> l) {
    listenerList.add(l);
  }

  /**
   * Removes a listener previously added with <code>addTreeModelListener</code>.
   * 
   * @param l the listener to remove
   * @see #addDatabaseListener
   */
  @Override
  public void removeDatabaseListener(DatabaseListener<O> l) {
    listenerList.remove(l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type.
   * 
   * @param objectIDs the ids of the database objects that have been removed
   */
  protected void fireObjectsChanged(DBIDs objectIDs) {
    if(listenerList.isEmpty()) {
      return;
    }
    DatabaseEvent<O> e = new DatabaseEvent<O>(this, objectIDs);
    for(DatabaseListener<O> listener : listenerList) {
      listener.objectsChanged(e);
    }
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type.
   * 
   * @param objectIDs the ids of the database objects that have been removed
   */
  protected void fireObjectsInserted(DBIDs objectIDs) {
    if(listenerList.isEmpty()) {
      return;
    }
    DatabaseEvent<O> e = new DatabaseEvent<O>(this, objectIDs);
    for(DatabaseListener<O> listener : listenerList) {
      listener.objectsInserted(e);
    }
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type.
   * 
   * @param objectIDs the ids of the database objects that have been removed
   */
  protected void fireObjectsRemoved(DBIDs objectIDs) {
    if(listenerList.isEmpty()) {
      return;
    }
    DatabaseEvent<O> e = new DatabaseEvent<O>(this, objectIDs);
    for(DatabaseListener<O> listener : listenerList) {
      listener.objectsRemoved(e);
    }
  }

  @Override
  public Collection<AnyResult> getPrimary() {
    return Collections.unmodifiableCollection(primaryResults);
  }

  @Override
  public Collection<AnyResult> getDerived() {
    return Collections.unmodifiableCollection(derivedResults);
  }

  @Override
  public void addDerivedResult(AnyResult r) {
    derivedResults.add(r);
  }

  @Override
  public String getLongName() {
    return "Database";
  }

  @Override
  public String getShortName() {
    return "database";
  }
}