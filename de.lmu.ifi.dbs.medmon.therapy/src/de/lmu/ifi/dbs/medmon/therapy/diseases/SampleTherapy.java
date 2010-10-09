package de.lmu.ifi.dbs.medmon.therapy.diseases;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.therapy.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.ITherapy;

public class SampleTherapy implements ITherapy {

	private final IDisease disease;
	private final ISensorDataAlgorithm algorithm;

	public SampleTherapy(IDisease disease, ISensorDataAlgorithm algorithm) {
		this.disease = disease;
		this.algorithm = algorithm;
	}
	
	@Override
	public IDisease getDisease() {
		return disease;
	}

	@Override
	public ISensorDataAlgorithm getAnalysers() {
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
