package de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints;

import java.util.List;
import java.util.Vector;

import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;

/**
 * Global parameter constraint specifying that only one parameter of a list of
 * parameters ({@link Parameter}) is allowed to be set.
 * 
 * @author Steffi Wanka
 */
public class OnlyOneIsAllowedToBeSetGlobalConstraint implements GlobalParameterConstraint {
  /**
   * List of parameters to be checked.
   */
  private List<Parameter<?, ?>> parameters;

  /**
   * Constructs a global parameter constraint for testing if only one parameter
   * of a list of parameters is set.
   * 
   * @param params list of parameters to be checked
   */
  public OnlyOneIsAllowedToBeSetGlobalConstraint(List<Parameter<?, ?>> params) {
    parameters = params;
  }

  /**
   * Checks if only one parameter of a list of parameters is set. If not, a
   * parameter exception is thrown.
   * 
   */
  @Override
  public void test() throws ParameterException {
    Vector<String> set = new Vector<String>();
    for(Parameter<?, ?> p : parameters) {
      if(p.isDefined()) {
        // FIXME: Retire the use of this constraint for Flags!
        if(p instanceof Flag) {
          if (((Flag)p).getValue()) {
            set.add(p.getName());
          }
        } else {
          set.add(p.getName());
        }
      }
    }
    if(set.size() > 1) {
      throw new WrongParameterValueException("Global Parameter Constraint Error.\n" + "Only one of the parameters " + OptionUtil.optionsNamesToString(parameters) + " is allowed to be set. " + "Parameters currently set: " + set.toString());
    }
  }

  @Override
  public String getDescription() {
    return "Only one of the parameters " + OptionUtil.optionsNamesToString(parameters) + " is allowed to be set.";
  }
}