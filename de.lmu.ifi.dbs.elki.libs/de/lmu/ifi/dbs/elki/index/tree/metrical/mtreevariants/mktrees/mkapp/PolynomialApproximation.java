package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.mktrees.mkapp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.lmu.ifi.dbs.elki.utilities.FormatUtil;

/**
 * Provides an polynomial approximation bo + b1*k + b2*k^2 + ... + bp*k^p
 * for knn-distances consisting of parameters b0, ..., bp.
 *
 * @author Elke Achtert 
 */
public class PolynomialApproximation implements Externalizable {
  private static final long serialVersionUID = 1;

  /**
   * The parameters b0, ..., bp.
   */
  private double[] b;

  /**
   * Empty constructor for serialization purposes.
   */
  public PolynomialApproximation() {
	  // empty constructor
  }

  /**
   * Provides an polynomial approximation bo + b1*k + b2*k^2 + ... + bp*k^p
   * for knn-distances consisting of parameters b0, ..., bp.
   *
   * @param b the parameters b0, ..., bi
   */
  public PolynomialApproximation(double[] b) {
    this.b = b;
  }

  /**
   * Returns the parameter bp at the specified index p.
   *
   * @param p the index
   * @return the parameter bp at the specified index p
   */
  public double getB(int p) {
    return b[p];
  }

  /**
   * Returns a copy of the the array of coefficients b0, ..., bp.
   * @return the a copy of the array of coefficients b0, ..., bp
   */
  public double[] getCoefficients() {
    double[] result = new double[b.length];
    System.arraycopy(b, 0, result, 0, b.length);
    return result;
  }

  /**
   * Returns the order of the polynom.
   *
   * @return the order of the polynom
   */
  public int getPolynomialOrder() {
    return b.length;
  }

  /**
   * Returns the function value of the polynoial approximation
   * at the specified k.
   *
   * @param k the value for which the polynoial approximation should be returned
   * @return the function value of the polynoial approximation
   *         at the specified k
   */
  public double getValueAt(int k) {
    double result = 0;
    double log_k = Math.log(k);
    for (int p = 0; p < b.length; p++) {
      result += b[p] * Math.pow(log_k, p);
    }
    return result;
  }

  /**
   * The object implements the writeExternal method to save its contents
   * by calling the methods of DataOutput for its primitive values or
   * calling the writeObject method of ObjectOutput for objects, strings,
   * and arrays.
   *
   * @param out the stream to write the object to
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(b.length);
    for (double aB : b) {
      out.writeDouble(aB);
    }
  }

  /**
   * The object implements the readExternal method to restore its
   * contents by calling the methods of DataInput for primitive
   * types and readObject for objects, strings and arrays.  The
   * readExternal method must read the values in the same sequence
   * and with the same types as were written by writeExternal.
   *
   * @param in the stream to read data from in order to restore the object
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException {
    b = new double[in.readInt()];
    for (int p = 0; p < b.length; p++) {
      b[p] = in.readDouble();
    }
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  @Override
  public String toString() {
    return FormatUtil.format(b, 4);
  }
}
