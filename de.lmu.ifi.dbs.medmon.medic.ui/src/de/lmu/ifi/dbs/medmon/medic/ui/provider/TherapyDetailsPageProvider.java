package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import de.lmu.ifi.dbs.medmon.medic.core.extensions.ITherapy;
import de.lmu.ifi.dbs.medmon.medic.ui.pages.TherapyDetailsPage;

public class TherapyDetailsPageProvider implements IDetailsPageProvider {

	@Override
	public Object getPageKey(Object object) {
		if(object instanceof ITherapy)
			return ITherapy.class;
		return null;
	}

	@Override
	public IDetailsPage getPage(Object key) {
		if(key == ITherapy.class)
			return new TherapyDetailsPage();
		return null;
	}

}
