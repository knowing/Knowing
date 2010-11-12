package de.lmu.ifi.dbs.medmon.jobs.persistence;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import de.lmu.ifi.dbs.medmon.jobs.processing.ConvertRule;

public class PersistRule implements ISchedulingRule {

	@Override
	public boolean contains(ISchedulingRule rule) {
		if(rule instanceof PersistRule)
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
