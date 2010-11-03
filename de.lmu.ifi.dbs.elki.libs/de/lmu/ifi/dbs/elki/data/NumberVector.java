package de.lmu.ifi.dbs.elki.data;

import java.util.Random;

import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialObject;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;

/**
 * Interface NumberVector defines the methods that should be implemented by any
 * Object that is element of a real vector space of type N.
 * 
 * 
 * @param <V> the type of NumberVector implemented by a subclass
 * @param <N> the type of the attribute values
 * 
 * @author Arthur Zimek
 */
public interface NumberVector<V extends NumberVector<? extends V, N>, N extends Number> extends FeatureVector<V, N>, SpatialObject {
  /**
   * Returns a NumberVector of V with uniformly distributed (0-1) random values.
   * 
   * @param random a Random instance
   * @return a NumberVector of V with random values
   */
  V randomInstance(Random random);

  /**
   * Returns a NumberVector of V with random values between min and max.
   * 
   * @param min minimum of random value
   * @param max maximum of random value
   * @param random a random instance
   * @return a NumberVector of V with random values between min and max
   */
  V randomInstance(N min, N max, Random random);

  /**
   * Returns a NumberVector of V with random values between min and max.
   * 
   * @param min minimum of random value for each axis
   * @param max maximum of random value for each axis
   * @param random a random instance
   * @return a NumberVector of V with random values between min and max
   */
  V randomInstance(V min, V max, Random random);

  /**
   * Returns a Vector representing in one column and
   * <code>getDimensionality()</code> rows the values of this NumberVector of V.
   * 
   * @return a Matrix representing in one column and
   *         <code>getDimensionality()</code> rows the values of this
   *         NumberVector of V
   */
  Vector getColumnVector();

  /**
   * Returns a Matrix representing in one row and
   * <code>getDimensionality()</code> columns the values of this NumberVector of
   * V.
   * 
   * @return a Matrix representing in one row and
   *         <code>getDimensionality()</code> columns the values of this
   *         NumberVector of V
   */
  Matrix getRowVector();

  /**
   * Returns a new NumberVector of V that is the sum of this NumberVector of V
   * and the given NumberVector of V.
   * 
   * @param fv a NumberVector of V to be added to this NumberVector of V
   * @return a new NumberVector of V that is the sum of this NumberVector of V
   *         and the given NumberVector of V
   */
  V plus(V fv);

  /**
   * Returns a new NumberVector of V that is the sum of this NumberVector of V
   * and the negativeVector() of given NumberVector of V.
   * 
   * @param fv a NumberVector of V to be subtracted to this NumberVector of V
   * @return a new NumberVector of V that is the sum of this NumberVector of V
   *         and the negative of given NumberVector of V
   */
  V minus(V fv);

  /**
   * Provides the scalar product (inner product) of this NumberVector of V and
   * the given NumberVector of V.
   * 
   * @param fv the NumberVector of V to compute the scalar product for
   * @return the scalar product (inner product) of this and the given
   *         NumberVector of V
   */
  N scalarProduct(V fv);

  /**
   * Provides a null vector of the same Vector Space as this NumberVector of V
   * (that is, of the same dimensionality).
   * 
   * @return a null vector of the same Vector Space as this NumberVector of V
   *         (that is, of the same dimensionality)
   */
  V nullVector();

  /**
   * Returns the additive inverse to this NumberVector of V.
   * 
   * @return the additive inverse to this NumberVector of V
   */
  V negativeVector();

  /**
   * Returns a new NumberVector of V that is the result of a scalar
   * multiplication with the given scalar.
   * 
   * @param k a scalar to multiply this NumberVector of V with
   * @return a new NumberVector of V that is the result of a scalar
   *         multiplication with the given scalar
   */
  V multiplicate(double k);

  /**
   * Returns the value in the specified dimension as double.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).doubleValue()}, but usually this is much more
   * efficient due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 1 &le; dimension &le;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  double doubleValue(int dimension);

  /**
   * Returns the value in the specified dimension as float.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).floatValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 1 &le; dimension &le;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  float floatValue(int dimension);

  /**
   * Returns the value in the specified dimension as int.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).intValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 1 &le; dimension &le;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  int intValue(int dimension);

  /**
   * Returns the value in the specified dimension as long.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).longValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 1 &le; dimension &le;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  long longValue(int dimension);

  /**
   * Returns the value in the specified dimension as short.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).shortValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 1 &le; dimension &le;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  short shortValue(int dimension);

  /**
   * Returns the value in the specified dimension as byte.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).byteValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 1 &le; dimension &le;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  byte byteValue(int dimension);

  /**
   * Returns a new NumberVector of N for the given values.
   * 
   * @param values the values of the NumberVector
   * @return a new NumberVector of N for the given values
   */
  V newInstance(double[] values);

  /**
   * Returns a new NumberVector of N for the given values.
   * 
   * @param values the values of the NumberVector
   * @return a new NumberVector of N for the given values
   */
  V newInstance(Vector values);
}