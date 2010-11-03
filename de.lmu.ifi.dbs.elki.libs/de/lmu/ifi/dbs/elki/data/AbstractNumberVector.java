package de.lmu.ifi.dbs.elki.data;

/**
 * AbstractNumberVector is an abstract implementation of FeatureVector. Provided is an
 * attribute separator (space), and the ID-methods as required for a
 * DatabaseObject. The equals-method is implemented dynamically for all
 * subclasses to satisfy the requirements as defined in
 * {@link DatabaseObject#equals(Object) DatabaseObject.equals(Object)}. It needs
 * not to be overwritten except for sake of efficiency.
 * 
 * @author Arthur Zimek
 * @param <V> the concrete type of this AbstractNumberVector
 * @param <N> the type of number, this AbstractNumberVector consists of (i.e., a
 *        AbstractNumberVector {@code v} of type {@code V} and dimensionality {@code d}
 *        is an element of {@code N}<sup>{@code d}</sup>)
 */
public abstract class AbstractNumberVector<V extends AbstractNumberVector<? extends V, N>, N extends Number> extends AbstractDatabaseObject implements NumberVector<V, N> {
  /**
   * The String to separate attribute values in a String that represents the
   * values.
   */
  public final static String ATTRIBUTE_SEPARATOR = " ";

  /**
   * An Object obj is equal to this AbstractNumberVector if it is an instance of the
   * same runtime class and is of the identical dimensionality and the values of
   * this AbstractNumberVector are equal to the values of obj in all dimensions,
   * respectively.
   * 
   * @param obj another Object
   * @return true if the specified Object is an instance of the same runtime
   *         class and is of the identical dimensionality and the values of this
   *         AbstractNumberVector are equal to the values of obj in all dimensions,
   *         respectively
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if(this.getClass().isInstance(obj)) {
      V rv = (V) obj;
      boolean equal = (this.getDimensionality() == rv.getDimensionality());
      for(int i = 1; i <= getDimensionality() && equal; i++) {
        equal &= this.getValue(i).equals(rv.getValue(i));
      }
      return equal;
    }
    else {
      return false;
    }
  }

  /**
   * Get Minimum - implementation of the SpatialComparable interface
   * 
   * @return the value at the specified dimension
   * @see #doubleValue(int)
   */
  @Override
  public double getMin(int dimension) {
    return doubleValue(dimension);
  }

  /**
   * Get Maximum - implementation of the SpatialComparable interface
   * 
   * @return the value at the specified dimension
   * @see #doubleValue(int)
   */
  @Override
  public double getMax(int dimension) {
    return doubleValue(dimension);
  }

  @Override
  public byte byteValue(int dimension) {
    return (byte) longValue(dimension);
  }

  @Override
  public float floatValue(int dimension) {
    return (float) doubleValue(dimension);
  }

  @Override
  public int intValue(int dimension) {
    return (int) longValue(dimension);
  }

  @Override
  public short shortValue(int dimension) {
    return (short) longValue(dimension);
  }

  @Override
  public Integer getPageID() {
    return getID().getIntegerID();
  }
}