package de.sendsor.accelerationSensor.therapy;

import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.sensor.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.ITherapy;

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
		ITherapy[] therapies = new ITherapy[1];
		IAlgorithm algorithm = FrameworkUtil.findAlgorithm("Simple Analyzer");
		therapies[0] = new DefaultTherapy(this, algorithm);
		return therapies;
	}

}
