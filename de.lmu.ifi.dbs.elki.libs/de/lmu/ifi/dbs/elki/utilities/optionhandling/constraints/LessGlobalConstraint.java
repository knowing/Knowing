package de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints;

import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.NumberParameter;

/**
 * Represents a Less-Than global parameter constraint. The value of the first
 * number parameter ({@link NumberParameter}) given has to be less than the
 * value of the second number parameter ({@link NumberParameter}) given.
 * 
 * @author Steffi Wanka
 * @param <T> Number type
 */
public class LessGlobalConstraint<T extends Number> implements GlobalParameterConstraint {
  /**
   * First number parameter.
   */
  private NumberParameter<T> first;

  /**
   * Second number parameter.
   */
  private NumberParameter<T> second;

  /**
   * Creates a Less-Than global parameter constraint. That is the value of the
   * first number parameter given has to be less than the value of the second
   * number parameter given.
   * 
   * @param first first number parameter
   * @param second second number parameter
   */
  public LessGlobalConstraint(NumberParameter<T> first, NumberParameter<T> second) {
    this.first = first;
    this.second = second;
  }

  /**
   * Checks if the value of the first number parameter is less than the value of
   * the second number parameter. If not, a parameter exception is thrown.
   * 
   */
  @Override
  public void test() throws ParameterException {
    if(first.isDefined() && second.isDefined()) {
      if(first.getValue().doubleValue() >= second.getValue().doubleValue()) {
        throw new WrongParameterValueException("Global Parameter Constraint Error: \n" + "The value of parameter \"" + first.getName() + "\" has to be less than the" + "value of parameter \"" + second.getName() + "\"" + "(Current values: " + first.getName() + ": " + first.getValue().doubleValue() + ", " + second.getName() + ": " + second.getValue().doubleValue() + ")\n");
      }
    }
  }

  @Override
  public String getDescription() {
    return first.getName() + " < " + second.getName();
  }
}