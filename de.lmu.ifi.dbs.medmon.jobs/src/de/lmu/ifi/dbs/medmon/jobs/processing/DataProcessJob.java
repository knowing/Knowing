package de.lmu.ifi.dbs.medmon.jobs.processing;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.lmu.ifi.dbs.medmon.database.model.Data;

public class DataProcessJob extends Job {

	private static final DataProcessRule rule = new DataProcessRule();
	
	private final Data[] data;

	//private final IAlgorithm<Data> algorithm;
	//private final IAnalyzedData alreadyAnalyzed;

	//private IAnalyzedData analyzed;
	
	
	public DataProcessJob(String name, Data[] data) {
		super(name);
		this.data = data;
		//this.algorithm = algorithm;		IAlgorithm<Data> algorithm,
		//this.alreadyAnalyzed = analyzed;	IAnalyzedData analyzed
		setRule(rule);
		
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// DUMMY
		return Status.OK_STATUS;
	}
	
/*	public DataProcessJob(String name, Data[] data, IAlgorithm<Data> algorithm) {
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
	}*/
	

}
