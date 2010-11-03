package de.sendsor.accelerationSensor.therapy;

import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.sensor.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.ITherapy;
import de.sendsor.accelerationSensor.algorithm.PieAnalyzer;
import de.sendsor.accelerationSensor.algorithm.SimpleAnalyzer;

public class DefaultDisease implements IDisease {

	@Override
	public String getName() {
		return "Reine Auswertung";
	}

	@Override
	public String getDescription() {
		return "Die verschiedenen Bewegungskategorien werden angezeigt";
	}

	@Override
	public ITherapy[] getTherapies() {
		ITherapy[] therapies = new ITherapy[2];
		IAlgorithm alg1 = FrameworkUtil.findAlgorithm(SimpleAnalyzer.NAME);
		IAlgorithm alg2 = FrameworkUtil.findAlgorithm(PieAnalyzer.NAME);
		therapies[0] = new DefaultTherapy(this, alg1);
		therapies[1] = new DefaultTherapy(this, alg2);
		return therapies;
	}

}
