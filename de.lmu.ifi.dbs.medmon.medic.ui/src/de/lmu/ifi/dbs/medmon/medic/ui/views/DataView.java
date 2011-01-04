package de.lmu.ifi.dbs.medmon.medic.ui.views;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.base.ui.viewer.SensorTableViewer;
import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;

public class DataView extends ViewPart {
	
	public static final String ID = "de.lmu.ifi.dbs.medmon.sensor.SensorDataView";
	private SensorTableViewer viewer;

	public DataView() {
		// TODO Init with selected SensorData
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION);
		viewer = new SensorTableViewer(table);
		Set<Data> set = SampleDataFactory.getSensorData();
		viewer.setInput(set.toArray(new Data[set.size()]));
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
