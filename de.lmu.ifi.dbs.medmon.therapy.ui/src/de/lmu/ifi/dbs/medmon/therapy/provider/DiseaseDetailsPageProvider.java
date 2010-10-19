package de.lmu.ifi.dbs.medmon.therapy.provider;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import de.lmu.ifi.dbs.medmon.therapy.core.extensions.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.pages.DiseaseDetailsPage;

public class DiseaseDetailsPageProvider implements IDetailsPageProvider {

	@Override
	public Object getPageKey(Object object) {
		if(object instanceof IDisease)
			return IDisease.class;
		return null;
	}

	@Override
	public IDetailsPage getPage(Object key) {
		if(key == IDisease.class)
			return new DiseaseDetailsPage();
		return null;
	}

}
