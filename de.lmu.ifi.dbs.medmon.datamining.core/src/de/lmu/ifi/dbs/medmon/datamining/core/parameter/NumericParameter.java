package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an AlgorithmParameter for {@link ISensorDataAlgorithm} in order to
 * be able to display and configure it.
 * 
 * @author Nepomuk Seiler
 * @version 1.1
 */
public class NumericParameter implements IProcessorParameter<Integer> {

	private final String name;

	private final ArrayList<Integer> values;
	private final int minimum;
	private final int maximum;

	private Integer value;

	/**
	 * Standard configuration: <br>
	 * <code>minimum = 0</code><br>
	 * <code>maximum = Integer.MAX_VALUE</code><br>
	 * <code>value = 0</code><br>
	 */
	public NumericParameter(String name) {
		this(name, 0, Integer.MAX_VALUE, 0);
	}

	public NumericParameter(String name, int minimum, int maximum) {
		this(name, minimum, maximum, (Math.abs(maximum + minimum) / 2));
	}

	public NumericParameter(String name, int minimum, int maximum, int value) {
		this.name = name;
		this.minimum = minimum;
		this.maximum = maximum;
		this.value = value;
		values = new ArrayList<Integer>();
		initValues();
	}

	private void initValues() {
		values.add(minimum);
		values.add(maximum);
		values.add(value);
	}

	public int getMinimum() {
		return minimum;
	}

	public int getMaximum() {
		return maximum;
	}

	@Override
	public void setValue(Integer value) {
		if(isValid(value))
			this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public Integer[] getValues() {
		return values.toArray(new Integer[values.size()]);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isValid(Integer value) {
		if(value <= maximum || value >= minimum)
			return true;
		return false;
	}

}
