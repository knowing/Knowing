package de.lmu.ifi.dbs.medmon.jobs.factory;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.jobs.persistence.PersistJob;
import de.lmu.ifi.dbs.medmon.jobs.processing.ConvertJob;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

public class ImportJobManager {

	private final ISensorDataContainer<Data> container[];
	
	//private final IAlgorithm<Data> algorithm;

	private final IConverter<Data> converter;

	public ImportJobManager(ISensorDataContainer<Data> container[],  IConverter<Data> converter) {
		this.container = container;
		//this.algorithm = algorithm; Parameter: IAlgorithm<Data> algorithm,
		this.converter = converter;
	}
	
	public void run() {
		for (ISensorDataContainer<Data> c : container) {
			ConvertJob convertJob = new ConvertJob("Convert",c, converter);
			convertJob.schedule();
			Data[] data = convertJob.getConvertedData();
			PersistJob persistJob = new PersistJob("Persist", data);
			//DataProcessJob processJob = new DataProcessJob("Analyze", data, algorithm);
			
			persistJob.schedule();
			//processJob.schedule();
		}
	}
	
	//Convert Data
	
	//Analyze it & persist it
}
