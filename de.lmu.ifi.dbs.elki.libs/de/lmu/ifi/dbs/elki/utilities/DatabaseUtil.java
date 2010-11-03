package de.lmu.ifi.dbs.elki.utilities;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import de.lmu.ifi.dbs.elki.data.ClassLabel;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.utilities.exceptions.ExceptionMessages;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Class with Database-related utility functions such as centroid computation,
 * covariances etc.
 * 
 * @author Erich Schubert
 */
public final class DatabaseUtil {
  /**
   * Returns the centroid as a NumberVector object of the specified objects
   * stored in the given database. The objects belonging to the specified ids
   * must be instance of <code>NumberVector</code>.
   * 
   * @param <V> Vector type
   * @param database the database storing the objects
   * @param ids the ids of the objects
   * @return the centroid of the specified objects stored in the given database
   * @throws IllegalArgumentException if the id list is empty
   */
  public static <V extends NumberVector<? extends V, ?>> V centroid(Database<? extends V> database, DBIDs ids) {
    if(ids.isEmpty()) {
      throw new IllegalArgumentException("Cannot compute a centroid, because of empty list of ids!");
    }

    int dim = database.dimensionality();
    double[] centroid = new double[dim];

    for(DBID id : ids) {
      V o = database.get(id);
      for(int j = 1; j <= dim; j++) {
        centroid[j - 1] += o.doubleValue(j);
      }
    }
    double size = ids.size();
    for(int i = 0; i < dim; i++) {
      centroid[i] /= size;
    }

    return database.getObjectFactory().newInstance(centroid);
  }

  /**
   * Returns the centroid w.r.t. the dimensions specified by the given BitSet as
   * a NumberVector object of the specified objects stored in the given
   * database. The objects belonging to the specified IDs must be instance of
   * <code>NumberVector</code>.
   * 
   * @param <V> Vector type
   * @param database the database storing the objects
   * @param ids the identifiable objects
   * @param dimensions the BitSet representing the dimensions to be considered
   * @return the centroid of the specified objects stored in the given database
   *         w.r.t. the specified subspace
   * @throws IllegalArgumentException if the id list is empty
   */
  public static <V extends NumberVector<V, ?>> V centroid(Database<V> database, DBIDs ids, BitSet dimensions) {
    if(ids.isEmpty()) {
      throw new IllegalArgumentException("Cannot compute a centroid, because of empty list of ids!");
    }

    int dim = database.dimensionality();
    double[] centroid = new double[dim];

    for(DBID id : ids) {
      V o = database.get(id);
      for(int d = dimensions.nextSetBit(0); d >= 0; d = dimensions.nextSetBit(d + 1)) {
        centroid[d] += o.doubleValue(d + 1);
      }
    }

    double size = ids.size();
    for(int i = 0; i < dim; i++) {
      centroid[i] /= size;
    }

    return database.getObjectFactory().newInstance(centroid);
  }

  /**
   * Returns the centroid w.r.t. the dimensions specified by the given BitSet as
   * a NumberVector object of the specified objects stored in the given
   * database. The objects belonging to the specified ids must be instance of
   * <code>NumberVector</code>.
   * 
   * @param <V> Vector type
   * @param database the database storing the objects
   * @param iter iterator over the identifiable objects
   * @param bitSet the bitSet specifying the dimensions to be considered
   * @return the centroid of the specified objects stored in the given database
   * @throws IllegalArgumentException if the id list is empty
   */
  public static <V extends NumberVector<V, ?>> V centroid(Database<V> database, Iterator<DBID> iter, BitSet bitSet) {
    if(!iter.hasNext()) {
      throw new IllegalArgumentException("Cannot compute a centroid, because of empty list of ids!");
    }

    int dim = database.dimensionality();
    double[] centroid = new double[dim];

    int size = 0;
    // we need to "cache" one o for the newInstance method, since we can't clone
    // the iterator.
    while(iter.hasNext()) {
      DBID id = iter.next();
      size++;
      V o = database.get(id);
      for(int j = 1; j <= dim; j++) {
        if(bitSet.get(j - 1)) {
          centroid[j - 1] += o.doubleValue(j);
        }
      }
    }

    for(int i = 0; i < dim; i++) {
      centroid[i] /= size;
    }

    return database.getObjectFactory().newInstance(centroid);
  }

  /**
   * Returns the centroid as a NumberVector object of the specified database.
   * The objects must be instance of <code>NumberVector</code>.
   * 
   * @param <V> Vector type
   * @param database the database storing the objects
   * @return the centroid of the specified objects stored in the given database
   * @throws IllegalArgumentException if the database is empty
   */
  public static <V extends NumberVector<V, ?>> V centroid(Database<V> database) {
    if(database == null || database.size() == 0) {
      throw new IllegalArgumentException(ExceptionMessages.DATABASE_EMPTY);
    }
    int dim = database.dimensionality();
    double[] centroid = new double[dim];

    Iterator<DBID> it = database.iterator();
    while(it.hasNext()) {
      V o = database.get(it.next());
      for(int j = 1; j <= dim; j++) {
        centroid[j - 1] += o.doubleValue(j);
      }
    }

    double size = database.size();
    for(int i = 0; i < dim; i++) {
      centroid[i] /= size;
    }
    return database.getObjectFactory().newInstance(centroid);
  }

  /**
   * Returns the centroid as a NumberVector object of the specified objects
   * stored in the given database. The objects belonging to the specified ids
   * must be instance of <code>NumberVector</code>.
   * 
   * @param database the database storing the objects
   * @param ids the ids of the objects
   * @return the centroid of the specified objects stored in the given database
   * @throws IllegalArgumentException if the id list is empty
   */
  private static NumberVector<?, ?> centroidRelaxed(Database<? extends NumberVector<?, ?>> database, DBIDs ids) {
    if(ids.isEmpty()) {
      throw new IllegalArgumentException("Cannot compute a centroid, because of empty list of ids!");
    }

    int dim = database.dimensionality();
    double[] centroid = new double[dim];

    for(DBID id : ids) {
      NumberVector<?, ?> o = database.get(id);
      for(int j = 1; j <= dim; j++) {
        centroid[j - 1] += o.doubleValue(j);
      }
    }
    double size = ids.size();
    for(int i = 0; i < dim; i++) {
      centroid[i] /= size;
    }

    // FIXME: This is an ugly generics hack...
    Database<DoubleVector> db = databaseUglyVectorCast(database);
    return db.getObjectFactory().newInstance(centroid);
  }

  /**
   * Returns the centroid as a NumberVector object of the specified database.
   * The objects must be instance of <code>NumberVector</code>.
   * 
   * @param database the database storing the objects
   * @return the centroid of the specified objects stored in the given database
   * @throws IllegalArgumentException if the database is empty
   */
  private static NumberVector<?, ?> centroidRelaxed(Database<? extends NumberVector<?, ?>> database) {
    if(database == null || database.size() == 0) {
      throw new IllegalArgumentException(ExceptionMessages.DATABASE_EMPTY);
    }
    int dim = database.dimensionality();
    double[] centroid = new double[dim];

    Iterator<DBID> it = database.iterator();
    while(it.hasNext()) {
      NumberVector<?, ?> o = database.get(it.next());
      for(int j = 1; j <= dim; j++) {
        centroid[j - 1] += o.doubleValue(j);
      }
    }

    double size = database.size();
    for(int i = 0; i < dim; i++) {
      centroid[i] /= size;
    }
    // FIXME: This is an ugly generics hack...
    Database<DoubleVector> db = databaseUglyVectorCast(database);
    return db.getObjectFactory().newInstance(centroid);
  }

  /**
   * Returns the centroid as a Vector object of the specified data matrix.
   * 
   * @param data the data matrix, where the data vectors are column vectors
   * @return the centroid of the specified data matrix
   */
  public static Vector centroid(Matrix data) {
    int d = data.getRowDimensionality();
    double n = data.getColumnDimensionality();
    double[] centroid = new double[d];

    for(int i = 0; i < n; i++) {
      Vector x = data.getColumnVector(i);
      for(int j = 0; j < d; j++) {
        centroid[j] += x.get(j);
      }
    }

    for(int j = 0; j < d; j++) {
      centroid[j] /= n;
    }

    return new Vector(centroid);
  }

  /**
   * Determines the covariance matrix of the objects stored in the given
   * database.
   * 
   * @param <V> Vector type
   * @param database the database storing the objects
   * @param ids the ids of the objects
   * @return the covariance matrix of the specified objects
   */
  public static <V extends NumberVector<? extends V, ?>> Matrix covarianceMatrix(Database<? extends V> database, DBIDs ids) {
    // centroid
    V centroid = centroid(database, ids);

    // covariance matrixArray
    int columns = centroid.getDimensionality();
    int rows = ids.size();

    double[][] matrixArray = new double[rows][columns];

    int i = 0;
    for(Iterator<DBID> it = ids.iterator(); it.hasNext(); i++) {
      NumberVector<?, ?> obj = database.get(it.next());
      for(int d = 0; d < columns; d++) {
        matrixArray[i][d] = obj.doubleValue(d + 1) - centroid.doubleValue(d + 1);
      }
    }
    Matrix centeredMatrix = new Matrix(matrixArray);
    return centeredMatrix.transposeTimes(centeredMatrix);
  }

  /**
   * Determines the covariance matrix of the objects stored in the given
   * database.
   * 
   * @param <V> Vector type
   * @param database the database storing the objects
   * @return the covariance matrix of the specified objects
   */
  public static <V extends NumberVector<?, ?>> Matrix covarianceMatrix(Database<? extends V> database) {
    // centroid
    NumberVector<?, ?> centroid = centroidRelaxed(database);

    return covarianceMatrix(database, centroid);
  }

  /**
   * <p>
   * Determines the covariance matrix of the objects stored in the given
   * database w.r.t. the given centroid.
   * </p>
   * 
   * @param <V> Vector type
   * @param database the database storing the objects
   * @param centroid the centroid of the database
   * @return the covariance matrix of the specified objects
   */
  public static <V extends NumberVector<?, ?>> Matrix covarianceMatrix(Database<? extends V> database, V centroid) {
    // centered matrix
    int columns = centroid.getDimensionality();
    int rows = database.size();
    double[][] matrixArray = new double[rows][columns];

    Iterator<DBID> it = database.iterator();
    int i = 0;
    while(it.hasNext()) {
      NumberVector<?, ?> obj = database.get(it.next());
      for(int d = 0; d < columns; d++) {
        matrixArray[i][d] = obj.doubleValue(d + 1) - centroid.doubleValue(d + 1);
      }
      i++;
    }
    Matrix centeredMatrix = new Matrix(matrixArray);
    // covariance matrix
    Matrix cov = centeredMatrix.transposeTimes(centeredMatrix);
    cov = cov.times(1.0 / database.size());

    return cov;
  }

  /**
   * Determines the d x d covariance matrix of the given n x d data matrix.
   * 
   * @param data the database storing the objects
   * @return the covariance matrix of the given data matrix.
   */
  public static Matrix covarianceMatrix(Matrix data) {
    // centroid
    Vector centroid = centroid(data);

    // centered matrix
    double[][] matrixArray = new double[data.getRowDimensionality()][data.getColumnDimensionality()];

    for(int i = 0; i < data.getRowDimensionality(); i++) {
      for(int j = 0; j < data.getColumnDimensionality(); j++) {
        matrixArray[i][j] = data.get(i, j) - centroid.get(i);
      }
    }
    Matrix centeredMatrix = new Matrix(matrixArray);
    // covariance matrix
    Matrix cov = centeredMatrix.timesTranspose(centeredMatrix);
    cov = cov.times(1.0 / data.getColumnDimensionality());

    return cov;
  }

  /**
   * Determines the variances in each dimension of all objects stored in the
   * given database.
   * 
   * @param database the database storing the objects
   * @return the variances in each dimension of all objects stored in the given
   *         database
   */
  public static double[] variances(Database<? extends NumberVector<?, ?>> database) {
    NumberVector<?, ?> centroid = centroidRelaxed(database);
    double[] variances = new double[centroid.getDimensionality()];

    for(int d = 1; d <= centroid.getDimensionality(); d++) {
      double mu = centroid.doubleValue(d);

      for(Iterator<DBID> it = database.iterator(); it.hasNext();) {
        NumberVector<?, ?> o = database.get(it.next());
        double diff = o.doubleValue(d) - mu;
        variances[d - 1] += diff * diff;
      }

      variances[d - 1] /= database.size();
    }
    return variances;
  }

  /**
   * Determines the variances in each dimension of the specified objects stored
   * in the given database. Returns
   * <code>variances(database, centroid(database, ids), ids)</code>
   * 
   * @param database the database storing the objects
   * @param ids the ids of the objects
   * @return the variances in each dimension of the specified objects
   */
  public static double[] variances(Database<? extends NumberVector<?, ?>> database, DBIDs ids) {
    return variances(database, centroidRelaxed(database, ids), ids);
  }

  /**
   * Determines the variances in each dimension of the specified objects stored
   * in the given database.
   * 
   * @param database the database storing the objects
   * @param ids the ids of the objects
   * @param centroid the centroid or reference vector of the ids
   * @return the variances in each dimension of the specified objects
   */
  public static double[] variances(Database<? extends NumberVector<?, ?>> database, NumberVector<?, ?> centroid, DBIDs ids) {
    double[] variances = new double[centroid.getDimensionality()];

    for(int d = 1; d <= centroid.getDimensionality(); d++) {
      double mu = centroid.doubleValue(d);

      for(DBID id : ids) {
        NumberVector<?, ?> o = database.get(id);
        double diff = o.doubleValue(d) - mu;
        variances[d - 1] += diff * diff;
      }

      variances[d - 1] /= ids.size();
    }
    return variances;
  }

  /**
   * Determines the variances in each dimension of the specified objects stored
   * in the given database.
   * 
   * @param database the database storing the objects
   * @param ids the array of ids of the objects to be considered in each
   *        dimension
   * @param centroid the centroid or reference vector of the ids
   * @return the variances in each dimension of the specified objects
   */
  public static double[] variances(Database<? extends NumberVector<?, ?>> database, NumberVector<?, ?> centroid, DBIDs[] ids) {
    double[] variances = new double[centroid.getDimensionality()];

    for(int d = 1; d <= centroid.getDimensionality(); d++) {
      double mu = centroid.doubleValue(d);

      DBIDs ids_d = ids[d - 1];
      for(DBID neighborID : ids_d) {
        NumberVector<?, ?> neighbor = database.get(neighborID);
        double diff = neighbor.doubleValue(d) - mu;
        variances[d - 1] += diff * diff;
      }

      variances[d - 1] /= ids_d.size();
    }

    return variances;
  }

  /**
   * Determines the minimum and maximum values in each dimension of all objects
   * stored in the given database.
   * 
   * @param <NV> vector type
   * @param database the database storing the objects
   * @return Minimum and Maximum vector for the hyperrectangle
   */
  public static <NV extends NumberVector<NV, ?>> Pair<NV, NV> computeMinMax(Database<NV> database) {
    int dim = database.dimensionality();
    double[] mins = new double[dim];
    double[] maxs = new double[dim];
    for(int i = 0; i < dim; i++) {
      mins[i] = Double.MAX_VALUE;
      maxs[i] = -Double.MAX_VALUE;
    }
    for(DBID it : database) {
      NV o = database.get(it);
      for(int d = 0; d < dim; d++) {
        double v = o.doubleValue(d + 1);
        mins[d] = Math.min(mins[d], v);
        maxs[d] = Math.max(maxs[d], v);
      }
    }
    NV factory = database.getObjectFactory();
    NV min = factory.newInstance(mins);
    NV max = factory.newInstance(maxs);
    return new Pair<NV, NV>(min, max);
  }

  /**
   * Retrieves all class labels within the database.
   * 
   * @param database the database to be scanned for class labels
   * @return a set comprising all class labels that are currently set in the
   *         database
   */
  public static SortedSet<ClassLabel> getClassLabels(Database<?> database) {
    // FIXME: re-add a similar check!
    // if(!database.isSetForAllObjects(AssociationID.CLASS)) {
    // throw new IllegalStateException("AssociationID " +
    // AssociationID.CLASS.getName() + " is not set.");
    // }
    SortedSet<ClassLabel> labels = new TreeSet<ClassLabel>();
    for(Iterator<DBID> iter = database.iterator(); iter.hasNext();) {
      labels.add(database.getClassLabel(iter.next()));
    }
    return labels;
  }

  /**
   * Do a cheap guess at the databases object class.
   * 
   * @param <O> Restriction type
   * @param database Database
   * @return Class of first object in the Database.
   */
  @SuppressWarnings("unchecked")
  public static <O extends DatabaseObject> Class<? extends O> guessObjectClass(Database<O> database) {
    for(DBID id : database) {
      return (Class<? extends O>) database.get(id).getClass();
    }
    return null;
  }

  /**
   * Do a full inspection of the database to find the base object class.
   * 
   * Note: this can be an abstract class or interface!
   * 
   * TODO: Implement a full search for shared superclasses. But since currently
   * the databases will always use only once class, this is not yet implemented.
   * 
   * @param <O> Restriction type
   * @param database Database
   * @return Superclass of all objects in the database
   */
  @SuppressWarnings("unchecked")
  public static <O extends DatabaseObject> Class<? extends DatabaseObject> getBaseObjectClassExpensive(Database<O> database) {
    final Class<DatabaseObject> databaseObjectClass = DatabaseObject.class;
    List<Class<? extends DatabaseObject>> candidates = new ArrayList<Class<? extends DatabaseObject>>();
    Iterator<DBID> iditer = database.iterator();
    // empty database?!
    if(!iditer.hasNext()) {
      return null;
    }
    // put first class into result set.
    candidates.add(database.get(iditer.next()).getClass());
    // other objects
    while(iditer.hasNext()) {
      Class<? extends DatabaseObject> newcls = database.get(iditer.next()).getClass();
      // validate all candidates
      Iterator<Class<? extends DatabaseObject>> ci = candidates.iterator();
      while(ci.hasNext()) {
        Class<? extends DatabaseObject> cand = ci.next();
        if(cand.isAssignableFrom(newcls)) {
          continue;
        }
        // TODO: resolve conflicts by finding all superclasses!
        // Does this code here work?
        for(Class<?> interf : cand.getInterfaces()) {
          if(interf.isAssignableFrom(databaseObjectClass)) {
            candidates.add((Class<? extends DatabaseObject>) interf);
          }
        }
        if(cand.getSuperclass().isAssignableFrom(databaseObjectClass)) {
          candidates.add((Class<? extends DatabaseObject>) cand.getSuperclass());
        }
        ci.remove();
      }
    }
    // if we have any candidates left ...
    if(candidates != null && candidates.size() > 0) {
      // remove subclasses
      Iterator<Class<? extends DatabaseObject>> ci = candidates.iterator();
      while(ci.hasNext()) {
        Class<? extends DatabaseObject> cand = ci.next();
        for(Class<? extends DatabaseObject> oc : candidates) {
          if(oc != cand && cand.isAssignableFrom(oc)) {
            ci.remove();
            break;
          }
        }
      }
      assert (candidates.size() > 0);
      try {
        return candidates.get(0);
      }
      catch(ClassCastException e) {
        // ignore, and retry with next
      }
    }
    // no resulting class.
    return null;
  }

  /**
   * Find object by matching their labels.
   * 
   * @param database Database to search in
   * @param name_pattern Name to match against class or object label
   * @return found cluster or it throws an exception.
   */
  public static ArrayModifiableDBIDs getObjectsByLabelMatch(Database<?> database, Pattern name_pattern) {
    if(name_pattern == null) {
      return DBIDUtil.newArray();
    }
    ArrayModifiableDBIDs ret = DBIDUtil.newArray();
    for(DBID objid : database) {
      if(name_pattern.matcher(getClassOrObjectLabel(database, objid)).matches()) {
        ret.add(objid);
      }
    }
    return ret;
  }

  /**
   * Get the class label or object label of an object in the database
   * 
   * @param database Database
   * @param objid Object ID
   * @return String representation of label or object label
   */
  public static String getClassOrObjectLabel(Database<?> database, DBID objid) {
    ClassLabel lbl = database.getClassLabel(objid);
    if(lbl != null) {
      return lbl.toString();
    }
    return database.getObjectLabel(objid);
  }

  /**
   * Iterator class that retrieves the given objects from the database.
   * 
   * @author Erich Schubert
   */
  public static class DatabaseObjectIterator<O extends DatabaseObject> implements Iterator<O> {
    /**
     * The real iterator.
     */
    final Iterator<DBID> iter;

    /**
     * The database we use
     */
    final Database<? extends O> database;

    /**
     * Full Constructor.
     * 
     * @param iter Original iterator.
     * @param database Database
     */
    public DatabaseObjectIterator(Iterator<DBID> iter, Database<? extends O> database) {
      super();
      this.iter = iter;
      this.database = database;
    }

    /**
     * Simplified constructor.
     * 
     * @param database Database
     */
    public DatabaseObjectIterator(Database<? extends O> database) {
      super();
      this.database = database;
      this.iter = database.iterator();
    }

    @Override
    public boolean hasNext() {
      return iter.hasNext();
    }

    @Override
    public O next() {
      DBID id = iter.next();
      return database.get(id);
    }

    @Override
    public void remove() {
      iter.remove();
    }
  }

  /**
   * Collection view on a database that retrieves the objects when needed.
   * 
   * @author Erich Schubert
   */
  public static class CollectionFromDatabase<O extends DatabaseObject> extends AbstractCollection<O> implements Collection<O> {
    /**
     * The database we query
     */
    Database<? extends O> db;

    /**
     * Constructor.
     * 
     * @param db Database
     */
    public CollectionFromDatabase(Database<? extends O> db) {
      super();
      this.db = db;
    }

    @Override
    public Iterator<O> iterator() {
      return new DatabaseUtil.DatabaseObjectIterator<O>(db);
    }

    @Override
    public int size() {
      return db.size();
    }
  }

  /**
   * An ugly vector type cast unavoidable in some situations due to Generics.
   * 
   * @param <V> Base vector type
   * @param <T> Derived vector type (is actually V, too)
   * @param database Database
   * @return Database
   */
  @SuppressWarnings("unchecked")
  public static <V extends NumberVector<?, ?>, T extends NumberVector<?, ?>> Database<V> databaseUglyVectorCast(Database<T> database) {
    return (Database<V>) database;
  }
}