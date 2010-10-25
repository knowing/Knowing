package de.lmu.ifi.dbs.medmon.sensor.controller;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.ibm.icu.util.Calendar;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;
import de.lmu.ifi.dbs.medmon.sensor.ui.Activator;
import de.lmu.ifi.dbs.medmon.sensor.ui.wizard.ImportWizard;

public class ManagementController extends HyperlinkAdapter {

	public static final Integer IMPORT = 0;
	public static final Integer EXPORT = 1;
	public static final Integer DELETE = 2;

	private final ColumnViewer dataViewer;
	private final ColumnViewer sensorViewer;

	public ManagementController(ColumnViewer dataViewer, ColumnViewer sensorViewer) {
		this.dataViewer = dataViewer;
		this.sensorViewer = sensorViewer;
	}

	@Override
	public void linkActivated(HyperlinkEvent e) {
		if (e.getHref() == IMPORT) {
			importData();
		} else if (e.getHref() == EXPORT) {
			System.out.println("Export Data");
		} else if (e.getHref() == DELETE) {
			System.out.println("Delete Data");
		}
	}

	public void importData() {
		IStructuredSelection selection = (IStructuredSelection) sensorViewer.getSelection();
		ISensor<?> sensor = (ISensor<?>) selection.getFirstElement();
		IConverter<?> converter = sensor.getConverter();
		String file = converter.openChooseInputDialog(sensorViewer.getControl().getShell());
		try {
			Block[] blocks = converter.convertToBlock(file, Calendar.HOUR_OF_DAY);
			RootSensorDataContainer<?> root = new RootSensorDataContainer();
			converter.parseBlockToContainer(root, blocks);
			dataViewer.setInput(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// MessageDialog.openInformation(dataViewer.getControl().getShell(),
		// "Warnung", "Diese Funktion ist noch nicht moeglich");
	}


}
