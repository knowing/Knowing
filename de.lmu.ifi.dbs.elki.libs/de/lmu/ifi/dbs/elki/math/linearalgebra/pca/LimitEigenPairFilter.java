package de.lmu.ifi.dbs.elki.math.linearalgebra.pca;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.math.linearalgebra.EigenPair;
import de.lmu.ifi.dbs.elki.math.linearalgebra.SortedEigenPairs;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GlobalParameterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.LessEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.ParameterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.ParameterFlagGlobalConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;

/**
 * The LimitEigenPairFilter marks all eigenpairs having an (absolute) eigenvalue
 * below the specified threshold (relative or absolute) as weak eigenpairs, the
 * others are marked as strong eigenpairs.
 * 
 * @author Elke Achtert
 */
@Title("Limit-based Eigenpair Filter")
@Description("Filters all eigenpairs, which are lower than a given value.")
public class LimitEigenPairFilter implements EigenPairFilter, Parameterizable {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(LimitEigenPairFilter.class);
  
  /**
   * OptionID for {@link #ABSOLUTE_FLAG}
   */
  public static final OptionID EIGENPAIR_FILTER_ABSOLUTE = OptionID.getOrCreateOptionID("pca.filter.absolute", "Flag to mark delta as an absolute value.");

  /**
   * OptionID for {@link #DELTA_PARAM}
   */
  public static final OptionID EIGENPAIR_FILTER_DELTA = OptionID.getOrCreateOptionID("pca.filter.delta", "The threshold for strong Eigenvalues. If not otherwise specified, delta " + "is a relative value w.r.t. the (absolute) highest Eigenvalues and has to be " + "a double between 0 and 1. To mark delta as an absolute value, use " + "the option -" + EIGENPAIR_FILTER_ABSOLUTE.getName() + ".");

  /**
   * "absolute" Flag
   */
  private final Flag ABSOLUTE_FLAG = new Flag(EIGENPAIR_FILTER_ABSOLUTE);

  /**
   * The default value for delta.
   */
  public static final double DEFAULT_DELTA = 0.01;

  /**
   * Parameter delta
   */
  private final DoubleParameter DELTA_PARAM = new DoubleParameter(EIGENPAIR_FILTER_DELTA, new GreaterEqualConstraint(0), DEFAULT_DELTA);

  /**
   * Threshold for strong eigenpairs, can be absolute or relative.
   */
  private double delta;

  /**
   * Indicates whether delta is an absolute or a relative value.
   */
  private boolean absolute;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public LimitEigenPairFilter(Parameterization config) {
    super();
    config = config.descend(this);

    if (config.grab(ABSOLUTE_FLAG)) {
      absolute = ABSOLUTE_FLAG.getValue();
    }

    if(config.grab(DELTA_PARAM)) {
      delta = DELTA_PARAM.getValue();
      // TODO: make this a global constraint?
      if(absolute && DELTA_PARAM.tookDefaultValue()) {
        config.reportError(new WrongParameterValueException("Illegal parameter setting: " + "Flag " + ABSOLUTE_FLAG.getName() + " is set, " + "but no value for " + DELTA_PARAM.getName() + " is specified."));
      }
    }

    // Conditional Constraint:
    // delta must be >= 0 and <= 1 if it's a relative value
    // Since relative or absolute is dependent on the absolute flag this is a
    // global constraint!
    List<ParameterConstraint<Number>> cons = new Vector<ParameterConstraint<Number>>();
    // TODO: Keep the constraint here - applies to non-conditional case as well,
    // and is set above.
    ParameterConstraint<Number> aboveNull = new GreaterEqualConstraint(0);
    cons.add(aboveNull);
    ParameterConstraint<Number> underOne = new LessEqualConstraint(1);
    cons.add(underOne);

    GlobalParameterConstraint gpc = new ParameterFlagGlobalConstraint<Number, Double>(DELTA_PARAM, cons, ABSOLUTE_FLAG, false);
    config.checkConstraint(gpc);
  }

  @Override
  public FilteredEigenPairs filter(SortedEigenPairs eigenPairs) {
    StringBuffer msg = new StringBuffer();
    if(logger.isDebugging()) {
      msg.append("delta = ").append(delta);
    }

    // determine limit
    double limit;
    if(absolute) {
      limit = delta;
    }
    else {
      double max = Double.NEGATIVE_INFINITY;
      for(int i = 0; i < eigenPairs.size(); i++) {
        EigenPair eigenPair = eigenPairs.getEigenPair(i);
        double eigenValue = Math.abs(eigenPair.getEigenvalue());
        if(max < eigenValue) {
          max = eigenValue;
        }
      }
      limit = max * delta;
    }
    if(logger.isDebugging()) {
      msg.append("\nlimit = ").append(limit);
    }

    // init strong and weak eigenpairs
    List<EigenPair> strongEigenPairs = new ArrayList<EigenPair>();
    List<EigenPair> weakEigenPairs = new ArrayList<EigenPair>();

    // determine strong and weak eigenpairs
    for(int i = 0; i < eigenPairs.size(); i++) {
      EigenPair eigenPair = eigenPairs.getEigenPair(i);
      double eigenValue = Math.abs(eigenPair.getEigenvalue());
      if(eigenValue >= limit) {
        strongEigenPairs.add(eigenPair);
      }
      else {
        weakEigenPairs.add(eigenPair);
      }
    }
    if(logger.isDebugging()) {
      msg.append("\nstrong EigenPairs = ").append(strongEigenPairs);
      msg.append("\nweak EigenPairs = ").append(weakEigenPairs);
      logger.debugFine(msg.toString());
    }

    return new FilteredEigenPairs(weakEigenPairs, strongEigenPairs);
  }
}