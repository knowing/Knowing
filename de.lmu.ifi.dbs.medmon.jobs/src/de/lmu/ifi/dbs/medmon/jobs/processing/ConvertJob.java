package de.lmu.ifi.dbs.medmon.jobs.processing;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

public class ConvertJob extends Job {

	private static final ConvertRule rule = new ConvertRule();
	
	private final IConverter<Object> converter;
	private final ISensorDataContainer<Object> container;
	private final String family;
	
	private Object[] convertedData;

	public ConvertJob(String name, ISensorDataContainer<Object> container, IConverter<Object> converter, String family) {
		super(name);
		this.container = container;
		this.converter = converter;
		this.family = family;
		setRule(rule);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Converting...", IProgressMonitor.UNKNOWN);
		try {
			convertedData = container.getSensorData(converter);
		} catch (IOException e) {
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
	
	@Override
	public boolean belongsTo(Object family) {
		return family.equals(this.family);
	}


	public Object[] getConvertedData() {
		return convertedData;
	}

}
