package de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization;

import java.util.Vector;

import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;

/**
 * Class that allows chaining multiple parameterizations.
 * This is designed to allow overriding of some parameters for an algorithm,
 * while other can be configured via different means, e.g. given by the
 * user on the command line.
 * 
 * See {@link de.lmu.ifi.dbs.elki.utilities.optionhandling} package documentation
 * for examples.
 * 
 * @author Erich Schubert
 */
public class ChainedParameterization extends AbstractParameterization {
  /**
   * Keep the list of parameterizations.
   */
  private Vector<Parameterization> chain = new Vector<Parameterization>();
  
  /**
   * Error target
   */
  private Parameterization errorTarget = this;

  /**
   * Constructor that takes a number of Parameterizations to chain.
   * 
   * @param ps Parameterizations
   */
  public ChainedParameterization(Parameterization... ps) {
    for(Parameterization p : ps) {
      chain.add(p);
    }
    //logger.warning("Chain length: "+chain.size()+ " for "+this);
  }

  /**
   * Append a new Parameterization to the chain.
   * 
   * @param p Parameterization
   */
  public void appendParameterization(Parameterization p) {
    chain.add(p);
    //logger.warning("Chain length: "+chain.size()+ " for "+this);
  }
  
  @Override
  public boolean setValueForOption(Parameter<?,?> opt) throws ParameterException {
    for(Parameterization p : chain) {
      if(p.setValueForOption(opt)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasUnusedParameters() {
    for(Parameterization p : chain) {
      if(p.hasUnusedParameters()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Set the error target, since there is no unique way where
   * errors can be reported.
   * 
   * @param config Parameterization to report errors to
   */
  public void errorsTo(Parameterization config) {
    this.errorTarget = config;
  }

  /** {@inheritDoc} */
  @Override
  public void reportError(ParameterException e) {
    if (this.errorTarget == this) {
      super.reportError(e);
    } else {
      this.errorTarget.reportError(e);
    }
  }

  /** {@inheritDoc}
   * Parallel descend in all chains.
   */
  @Override
  public Parameterization descend(Object option) {
    ChainedParameterization n = new ChainedParameterization();
    n.errorsTo(this.errorTarget);
    for (Parameterization p : this.chain) {
      n.appendParameterization(p.descend(option));
    }
    return n;
  }
}