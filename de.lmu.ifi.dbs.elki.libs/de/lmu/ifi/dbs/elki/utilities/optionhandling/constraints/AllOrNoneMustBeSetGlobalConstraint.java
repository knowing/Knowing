package de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints;

import java.util.List;
import java.util.Vector;

import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;

/**
 * Global parameter constraint specifying that either all elements of a list of
 * parameters (
 * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter})
 * must be set, or none of them.
 * 
 * @author Steffi Wanka
 */
public class AllOrNoneMustBeSetGlobalConstraint implements GlobalParameterConstraint {
  /**
   * List of parameters to be checked
   */
  private List<Parameter<?, ?>> parameterList;

  /**
   * Constructs a global parameter constraint for testing if either all elements
   * of a list of parameters are set or none of them.
   * 
   * @param parameters list of parameters to be checked
   */
  public AllOrNoneMustBeSetGlobalConstraint(List<Parameter<?, ?>> parameters) {
    this.parameterList = parameters;
  }

  /**
   * Checks if either all elements of a list of parameters are set, or none of
   * them. If not, a parameter exception is thrown.
   */
  @Override
  public void test() throws ParameterException {

    Vector<String> set = new Vector<String>();
    Vector<String> notSet = new Vector<String>();

    for(Parameter<?, ?> p : parameterList) {
      if(p.isDefined()) {
        set.add(p.getName());
      }
      else {
        notSet.add(p.getName());
      }
    }
    if(!set.isEmpty() && !notSet.isEmpty()) {
      throw new WrongParameterValueException("Global Constraint Error.\n" + "Either all of the parameters " + OptionUtil.optionsNamesToString(parameterList) + " must be set or none of them. " + "Parameter(s) currently set: " + set.toString() + ", parameters currently " + "not set: " + notSet.toString());
    }
  }

  @Override
  public String getDescription() {
    return "Either all of the parameters " + OptionUtil.optionsNamesToString(parameterList) + " must be set or none of them. ";
  }
}
