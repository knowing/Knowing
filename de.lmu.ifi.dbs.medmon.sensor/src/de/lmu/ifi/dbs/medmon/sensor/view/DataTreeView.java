package de.lmu.ifi.dbs.medmon.sensor.view;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.sensor.data.DaySensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.data.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.provider.DataContentProvider;
import de.lmu.ifi.dbs.medmon.sensor.provider.DataLabelProvider;

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
		GregorianCalendar cal = new GregorianCalendar();
		DaySensorDataContainer c1 = new DaySensorDataContainer(SampleDataFactory.getSensorDataArray(new Timestamp(cal.getTimeInMillis())));
		cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
		DaySensorDataContainer c2 = new DaySensorDataContainer(SampleDataFactory.getSensorDataArray(new Timestamp(cal.getTimeInMillis())));
		cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
		DaySensorDataContainer c3 = new DaySensorDataContainer(SampleDataFactory.getSensorDataArray(new Timestamp(cal.getTimeInMillis())));
		cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
		DaySensorDataContainer c4 = new DaySensorDataContainer(SampleDataFactory.getSensorDataArray(new Timestamp(cal.getTimeInMillis())));	
		return new RootSensorDataContainer(new ISensorDataContainer[] { c1, c2, c3,c4});
	}

}
