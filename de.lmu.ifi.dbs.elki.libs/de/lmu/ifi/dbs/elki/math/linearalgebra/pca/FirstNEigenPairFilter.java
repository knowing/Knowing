package de.lmu.ifi.dbs.elki.math.linearalgebra.pca;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.math.linearalgebra.EigenPair;
import de.lmu.ifi.dbs.elki.math.linearalgebra.SortedEigenPairs;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * The FirstNEigenPairFilter marks the n highest eigenpairs as strong
 * eigenpairs, where n is a user specified number.
 * 
 * @author Elke Achtert
 */
// todo parameter comments
@Title("First n Eigenpair filter")
@Description("Sorts the eigenpairs in decending order of their eigenvalues and marks the first n eigenpairs as strong eigenpairs.")
public class FirstNEigenPairFilter implements EigenPairFilter, Parameterizable {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(FirstNEigenPairFilter.class);
  
  /**
   * OptionID for {@link #N_PARAM}
   */
  public static final OptionID EIGENPAIR_FILTER_N = OptionID.getOrCreateOptionID("pca.filter.n", "The number of strong eigenvectors: n eigenvectors with the n highest" + "eigenvalues are marked as strong eigenvectors.");

  /**
   * Parameter n.
   */
  private final IntParameter N_PARAM = new IntParameter(EIGENPAIR_FILTER_N, new GreaterEqualConstraint(0));

  /**
   * The threshold for strong eigenvectors: n eigenvectors with the n highest
   * eigenvalues are marked as strong eigenvectors.
   */
  private double n;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public FirstNEigenPairFilter(Parameterization config) {
    super();
    config = config.descend(this);
    // this.debug = true;
    if (config.grab(N_PARAM)) {
      n = N_PARAM.getValue();
    }
  }

  @Override
  public FilteredEigenPairs filter(SortedEigenPairs eigenPairs) {
    StringBuffer msg = new StringBuffer();
    if(logger.isDebugging()) {
      msg.append("sortedEigenPairs ").append(eigenPairs.toString());
      msg.append("\nn = ").append(n);
    }

    // init strong and weak eigenpairs
    List<EigenPair> strongEigenPairs = new ArrayList<EigenPair>();
    List<EigenPair> weakEigenPairs = new ArrayList<EigenPair>();

    // determine strong and weak eigenpairs
    for(int i = 0; i < eigenPairs.size(); i++) {
      EigenPair eigenPair = eigenPairs.getEigenPair(i);
      if(i < n) {
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