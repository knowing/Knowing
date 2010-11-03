package de.lmu.ifi.dbs.elki.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.parser.FloatVectorLabelParser;
import de.lmu.ifi.dbs.elki.parser.SparseFloatVectorLabelParser;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.Util;

/**
 * <p>
 * A SparseFloatVector is to store real values approximately as float values.
 * </p>
 * 
 * A SparseFloatVector only requires storage for those attribute values that are
 * non-zero.
 * 
 * @author Arthur Zimek
 */
// TODO: implement ByteArraySerializer<SparseFloatVector>
public class SparseFloatVector extends AbstractNumberVector<SparseFloatVector, Float> {
  /**
   * Mapping of indices and corresponding values. Only non-zero values will to
   * be stored.
   */
  private Map<Integer, Float> values;

  /**
   * The maximal occurring index of any dimension.
   */
  private Integer maximumIndex = -1;

  /**
   * The dimensionality of this feature vector.
   */
  private int dimensionality;

  /**
   * Provides a SparseFloatVector consisting of double values according to the
   * specified mapping of indices and values.
   * 
   * @param values the values to be set as values of the real vector
   * @param dimensionality the dimensionality of this feature vector
   * @throws IllegalArgumentException if the given dimensionality is too small
   *         to cover the given values (i.e., the maximum index of any value not
   *         zero is bigger than the given dimensionality)
   */
  public SparseFloatVector(Map<Integer, Float> values, int dimensionality) throws IllegalArgumentException {
    if(values.size() > dimensionality) {
      throw new IllegalArgumentException("values.size() > dimensionality!");
    }

    this.values = new HashMap<Integer, Float>(values.size(), 1);
    for(Integer index : values.keySet()) {
      if(index > maximumIndex) {
        maximumIndex = index;
      }
      Float value = values.get(index);
      if(value != 0) {
        this.values.put(index, value);
      }
    }
    this.dimensionality = dimensionality;
    if(maximumIndex > dimensionality) {
      throw new IllegalArgumentException("Given dimensionality " + dimensionality + " is too small w.r.t. the given values (occurring maximum: " + maximumIndex + ").");
    }
  }

  /**
   * Provides a SparseFloatVector consisting of double values according to the
   * specified mapping of indices and values.
   * 
   * @param values the values to be set as values of the real vector
   * @throws IllegalArgumentException if the given dimensionality is too small
   *         to cover the given values (i.e., the maximum index of any value not
   *         zero is bigger than the given dimensionality)
   */
  public SparseFloatVector(float[] values) throws IllegalArgumentException {
    this.dimensionality = values.length;
    this.values = new HashMap<Integer, Float>();
    for(int i = 0; i < values.length; i++) {
      float value = values[i];
      if(value != 0.0f) {
        if(i + 1 > maximumIndex) {
          maximumIndex = i + 1;
        }
        this.values.put(i + 1, value);
      }
    }
    if(maximumIndex > dimensionality) {
      throw new IllegalArgumentException("Given dimensionality " + dimensionality + " is too small w.r.t. the given values (occurring maximum: " + maximumIndex + ").");
    }
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#randomInstance(java.util.Random)
   */
  @Override
  public SparseFloatVector randomInstance(Random random) {
    return randomInstance(0.0f, 1.0f, random);
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#randomInstance(java.lang.Number,
   *      java.lang.Number, java.util.Random)
   */
  @Override
  public SparseFloatVector randomInstance(Float min, Float max, Random random) {
    float[] randomValues = new float[dimensionality];
    for(int i = 0; i < dimensionality; i++) {
      randomValues[i] = random.nextFloat() * (max - min) + min;
    }
    return new SparseFloatVector(randomValues);
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#randomInstance(de.lmu.ifi.dbs.elki.data.NumberVector,
   *      de.lmu.ifi.dbs.elki.data.NumberVector, java.util.Random)
   */
  @Override
  public SparseFloatVector randomInstance(SparseFloatVector min, SparseFloatVector max, Random random) {
    float[] randomValues = new float[dimensionality];
    for(int i = 0; i < dimensionality; i++) {
      randomValues[i] = random.nextFloat() * (max.getValue(i + 1) - min.getValue(i + 1)) + min.getValue(i + 1);
    }
    return new SparseFloatVector(randomValues);
  }

  @Override
  public int getDimensionality() {
    return dimensionality;
  }

  /**
   * Sets the dimensionality to the new value.
   * 
   * 
   * @param dimensionality the new dimensionality
   * @throws IllegalArgumentException if the given dimensionality is too small
   *         to cover the given values (i.e., the maximum index of any value not
   *         zero is bigger than the given dimensionality)
   */
  public void setDimensionality(int dimensionality) throws IllegalArgumentException {
    if(maximumIndex > dimensionality) {
      throw new IllegalArgumentException("Given dimensionality " + dimensionality + " is too small w.r.t. the given values (occurring maximum: " + maximumIndex + ").");
    }
    this.dimensionality = dimensionality;
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#getValue(int)
   */
  @Override
  public Float getValue(int dimension) {
    Float d = values.get(dimension);
    if(d != null) {
      return d;
    }
    else {
      return 0.0f;
    }
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#doubleValue(int)
   */
  @Override
  public double doubleValue(int dimension) {
    Float d = values.get(dimension);
    if(d != null) {
      return d;
    }
    else {
      return 0.0f;
    }
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#longValue(int)
   */
  @Override
  public long longValue(int dimension) {
    Float d = values.get(dimension);
    if(d != null) {
      return d.longValue();
    }
    else {
      return 0;
    }
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#getColumnVector()
   */
  @Override
  public Vector getColumnVector() {
    double[] values = getValues();
    return new Vector(values);
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#getRowVector()
   */
  @Override
  public Matrix getRowVector() {
    return new Matrix(new double[][] { getValues() });
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#plus(de.lmu.ifi.dbs.elki.data.NumberVector)
   */
  @Override
  public SparseFloatVector plus(SparseFloatVector fv) {
    if(fv.getDimensionality() != this.getDimensionality()) {
      throw new IllegalArgumentException("Incompatible dimensionality: " + this.getDimensionality() + " - " + fv.getDimensionality() + ".");
    }
    Map<Integer, Float> newValues = new HashMap<Integer, Float>(this.values);

    for(Integer fvkey : fv.values.keySet()) {
      if(newValues.containsKey(fvkey)) {
        newValues.put(fvkey, newValues.get(fvkey) + fv.values.get(fvkey));
      }
      else {
        newValues.put(fvkey, fv.values.get(fvkey));
      }
    }
    return new SparseFloatVector(newValues, this.dimensionality);
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#minus(de.lmu.ifi.dbs.elki.data.NumberVector)
   */
  @Override
  public SparseFloatVector minus(SparseFloatVector fv) {
    if(fv.getDimensionality() != this.getDimensionality()) {
      throw new IllegalArgumentException("Incompatible dimensionality: " + this.getDimensionality() + " - " + fv.getDimensionality() + ".");
    }
    Map<Integer, Float> newValues = new HashMap<Integer, Float>(this.values);

    for(Integer fvkey : fv.values.keySet()) {
      if(newValues.containsKey(fvkey)) {
        newValues.put(fvkey, newValues.get(fvkey) - fv.values.get(fvkey));
      }
      else {
        newValues.put(fvkey, - fv.values.get(fvkey));
      }
    }
    return new SparseFloatVector(newValues, this.dimensionality);
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#nullVector()
   */
  @Override
  public SparseFloatVector nullVector() {
    return new SparseFloatVector(new HashMap<Integer, Float>(), dimensionality);
  }

  /**
   * 
   * @see de.lmu.ifi.dbs.elki.data.NumberVector#negativeVector()
   */
  @Override
  public SparseFloatVector negativeVector() {
    return multiplicate(-1);
  }

  /**
   * Provides a new SparseFloatVector as result of the multiplication of this
   * SparseFloatVector by the scalar <code>k</code>.
   * 
   * @param k a scalar to multiply this SparseFloatVector
   * @return a new SparseFloatVector as result of the multiplication
   */
  @Override
  public SparseFloatVector multiplicate(double k) {
    Map<Integer, Float> newValues = new HashMap<Integer, Float>(this.values.size(), 1);
    for(Integer key : this.values.keySet()) {
      newValues.put(key, (float) (values.get(key) * k));
    }
    return new SparseFloatVector(newValues, this.dimensionality);
  }

  /**
   * Provides a String representation of this SparseFloatVector as suitable for
   * {@link FloatVectorLabelParser}.
   * 
   * This includes zero valued attributes but no indices.
   * 
   * <p>
   * Example: a vector (0,1.2,1.3,0)<sup>T</sup> would result in the String<br>
   * <code>0 1.2 1.3 0</code><br>
   * </p>
   * 
   * @return a String representation of this SparseFloatVector as a list of all
   *         values
   */
  public String toCompleteString() {
    StringBuffer featureLine = new StringBuffer();
    for(int i = 0; i < dimensionality; i++) {
      featureLine.append(getValue(i + 1));
      if(i + 1 < dimensionality) {
        featureLine.append(ATTRIBUTE_SEPARATOR);
      }
    }
    return featureLine.toString();
  }

  /**
   * <p>
   * Provides a String representation of this SparseFloatVector as suitable for
   * {@link SparseFloatVectorLabelParser}.
   * </p>
   * 
   * <p>
   * The returned String is a single line with entries separated by
   * {@link AbstractNumberVector#ATTRIBUTE_SEPARATOR}. The first entry gives the
   * number of values actually not zero. Following entries are pairs of Integer
   * and Float where the Integer gives the index of the dimensionality and the
   * Float gives the corresponding value.
   * </p>
   * 
   * <p>
   * Example: a vector (0,1.2,1.3,0)<sup>T</sup> would result in the String<br>
   * <code>2 2 1.2 3 1.3</code><br>
   * </p>
   * 
   * @return a String representation of this SparseFloatVector
   */
  @Override
  public String toString() {
    StringBuilder featureLine = new StringBuilder();
    featureLine.append(this.values.size());
    List<Integer> keys = new ArrayList<Integer>(this.values.keySet());
    Collections.sort(keys);
    for(Integer key : keys) {
      featureLine.append(ATTRIBUTE_SEPARATOR);
      featureLine.append(key);
      featureLine.append(ATTRIBUTE_SEPARATOR);
      featureLine.append(this.values.get(key));
    }

    return featureLine.toString();
  }

  /**
   * Returns an array consisting of the values of this feature vector.
   * 
   * @return an array consisting of the values of this feature vector
   */
  private double[] getValues() {
    double[] values = new double[dimensionality];
    for(int i = 0; i < dimensionality; i++) {
      values[i] = getValue(i + 1);
    }
    return values;
  }

  /**
   * Provides a list containing the indices (dimensions) with a value other than
   * zero.
   * 
   * The indices of occurring dimensions are sorted in ascending order. The
   * returned list is not backed by this SparseFloatVector, i.e., changes of
   * this SparseFloatVector will not affect the list returned by thins method.
   * 
   * @return a list containing the indices (dimensions) with a value other than
   *         zero
   */
  public List<Integer> getIndicesOfNotNullValues() {
    List<Integer> keys = new ArrayList<Integer>(this.values.keySet());
    Collections.sort(keys);
    return keys;
  }

  /**
   * Provides the scalar product (inner product) of this and the given
   * SparseFloatVector.
   * 
   * @param fv the SparseFloatVector to compute the scalar product for
   * @return the scalar product (inner product) of this and the given
   *         SparseFloatVector
   */
  @Override
  public Float scalarProduct(SparseFloatVector fv) {
    if(this.getDimensionality() != fv.getDimensionality()) {
      throw new IllegalArgumentException("Incompatible dimensionality: " + this.getDimensionality() + " - " + fv.getDimensionality() + ".");
    }
    float result = 0.0f;
    if(fv.values.keySet().size() <= this.values.keySet().size()) {
      for(Integer fvkey : fv.values.keySet()) {
        if(this.values.containsKey(fvkey)) {
          result += this.values.get(fvkey) * fv.values.get(fvkey);
        }
      }
    }
    else {
      for(Integer key : this.values.keySet()) {
        if(fv.values.containsKey(key)) {
          result += this.values.get(key) * fv.values.get(key);
        }
      }
    }
    return result;
  }

  @Override
  public SparseFloatVector newInstance(Vector values) {
    return newInstance(values.getArrayRef());
  }

  @Override
  public SparseFloatVector newInstance(double[] values) {
    // FIXME: inefficient
    return new SparseFloatVector(Util.convertToFloat(values));
  }

  @Override
  public SparseFloatVector newInstance(List<Float> values) {
    return new SparseFloatVector(Util.unboxToFloat(ClassGenericsUtil.toArray(values, Float.class)));
  }

  @Override
  public SparseFloatVector newInstance(Float[] values) {
    return new SparseFloatVector(Util.unboxToFloat(values));
  }
}