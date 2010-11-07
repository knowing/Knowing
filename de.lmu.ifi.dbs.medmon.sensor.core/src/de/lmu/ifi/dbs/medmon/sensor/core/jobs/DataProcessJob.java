package de.lmu.ifi.dbs.medmon.sensor.core.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAnalyzedData;

public class DataProcessJob extends Job {

	private static final DataProcessRule rule = new DataProcessRule();
	
	private final Data[] data;

	private final IAlgorithm<Data> algorithm;
	private final IAnalyzedData alreadyAnalyzed;

	private IAnalyzedData analyzed;
	
	
	public DataProcessJob(String name, Data[] data, IAlgorithm<Data> algorithm, IAnalyzedData analyzed) {
		super(name);
		this.data = data;
		this.algorithm = algorithm;
		this.alreadyAnalyzed = analyzed;
		setRule(rule);
		
	}
	
	public DataProcessJob(String name, Data[] data, IAlgorithm<Data> algorithm) {
		this(name, data, algorithm, null);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("analyze", IProgressMonitor.UNKNOWN);
		//TODO Process-Chaining
		if(alreadyAnalyzed == null)
			analyzed = algorithm.process(data);
		else
			analyzed = algorithm.process(data, alreadyAnalyzed);
		
		return Status.OK_STATUS;
	}

	public IAnalyzedData getAnalyzed() {
		return analyzed;
	}
	

}
