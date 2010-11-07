package de.sendsor.accelerationSensor.algorithm;

import java.util.Comparator;
import java.util.AbstractMap.SimpleEntry;

public class KeyComp implements Comparator<SimpleEntry<Double, ?>> {

	@Override
	public int compare(SimpleEntry<Double, ?> o1, SimpleEntry<Double, ?> o2) {
		return o1.getKey().compareTo(o2.getKey());
	}
}
