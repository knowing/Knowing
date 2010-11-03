package de.lmu.ifi.dbs.elki.normalization;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.DatabaseObjectMetadata;
import de.lmu.ifi.dbs.elki.math.MeanVariance;
import de.lmu.ifi.dbs.elki.math.linearalgebra.LinearEquationSystem;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.FormatUtil;
import de.lmu.ifi.dbs.elki.utilities.Util;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.AllOrNoneMustBeSetGlobalConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.EqualSizeGlobalConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleListParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ListParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Class to perform and undo a normalization on real vectors with respect to
 * given mean and standard deviation in each dimension.
 * 
 * @author Erich Schubert
 * @param <V> vector type
 */
// TODO: extract superclass AbstractAttributeWiseNormalization
public class AttributeWiseVarianceNormalization<V extends NumberVector<V, ?>> extends AbstractNormalization<V> {
  /**
   * OptionID for {@link #MEAN_PARAM}
   */
  public static final OptionID MEAN_ID = OptionID.getOrCreateOptionID("normalize.mean", "a comma separated concatenation of the mean values in each dimension that are mapped to 0. If no value is specified, the mean value of the attribute range in this dimension will be taken.");

  /**
   * OptionID for {@link #STDDEV_PARAM}
   */
  public static final OptionID STDDEV_ID = OptionID.getOrCreateOptionID("normalize.stddev", "a comma separated concatenation of the standard deviations in each dimension that are scaled to 1. If no value is specified, the standard deviation of the attribute range in this dimension will be taken.");

  /**
   * Parameter for means.
   */
  private final DoubleListParameter MEAN_PARAM = new DoubleListParameter(MEAN_ID, true);

  /**
   * Parameter for stddevs.
   */
  private final DoubleListParameter STDDEV_PARAM = new DoubleListParameter(STDDEV_ID, true);

  /**
   * Stores the mean in each dimension.
   */
  private double[] mean = new double[0];

  /**
   * Stores the standard deviation in each dimension.
   */
  private double[] stddev = new double[0];

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public AttributeWiseVarianceNormalization(Parameterization config) {
    super();
    config = config.descend(this);
    config.grab(MEAN_PARAM);
    config.grab(STDDEV_PARAM);
    if(MEAN_PARAM.isDefined() && STDDEV_PARAM.isDefined()) {
      List<Double> mean_list = MEAN_PARAM.getValue();
      List<Double> stddev_list = STDDEV_PARAM.getValue();

      mean = Util.unbox(mean_list.toArray(new Double[mean_list.size()]));
      stddev = Util.unbox(stddev_list.toArray(new Double[stddev_list.size()]));

      for(double d : stddev) {
        if(d == 0) {
          config.reportError(new WrongParameterValueException("Standard deviations must not be 0."));
        }
      }
    }

    ArrayList<Parameter<?, ?>> global_1 = new ArrayList<Parameter<?, ?>>();
    global_1.add(MEAN_PARAM);
    global_1.add(STDDEV_PARAM);
    config.checkConstraint(new AllOrNoneMustBeSetGlobalConstraint(global_1));

    ArrayList<ListParameter<?>> global = new ArrayList<ListParameter<?>>();
    global.add(MEAN_PARAM);
    global.add(STDDEV_PARAM);
    config.checkConstraint(new EqualSizeGlobalConstraint(global));
  }

  private double normalize(int d, double val) {
    return (val - mean[d]) / stddev[d];
  }

  private double restore(int d, double val) {
    return (val * stddev[d]) + mean[d];
  }

  @Override
  public List<Pair<V, DatabaseObjectMetadata>> normalizeObjects(List<Pair<V, DatabaseObjectMetadata>> objectAndAssociationsList) throws NonNumericFeaturesException {
    if(objectAndAssociationsList.size() == 0) {
      return new ArrayList<Pair<V, DatabaseObjectMetadata>>();
    }

    if(mean.length == 0 || stddev.length == 0) {
      determineMeanVariance(objectAndAssociationsList);
    }

    int dim = objectAndAssociationsList.get(0).getFirst().getDimensionality();
    if(dim != mean.length || dim != stddev.length) {
      throw new IllegalArgumentException("Dimensionalities do not agree!");
    }

    try {
      List<Pair<V, DatabaseObjectMetadata>> normalized = new ArrayList<Pair<V, DatabaseObjectMetadata>>();
      for(Pair<V, DatabaseObjectMetadata> objectAndAssociations : objectAndAssociationsList) {
        final V obj = objectAndAssociations.getFirst();
        double[] values = new double[obj.getDimensionality()];
        for(int d = 1; d <= obj.getDimensionality(); d++) {
          values[d - 1] = normalize(d - 1, obj.doubleValue(d));
        }

        V normalizedFeatureVector = obj.newInstance(values);
        normalizedFeatureVector.setID(obj.getID());
        DatabaseObjectMetadata associations = objectAndAssociations.getSecond();
        normalized.add(new Pair<V, DatabaseObjectMetadata>(normalizedFeatureVector, associations));
      }
      return normalized;
    }
    catch(Exception e) {
      throw new NonNumericFeaturesException("Attributes cannot be normalized.", e);
    }
  }

  @Override
  public List<V> normalize(List<V> featureVectors) throws NonNumericFeaturesException {
    if(featureVectors.size() == 0) {
      return new ArrayList<V>();
    }

    if(mean.length == 0 || stddev.length == 0) {
      determineMeanVariance(ClassGenericsUtil.toArray(featureVectors, NumberVector.class));
    }

    int dim = featureVectors.get(0).getDimensionality();
    if(dim != mean.length || dim != stddev.length) {
      throw new IllegalArgumentException("Dimensionalities do not agree!");
    }

    try {
      List<V> normalized = new ArrayList<V>();
      for(V featureVector : featureVectors) {
        double[] values = new double[featureVector.getDimensionality()];
        for(int d = 1; d <= featureVector.getDimensionality(); d++) {
          values[d - 1] = normalize(d - 1, featureVector.doubleValue(d));
        }
        V normalizedFeatureVector = featureVector.newInstance(values);
        normalizedFeatureVector.setID(featureVector.getID());
        normalized.add(normalizedFeatureVector);
      }
      return normalized;
    }
    catch(Exception e) {
      throw new NonNumericFeaturesException("Attributes cannot be normalized.", e);
    }
  }

  @Override
  public V restore(V featureVector) throws NonNumericFeaturesException {
    if(featureVector.getDimensionality() == mean.length) {
      double[] values = new double[featureVector.getDimensionality()];
      for(int d = 1; d <= featureVector.getDimensionality(); d++) {
        values[d - 1] = restore(d - 1, featureVector.doubleValue(d));
      }
      V restoredFeatureVector = featureVector.newInstance(values);
      restoredFeatureVector.setID(featureVector.getID());
      return restoredFeatureVector;
    }
    else {
      throw new NonNumericFeaturesException("Attributes cannot be resized: current dimensionality: " + featureVector.getDimensionality() + " former dimensionality: " + mean.length);
    }
  }

  @Override
  public List<V> restore(List<V> featureVectors) throws NonNumericFeaturesException {
    try {
      List<V> restored = new ArrayList<V>();
      for(V featureVector : featureVectors) {
        restored.add(restore(featureVector));
      }
      return restored;
    }
    catch(Exception e) {
      throw new NonNumericFeaturesException("Attributes cannot be resized.", e);
    }
  }

  @Override
  public LinearEquationSystem transform(LinearEquationSystem linearEquationSystem) {
    double[][] coeff = linearEquationSystem.getCoefficents();
    double[] rhs = linearEquationSystem.getRHS();
    int[] row = linearEquationSystem.getRowPermutations();
    int[] col = linearEquationSystem.getColumnPermutations();

    for(int i = 0; i < coeff.length; i++) {
      for(int r = 0; r < coeff.length; r++) {
        double sum = 0.0;
        for(int c = 0; c < coeff[0].length; c++) {
          sum += mean[c] * coeff[row[r]][col[c]] / stddev[c];
          coeff[row[r]][col[c]] = coeff[row[r]][col[c]] / stddev[c];
        }
        rhs[row[r]] = rhs[row[r]] + sum;
      }
    }

    LinearEquationSystem lq = new LinearEquationSystem(coeff, rhs, row, col);
    return lq;
  }

  /**
   * Returns a string representation of this normalization. The specified prefix
   * pre will be the prefix of each new line. This method is used to write the
   * parameters of a normalization to a result of an algorithm using this
   * normalization.
   * 
   * @param pre the prefix of each new line
   * @return a string representation of this normalization
   */
  @Override
  public String toString(String pre) {
    StringBuffer result = new StringBuffer();
    result.append(pre).append("normalization class: ").append(getClass().getName());
    result.append("\n");
    result.append(pre).append("normalization means: ").append(FormatUtil.format(mean));
    result.append("\n");
    result.append(pre).append("normalization stddevs: ").append(FormatUtil.format(stddev));

    return result.toString();
  }

  /**
   * Determines the mean and standard deviations in each dimension of the given
   * featureVectors.
   * 
   * @param featureVectors the list of feature vectors
   */
  private void determineMeanVariance(V[] featureVectors) {
    if(featureVectors.length == 0) {
      return;
    }
    int dimensionality = featureVectors[0].getDimensionality();
    MeanVariance[] mvs = MeanVariance.newArray(dimensionality);

    for(V featureVector : featureVectors) {
      for(int d = 1; d <= featureVector.getDimensionality(); d++) {
        mvs[d - 1].put(featureVector.doubleValue(d));
      }
    }

    mean = new double[dimensionality];
    stddev = new double[dimensionality];
    for(int d = 0; d < dimensionality; d++) {
      mean[d] = mvs[d].getMean();
      stddev[d] = mvs[d].getStddev();
      if(stddev[d] == 0) {
        stddev[d] = 1.0;
      }
    }
  }

  /**
   * Determines the means and standard deviations in each dimension of the given
   * featureVectors.
   * 
   * @param objectAndAssociationsList the list of feature vectors and their
   *        associations
   */
  private void determineMeanVariance(List<Pair<V, DatabaseObjectMetadata>> objectAndAssociationsList) {
    if(objectAndAssociationsList.isEmpty()) {
      return;
    }
    int dimensionality = objectAndAssociationsList.get(0).getFirst().getDimensionality();
    MeanVariance[] mvs = MeanVariance.newArray(dimensionality);

    for(Pair<V, DatabaseObjectMetadata> objectAndAssociations : objectAndAssociationsList) {
      V featureVector = objectAndAssociations.getFirst();
      for(int d = 1; d <= featureVector.getDimensionality(); d++) {
        mvs[d - 1].put(featureVector.doubleValue(d));
      }
    }

    mean = new double[dimensionality];
    stddev = new double[dimensionality];
    for(int d = 0; d < dimensionality; d++) {
      mean[d] = mvs[d].getMean();
      stddev[d] = mvs[d].getStddev();
      if(stddev[d] == 0) {
        stddev[d] = 1.0;
      }
    }
  }
}