package de.lmu.ifi.dbs.medmon.medic.core.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class ConvertCSVJob extends Job {

	public ConvertCSVJob(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return null;
	}
	
}
