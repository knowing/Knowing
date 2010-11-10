package de.lmu.ifi.dbs.medmon.sensor.core.jobs;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

public class ConvertJob extends Job {

	private static final ConvertRule rule = new ConvertRule();
	
	private final IConverter<Data> converter;
	private final ISensorDataContainer<Data> container;
	
	private Data[] convertedData;

	public ConvertJob(String name, ISensorDataContainer<Data> container, IConverter<Data> converter) {
		super(name);
		this.container = container;
		this.converter = converter;
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


	public Data[] getConvertedData() {
		return convertedData;
	}

}