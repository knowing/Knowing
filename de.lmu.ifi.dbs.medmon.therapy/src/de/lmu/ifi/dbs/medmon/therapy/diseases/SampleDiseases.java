package de.lmu.ifi.dbs.medmon.therapy.diseases;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.algorithm.util.AlgorithmUtil;
import de.lmu.ifi.dbs.medmon.therapy.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.ITherapy;

public class SampleDiseases implements IDisease {

	@Override
	public String getName() {
		return "Stubenhocker";
	}

	@Override
	public String getDescription() {
		return "Sitzt zu viel vor dem Computer und bewegt sich nicht genuegend";
	}

	@Override
	public ITherapy[] getTherapies() {
		ISensorDataAlgorithm algorithm = AlgorithmUtil.findAlgorithm("Simple Analyzer");
		return new ITherapy[] { new SampleTherapy(this, algorithm), new SampleTherapy(this, algorithm)};
	}

}
