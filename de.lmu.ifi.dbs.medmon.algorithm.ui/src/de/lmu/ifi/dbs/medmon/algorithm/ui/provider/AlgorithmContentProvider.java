package de.lmu.ifi.dbs.medmon.algorithm.ui.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.sensor.core.util.AlgorithmUtil;

/**
 * Needs no specified input element. Provides all registered 
 * ISensorDataAlgorithm Extensions.
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 */
public class AlgorithmContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return AlgorithmUtil.evaluateAlgorithms();
	}

	

}
