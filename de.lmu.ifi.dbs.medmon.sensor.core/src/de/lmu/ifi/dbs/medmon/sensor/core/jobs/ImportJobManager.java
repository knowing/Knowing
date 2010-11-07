package de.lmu.ifi.dbs.medmon.sensor.core.jobs;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAlgorithm;

public class ImportJobManager {

	private final ISensorDataContainer<Data> container[];
	
	private final IAlgorithm<Data> algorithm;

	private final IConverter<Data> converter;

	public ImportJobManager(ISensorDataContainer<Data> container[], IAlgorithm<Data> algorithm, IConverter<Data> converter) {
		this.container = container;
		this.algorithm = algorithm;
		this.converter = converter;
	}
	
	public void run() {
		for (ISensorDataContainer<Data> c : container) {
			ConvertJob convertJob = new ConvertJob("Convert",c, converter);
			convertJob.schedule();
			Data[] data = convertJob.getConvertedData();
			PersistJob persistJob = new PersistJob("Persist", data);
			DataProcessJob processJob = new DataProcessJob("Analyze", data, algorithm);
			
			persistJob.schedule();
			processJob.schedule();
		}
	}
	
	//Convert Data
	
	//Analyze it & persist it
}
