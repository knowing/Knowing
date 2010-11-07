package de.lmu.ifi.dbs.medmon.sensor.core.jobs;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class DataProcessRule implements ISchedulingRule {

	@Override
	public boolean contains(ISchedulingRule rule) {
		if(rule instanceof DataProcessRule)
			return true;
		return false;
	}

	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		if(rule instanceof ConvertRule)
			return true;
		return false;
	}

}
