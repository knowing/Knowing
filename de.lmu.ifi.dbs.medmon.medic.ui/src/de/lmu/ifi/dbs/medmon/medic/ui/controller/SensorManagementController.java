package de.lmu.ifi.dbs.medmon.medic.ui.controller;

import java.io.IOException;
import java.util.Calendar;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SensorManagementController extends HyperlinkAdapter implements Listener {

	public static final Integer IMPORT = 0;
	public static final Integer EXPORT = 1;
	public static final Integer DELETE = 2;

	private final ColumnViewer dataViewer;
	private final ColumnViewer sensorViewer;

	public SensorManagementController(ColumnViewer dataViewer, ColumnViewer sensorViewer) {
		this.dataViewer = dataViewer;
		this.sensorViewer = sensorViewer;
	}

	@Override
	public void linkActivated(HyperlinkEvent e) {
		if (e.getHref() == IMPORT)
			importData();
		else if (e.getHref() == EXPORT)
			System.out.println("Export Data");
		else if (e.getHref() == DELETE)
			System.out.println("Delete Data");
		
	}
	
	@Override
	public void handleEvent(Event event) {
		if(!(event.type == SWT.Selection))
			return;
		
		if(event.widget.getData() == IMPORT)
			importData();
	}

	public void importData() {
		IStructuredSelection selection = (IStructuredSelection) sensorViewer.getSelection();
		ISensor<?> sensor = (ISensor<?>) selection.getFirstElement();
		IConverter<?> converter = sensor.getConverter();
		String file = converter.openChooseInputDialog(sensorViewer.getControl().getShell());
		try {
			ISensorDataContainer root = converter.convertToContainer(file, ContainerType.WEEK, ContainerType.HOUR, null);
			dataViewer.setInput(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}


}
