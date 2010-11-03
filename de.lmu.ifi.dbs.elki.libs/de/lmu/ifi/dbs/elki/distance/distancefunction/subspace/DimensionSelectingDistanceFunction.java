package de.lmu.ifi.dbs.elki.distance.distancefunction.subspace;

import de.lmu.ifi.dbs.elki.data.HyperBoundingBox;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.query.SpatialPrimitiveDistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.AbstractPrimitiveDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.SpatialPrimitiveDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * Provides a distance function that computes the distance between feature
 * vectors as the absolute difference of their values in a specified dimension.
 * 
 * @author Elke Achtert
 */
public class DimensionSelectingDistanceFunction extends AbstractPrimitiveDistanceFunction<NumberVector<?, ?>, DoubleDistance> implements SpatialPrimitiveDistanceFunction<NumberVector<?, ?>, DoubleDistance> {
  /**
   * OptionID for {@link #DIM_PARAM}
   */
  public static final OptionID DIM_ID = OptionID.getOrCreateOptionID("dim", "an integer between 1 and the dimensionality of the " + "feature space 1 specifying the dimension to be considered " + "for distance computation.");

  /**
   * Parameter for dimensionality.
   */
  private final IntParameter DIM_PARAM = new IntParameter(DIM_ID, new GreaterEqualConstraint(1));

  /**
   * The dimension to be considered for distance computation.
   */
  private int dim;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public DimensionSelectingDistanceFunction(Parameterization config) {
    super();
    config = config.descend(this);
    if(config.grab(DIM_PARAM)) {
      dim = DIM_PARAM.getValue();
    }
  }

  /**
   * Computes the distance between two given DatabaseObjects according to this
   * distance function.
   * 
   * @param v1 first DatabaseObject
   * @param v2 second DatabaseObject
   * @return the distance between two given DatabaseObjects according to this
   *         distance function
   */
  @Override
  public DoubleDistance distance(NumberVector<?, ?> v1, NumberVector<?, ?> v2) {
    if(dim > v1.getDimensionality() || dim > v2.getDimensionality()) {
      throw new IllegalArgumentException("Specified dimension to be considered " + "is larger that dimensionality of FeatureVectors:" + "\n  first argument: " + v1.toString() + "\n  second argument: " + v2.toString() + "\n  dimension: " + dim);
    }

    double manhattan = v1.doubleValue(dim) - v2.doubleValue(dim);
    return new DoubleDistance(Math.abs(manhattan));
  }

  @Override
  public DoubleDistance minDist(HyperBoundingBox mbr, NumberVector<?, ?> v) {
    if(dim > mbr.getDimensionality() || dim > v.getDimensionality()) {
      throw new IllegalArgumentException("Specified dimension to be considered " + "is larger that dimensionality of FeatureVectors:" + "\n  first argument: " + mbr.toString() + "\n  second argument: " + v.toString() + "\n  dimension: " + dim);
    }

    double value = v.doubleValue(dim);
    double r;
    if(value < mbr.getMin(dim)) {
      r = mbr.getMin(dim);
    }
    else if(value > mbr.getMax(dim)) {
      r = mbr.getMax(dim);
    }
    else {
      r = value;
    }

    double manhattan = value - r;
    return new DoubleDistance(Math.abs(manhattan));
  }

  // FIXME: REMOVE?
  /*
   * @Override public DoubleDistance minDist(HyperBoundingBox mbr, DBID id) {
   * return minDist(mbr, getDatabase().get(id)); }
   */

  @Override
  public DoubleDistance distance(HyperBoundingBox mbr1, HyperBoundingBox mbr2) {
    if(dim > mbr1.getDimensionality() || dim > mbr2.getDimensionality()) {
      throw new IllegalArgumentException("Specified dimension to be considered " + "is larger that dimensionality of FeatureVectors:" + "\n  first argument: " + mbr1.toString() + "\n  second argument: " + mbr2.toString() + "\n  dimension: " + dim);
    }

    double m1, m2;
    if(mbr1.getMax(dim) < mbr2.getMin(dim)) {
      m1 = mbr1.getMax(dim);
      m2 = mbr2.getMin(dim);
    }
    else if(mbr1.getMin(dim) > mbr2.getMax(dim)) {
      m1 = mbr1.getMin(dim);
      m2 = mbr2.getMax(dim);
    }
    else { // The mbrs intersect!
      m1 = 0;
      m2 = 0;
    }
    double manhattan = m1 - m2;

    return new DoubleDistance(Math.abs(manhattan));
  }

  @Override
  public DoubleDistance centerDistance(HyperBoundingBox mbr1, HyperBoundingBox mbr2) {
    if(dim > mbr1.getDimensionality() || dim > mbr2.getDimensionality()) {
      throw new IllegalArgumentException("Specified dimension to be considered " + "is larger that dimensionality of FeatureVectors:" + "\n  first argument: " + mbr1.toString() + "\n  second argument: " + mbr2.toString() + "\n  dimension: " + dim);
    }

    double c1 = (mbr1.getMin(dim) + mbr1.getMax(dim)) / 2;
    double c2 = (mbr2.getMin(dim) + mbr2.getMax(dim)) / 2;

    double manhattan = c1 - c2;

    return new DoubleDistance(Math.abs(manhattan));
  }

  /**
   * Returns the selected dimension.
   * 
   * @return the selected dimension
   */
  public int getSelectedDimension() {
    return dim;
  }

  @Override
  public Class<? super NumberVector<?, ?>> getInputDatatype() {
    return NumberVector.class;
  }

  @Override
  public DoubleDistance getDistanceFactory() {
    return DoubleDistance.FACTORY;
  }

  @Override
  public <T extends NumberVector<?, ?>> SpatialPrimitiveDistanceQuery<T, DoubleDistance> instantiate(Database<T> database) {
    return new SpatialPrimitiveDistanceQuery<T, DoubleDistance>(database, this);
  }
}