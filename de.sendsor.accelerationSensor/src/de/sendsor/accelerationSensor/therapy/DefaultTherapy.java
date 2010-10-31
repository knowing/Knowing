package de.sendsor.accelerationSensor.therapy;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.ITherapy;

public class DefaultTherapy implements ITherapy{

	private final IDisease disease;
	private final IAlgorithm<Data> algorithm;	
	
	public DefaultTherapy(IDisease disease, IAlgorithm<Data> algorithm) {
		this.disease = disease;
		this.algorithm = algorithm;
	}

	@Override
	public IDisease getDisease() {
		return disease;
	}

	@Override
	public IAlgorithm getAnalysers() {
		return algorithm;
	}

	@Override
	public String getName() {
		return "Zeitanalyse";
	}

	@Override
	public String getDescription() {
		return "";
	}

}
