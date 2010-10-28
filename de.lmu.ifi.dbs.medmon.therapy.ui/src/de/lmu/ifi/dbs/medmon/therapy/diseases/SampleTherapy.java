package de.lmu.ifi.dbs.medmon.therapy.diseases;

import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.ITherapy;

public class SampleTherapy implements ITherapy {

	private final IDisease disease;
	private final IAlgorithm algorithm;

	public SampleTherapy(IDisease disease, IAlgorithm algorithm) {
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
		return "Tanzen";
	}

	@Override
	public String getDescription() {
		return "Bewegung durch Tanzen";
	}

}
