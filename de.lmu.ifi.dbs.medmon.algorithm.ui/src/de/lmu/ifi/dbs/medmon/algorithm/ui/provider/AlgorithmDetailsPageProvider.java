package de.lmu.ifi.dbs.medmon.algorithm.ui.provider;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import de.lmu.ifi.dbs.medmon.algorithm.ui.editor.AlgorithmDetailsPage;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;

/**
 * Provides DetailPages for every class implementing the interface
 * ISensorDataAlgorithm and shows up the AlgorithmDetailsPage.
 * @author muki
 * @version 1.0
 */
public class AlgorithmDetailsPageProvider implements IDetailsPageProvider {

	@Override
	public Object getPageKey(Object object) {
		if(object instanceof IAlgorithm)
			return IAlgorithm.class;
		return null;
	}

	@Override
	public IDetailsPage getPage(Object key) {
		if(key == IAlgorithm.class)
			return new AlgorithmDetailsPage();
		return null;
	}

}
