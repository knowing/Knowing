package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import de.lmu.ifi.dbs.medmon.medic.ui.pages.DataDetailPage;
import de.lmu.ifi.dbs.medmon.medic.ui.pages.SensorDetailPage;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SensorDetailPageProvider implements IDetailsPageProvider {

	@Override
	public Object getPageKey(Object object) {
		if(object instanceof ISensor)
			return ISensor.class;
		if(object instanceof ISensorDataContainer)
			return ISensorDataContainer.class;
		return null;
	}

	@Override
	public IDetailsPage getPage(Object key) {
		if(key == ISensor.class)
			return new SensorDetailPage();
		if(key == ISensorDataContainer.class)
			return new DataDetailPage();
		return null;
	}

}
