package de.sendsor.accelerationSensor.therapy;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.medic.core.extensions.IDisease;
import de.lmu.ifi.dbs.medmon.medic.core.extensions.ITherapy;

public class DefaultTherapy implements ITherapy{

	private final IDisease disease;
	private final IAlgorithm algorithm;
	
	public DefaultTherapy(IDisease disease, IAlgorithm algorithm) {
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
		return algorithm.getName();
	}

	@Override
	public String getDescription() {
		return algorithm.getDescription();
	}

}
