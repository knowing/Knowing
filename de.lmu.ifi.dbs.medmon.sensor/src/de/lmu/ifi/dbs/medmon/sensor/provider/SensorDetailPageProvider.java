package de.lmu.ifi.dbs.medmon.sensor.provider;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import de.lmu.ifi.dbs.medmon.sensor.data.ISensor;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.editor.pages.DataDetailPage;
import de.lmu.ifi.dbs.medmon.sensor.editor.pages.SensorDetailPage;

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
