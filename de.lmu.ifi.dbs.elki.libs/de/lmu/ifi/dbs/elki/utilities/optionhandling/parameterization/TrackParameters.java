package de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.lmu.ifi.dbs.elki.logging.LoggingUtil;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.InternalParameterizationErrors;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GlobalParameterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Utility wrapper to track parameters for a configuration session.
 * 
 * All actual Parameterization operations are forwarded to the inner class.
 * 
 * @author Erich Schubert
 */
public class TrackParameters implements Parameterization {
  /**
   * Inner parameterization
   */
  Parameterization inner;

  /**
   * Tracking storage
   */
  java.util.Vector<Pair<Object, Parameter<?, ?>>> options = new java.util.Vector<Pair<Object, Parameter<?, ?>>>();

  /**
   * Tree information: parent links
   */
  Map<Object, Object> parents = new HashMap<Object, Object>();

  /**
   * Tree information: child links
   */
  // Implementation note: we need the map to support "null" keys!
  Map<Object, List<Object>> children = new HashMap<Object, List<Object>>();

  /**
   * Current parent for nested parameterization
   */
  Object cur = null;

  /**
   * Constructor.
   * 
   * @param inner Inner parameterization to wrap.
   */
  public TrackParameters(Parameterization inner) {
    super();
    this.inner = inner;
  }

  /**
   * Internal constructor, for nested tracking.
   * 
   * @param inner
   * @param option
   * @param options
   * @param parents
   * @param children
   */
  private TrackParameters(Parameterization inner, Object option, Vector<Pair<Object, Parameter<?, ?>>> options, Map<Object, Object> parents, Map<Object, List<Object>> children) {
    super();
    this.inner = inner.descend(option);
    this.cur = option;
    this.options = options;
    this.parents = parents;
    this.children = children;
  }

  @Override
  public Collection<ParameterException> getErrors() {
    return inner.getErrors();
  }

  @Override
  public boolean hasErrors() {
    return inner.hasErrors();
  }

  @Override
  public boolean grab(Parameter<?, ?> opt) {
    registerChild(opt);
    options.add(new Pair<Object, Parameter<?, ?>>(cur, opt));
    return inner.grab(opt);
  }

  @Override
  public boolean hasUnusedParameters() {
    return inner.hasUnusedParameters();
  }

  @Override
  public void reportError(ParameterException e) {
    inner.reportError(e);
  }

  @Override
  public boolean setValueForOption(Parameter<?, ?> opt) throws ParameterException {
    registerChild(opt);
    options.add(new Pair<Object, Parameter<?, ?>>(cur, opt));
    return inner.setValueForOption(opt);
  }

  /**
   * Get all seen parameters, set or unset, along with their owner objects.
   * 
   * @return Parameters seen
   */
  public Collection<Pair<Object, Parameter<?, ?>>> getAllParameters() {
    return options;
  }

  /**
   * Get the tracked parameters that were actually set.
   * 
   * @return Parameters given
   */
  public Collection<Pair<OptionID, Object>> getGivenParameters() {
    java.util.Vector<Pair<OptionID, Object>> ret = new java.util.Vector<Pair<OptionID, Object>>();
    for(Pair<Object, Parameter<?, ?>> pair : options) {
      if(pair.second.isDefined() && pair.second.getGivenValue() != null) {
        ret.add(new Pair<OptionID, Object>(pair.second.getOptionID(), pair.second.getGivenValue()));
      }
    }
    return ret;
  }

  /** {@inheritDoc} */
  @Override
  public boolean checkConstraint(GlobalParameterConstraint constraint) {
    return inner.checkConstraint(constraint);
  }

  /**
   * {@inheritDoc} Track parameters using a shared options list with parent
   * tracker.
   */
  @Override
  public Parameterization descend(Object option) {
    registerChild(option);
    return new TrackParameters(inner, option, options, parents, children);
  }

  private void registerChild(Object opt) {
    if(opt == cur) {
      LoggingUtil.exception("Options shouldn't have themselves as parents!", new Throwable());
    }
    parents.put(opt, cur);
    List<Object> c = children.get(cur);
    if(c == null) {
      c = new java.util.Vector<Object>();
      children.put(cur, c);
    }
    if(!c.contains(opt)) {
      c.add(opt);
    }
  }

  /**
   * Traverse the tree upwards.
   * 
   * @param pos Current object
   * @return Parent object
   */
  public Object getParent(Object pos) {
    return parents.get(pos);
  }

  @Override
  public <C> C tryInstantiate(Class<C> r, Class<?> c) {
    try {
      return ClassGenericsUtil.tryInstantiate(r, c, this);
    }
    catch(Exception e) {
      reportError(new InternalParameterizationErrors("Error instantiating internal class.", e));
      return null;
    }
  }
}