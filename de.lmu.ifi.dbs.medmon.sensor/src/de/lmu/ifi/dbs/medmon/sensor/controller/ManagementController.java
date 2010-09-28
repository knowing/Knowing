package de.lmu.ifi.dbs.medmon.sensor.controller;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import de.lmu.ifi.dbs.medmon.sensor.converter.SDRConverter;

public class ManagementController extends HyperlinkAdapter {
	
	public static final Integer IMPORT = 0;
	public static final Integer EXPORT = 1;
	public static final Integer DELETE = 2;
	
	private final ColumnViewer viewer;
		
	public ManagementController(ColumnViewer viewer) {
		this.viewer = viewer;
	}



	@Override
	public void linkActivated(HyperlinkEvent e) {
		if(e.getHref() == IMPORT) {
			importData();
		} else if (e.getHref() == EXPORT) {
			System.out.println("Export Data");
		} else if (e.getHref() == DELETE) {
			System.out.println("Delete Data");
		}
	}
	
	private void importData() {
		FileDialog dialog = new FileDialog(viewer.getControl().getShell());
		dialog.setFilterExtensions(new String[] { "*.sdr", "*.csv" });
		dialog.setFilterNames(new String[] { "SensorFile(*.sdr)", "CSV Table(*.csv)"});
		String selected = dialog.open();
		try {
			viewer.setInput(SDRConverter.convertSDRtoData(selected, 1, 20));
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(dialog.getParent(), "Fehler beim Daten lesen", e.getMessage());
		}
	}

}
