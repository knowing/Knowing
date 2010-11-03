package de.lmu.ifi.dbs.elki.distance.distancevalue;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.regex.Pattern;

/**
 * A PreferenceVectorBasedCorrelationDistance holds additionally to the
 * CorrelationDistance the common preference vector of the two objects defining
 * the distance.
 * 
 * @author Elke Achtert
 */
public class PreferenceVectorBasedCorrelationDistance extends CorrelationDistance<PreferenceVectorBasedCorrelationDistance> {
  /**
   * The static factory instance
   */
  public final static PreferenceVectorBasedCorrelationDistance FACTORY = new PreferenceVectorBasedCorrelationDistance();
  
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1;

  /**
   * The dimensionality of the feature space (needed for serialization).
   */
  private int dimensionality;

  /**
   * The common preference vector of the two objects defining this distance.
   */
  private BitSet commonPreferenceVector;

  /**
   * Empty constructor for serialization purposes.
   */
  public PreferenceVectorBasedCorrelationDistance() {
    super();
  }

  /**
   * Constructs a new CorrelationDistance object.
   * 
   * @param dimensionality the dimensionality of the feature space (needed for
   *        serialization)
   * @param correlationValue the correlation dimension to be represented by the
   *        CorrelationDistance
   * @param euclideanValue the euclidean distance to be represented by the
   *        CorrelationDistance
   * @param commonPreferenceVector the common preference vector of the two
   *        objects defining this distance
   */
  public PreferenceVectorBasedCorrelationDistance(int dimensionality, int correlationValue, double euclideanValue, BitSet commonPreferenceVector) {
    super(correlationValue, euclideanValue);
    this.dimensionality = dimensionality;
    this.commonPreferenceVector = commonPreferenceVector;
  }

  /**
   * Returns the common preference vector of the two objects defining this
   * distance.
   * 
   * @return the common preference vector
   */
  public BitSet getCommonPreferenceVector() {
    return commonPreferenceVector;
  }

  /**
   * Returns a string representation of this
   * PreferenceVectorBasedCorrelationDistance.
   * 
   * @return the correlation value, the euclidean value and the common
   *         preference vector separated by blanks
   */
  @Override
  public String toString() {
    return super.toString() + SEPARATOR + commonPreferenceVector.toString();
  }

  /**
   * @throws IllegalArgumentException if the dimensionality values and common
   *         preference vectors of this distance and the specified distance are
   *         not equal
   */
  @Override
  public PreferenceVectorBasedCorrelationDistance plus(PreferenceVectorBasedCorrelationDistance distance) {
    if(this.dimensionality != distance.dimensionality) {
      throw new IllegalArgumentException("The dimensionality values of this distance " + "and the specified distance need to be equal.\n" + "this.dimensionality     " + this.dimensionality + "\n" + "distance.dimensionality " + distance.dimensionality + "\n");
    }

    if(!this.commonPreferenceVector.equals(distance.commonPreferenceVector)) {
      throw new IllegalArgumentException("The common preference vectors of this distance " + "and the specified distance need to be equal.\n" + "this.commonPreferenceVector     " + this.commonPreferenceVector + "\n" + "distance.commonPreferenceVector " + distance.commonPreferenceVector + "\n");
    }

    return new PreferenceVectorBasedCorrelationDistance(dimensionality, getCorrelationValue() + distance.getCorrelationValue(), getEuclideanValue() + distance.getEuclideanValue(), (BitSet) commonPreferenceVector.clone());
  }

  /**
   * @throws IllegalArgumentException if the dimensionality values and common
   *         preference vectors of this distance and the specified distance are
   *         not equal
   */
  @Override
  public PreferenceVectorBasedCorrelationDistance minus(PreferenceVectorBasedCorrelationDistance distance) {
    if(this.dimensionality != distance.dimensionality) {
      throw new IllegalArgumentException("The dimensionality values of this distance " + "and the specified distance need to be equal.\n" + "this.dimensionality     " + this.dimensionality + "\n" + "distance.dimensionality " + distance.dimensionality + "\n");
    }

    if(!this.commonPreferenceVector.equals(distance.commonPreferenceVector)) {
      throw new IllegalArgumentException("The common preference vectors of this distance " + "and the specified distance need to be equal.\n" + "this.commonPreferenceVector     " + this.commonPreferenceVector + "\n" + "distance.commonPreferenceVector " + distance.commonPreferenceVector + "\n");
    }

    return new PreferenceVectorBasedCorrelationDistance(dimensionality, getCorrelationValue() - distance.getCorrelationValue(), getEuclideanValue() - distance.getEuclideanValue(), (BitSet) commonPreferenceVector.clone());
  }

  /**
   * Checks if the dimensionality values of this distance and the specified
   * distance are equal. If the check fails an IllegalArgumentException is
   * thrown, otherwise
   * {@link CorrelationDistance#compareTo(CorrelationDistance)
   * CorrelationDistance#compareTo(distance)} is returned.
   * 
   * @return the value of
   *         {@link CorrelationDistance#compareTo(CorrelationDistance)
   *         CorrelationDistance#compareTo(distance)}
   * @throws IllegalArgumentException if the dimensionality values of this
   *         distance and the specified distance are not equal
   */
  @Override
  public int compareTo(PreferenceVectorBasedCorrelationDistance distance) {
    if(this.dimensionality >= 0 && distance.dimensionality >= 0 && this.dimensionality != distance.dimensionality) {
      throw new IllegalArgumentException("The dimensionality values of this distance " + "and the specified distance need to be equal.\n" + "this.dimensionality     " + this.dimensionality + "\n" + "distance.dimensionality " + distance.dimensionality + "\n");
    }

    return super.compareTo(distance);
  }

  /**
   * Calls
   * {@link de.lmu.ifi.dbs.elki.distance.distancevalue.CorrelationDistance#writeExternal(java.io.ObjectOutput)}
   * and writes additionally the dimensionality and each Byte of the common
   * preference vector to the specified stream.
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeInt(dimensionality);
    for(int d = 0; d < dimensionality; d++) {
      out.writeBoolean(commonPreferenceVector.get(d));
    }
  }

  /**
   * Calls
   * {@link de.lmu.ifi.dbs.elki.distance.distancevalue.CorrelationDistance#readExternal(java.io.ObjectInput)}
   * and reads additionally the dimensionality and each Byte of the common
   * preference vector from the specified stream..
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException {
    super.readExternal(in);
    dimensionality = in.readInt();
    commonPreferenceVector = new BitSet();
    for(int d = 0; d < dimensionality; d++) {
      commonPreferenceVector.set(d, in.readBoolean());
    }
  }

  /**
   * Returns the number of Bytes this distance uses if it is written to an
   * external file.
   * 
   * @return 16 + 4 * dimensionality (8 Byte for two integer, 8 Byte for a
   *         double value, and 4 * dimensionality for the bit set)
   */
  @Override
  public int externalizableSize() {
    return super.externalizableSize() + 4 + dimensionality * 4;
  }

  @Override
  public Pattern getPattern() {
    return CORRELATION_DISTANCE_PATTERN;
  }

  @Override
  public PreferenceVectorBasedCorrelationDistance parseString(String pattern) throws IllegalArgumentException {
    if(pattern.equals(INFINITY_PATTERN)) {
      return infiniteDistance();
    }
    if(testInputPattern(pattern)) {
      String[] values = SEPARATOR.split(pattern);
      return new PreferenceVectorBasedCorrelationDistance(-1, Integer.parseInt(values[0]), Double.parseDouble(values[1]), new BitSet());
    }
    else {
      throw new IllegalArgumentException("Given pattern \"" + pattern + "\" does not match required pattern \"" + requiredInputPattern() + "\"");
    }
  }

  @Override
  public PreferenceVectorBasedCorrelationDistance infiniteDistance() {
    return new PreferenceVectorBasedCorrelationDistance(-1, Integer.MAX_VALUE, Double.POSITIVE_INFINITY, new BitSet());
  }

  @Override
  public PreferenceVectorBasedCorrelationDistance nullDistance() {
    return new PreferenceVectorBasedCorrelationDistance(-1, 0, 0, new BitSet());
  }

  @Override
  public PreferenceVectorBasedCorrelationDistance undefinedDistance() {
    return new PreferenceVectorBasedCorrelationDistance(-1, -1, Double.NaN, new BitSet());
  }
}