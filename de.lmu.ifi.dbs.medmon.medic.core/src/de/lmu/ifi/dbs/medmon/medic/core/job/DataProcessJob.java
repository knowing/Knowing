package de.lmu.ifi.dbs.medmon.medic.core.job;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.Processor;

public class DataProcessJob extends Job {

	private static final DataProcessRule rule = new DataProcessRule();
	
	private final RawData data;
	private final DataProcessingUnit dpu;
	private final Processor processor;
	private final Map<String, IAnalyzedData> acc;

	private final String family;

	public DataProcessJob(String name, DataProcessingUnit dpu, Processor processor, RawData data, Map<String, IAnalyzedData> acc, String family) {
		super(name);
		this.dpu = dpu;
		this.processor = processor;
		this.data = data;
		this.acc = acc;
		this.family = family;
		setRule(rule);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		return Status.OK_STATUS;
	}
	
	@Override
	public boolean belongsTo(Object family) {
		return family.equals(this.family);
	}
	
	public Map<String, IAnalyzedData> getAcc() {
		return acc;
	}

}
