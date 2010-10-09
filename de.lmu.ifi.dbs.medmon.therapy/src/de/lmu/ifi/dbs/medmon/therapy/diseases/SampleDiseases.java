package de.lmu.ifi.dbs.medmon.therapy.diseases;

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
		return new ITherapy[] { new SampleTherapy(this, null), new SampleTherapy(this, null)};
	}

}
