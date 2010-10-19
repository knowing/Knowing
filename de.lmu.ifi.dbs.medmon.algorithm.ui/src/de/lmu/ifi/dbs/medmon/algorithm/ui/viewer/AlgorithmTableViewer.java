package de.lmu.ifi.dbs.medmon.algorithm.ui.viewer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.medmon.algorithm.ui.provider.AlgorithmContentProvider;
import de.lmu.ifi.dbs.medmon.algorithm.ui.provider.AlgorithmLabelProvider;

public class AlgorithmTableViewer extends TableViewer {

	public AlgorithmTableViewer(Composite parent, int style) {
		super(parent, style);
		init();
	}

	public AlgorithmTableViewer(Table table) {
		super(table);
		init();
	}
	
	private void init() {
		setContentProvider(new AlgorithmContentProvider());
		setLabelProvider(new AlgorithmLabelProvider());
	}

}
