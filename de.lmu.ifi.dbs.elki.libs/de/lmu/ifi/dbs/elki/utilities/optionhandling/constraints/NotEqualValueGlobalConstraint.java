package de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.NumberParameter;

/**
 * Global parameter constraint specifying that parameters of a list of number
 * parameters ({@link NumberParameter}) are not allowed to have the same value.
 * 
 * @author Steffi Wanka
 * @param <N> Number type
 */
public class NotEqualValueGlobalConstraint<N extends Number> implements GlobalParameterConstraint {
  /**
   * List of number parameters to be checked.
   */
  private List<NumberParameter<N>> parameters;

  /**
   * Constructs a Not-Equal-Value global parameter constraint. That is, the
   * elements of a list of number parameters are not allowed to have equal
   * values.
   * 
   * @param parameters list of number parameters to be tested
   */
  public NotEqualValueGlobalConstraint(List<NumberParameter<N>> parameters) {
    this.parameters = parameters;
  }

  /**
   * Checks if the elements of the list of number parameters do have different
   * values. If not, a parameter exception is thrown.
   * 
   */
  @Override
  public void test() throws ParameterException {
    Set<Number> numbers = new HashSet<Number>();

    for(NumberParameter<N> param : parameters) {
      if(param.isDefined()) {
        if(!numbers.add(param.getValue())) {
          throw new WrongParameterValueException("Global Parameter Constraint Error:\n" + "Parameters " + OptionUtil.optionsNamesToString(parameters) + " must have different values. Current values: " + OptionUtil.parameterNamesAndValuesToString(parameters) + ".\n");
        }
      }
    }
  }

  @Override
  public String getDescription() {
    return "Parameters " + OptionUtil.optionsNamesToString(parameters) + " must have different values.";
  }
}