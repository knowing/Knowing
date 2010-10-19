package de.lmu.ifi.dbs.medmon.sensor.ui.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.ui.provider.DataContentProvider;
import de.lmu.ifi.dbs.medmon.sensor.ui.provider.DataLabelProvider;

public class DataTreeView extends ViewPart {

	public static final String ID = "de.lmu.ifi.dbs.medmon.sensor.DataTreeView";
	
	private TreeViewer viewer;
	
	public DataTreeView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new DataContentProvider());
		viewer.setLabelProvider(new DataLabelProvider());
		viewer.setInput(sampleData());

	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}
	
	
	private ISensorDataContainer sampleData() {
		return new RootSensorDataContainer();
	}

}
