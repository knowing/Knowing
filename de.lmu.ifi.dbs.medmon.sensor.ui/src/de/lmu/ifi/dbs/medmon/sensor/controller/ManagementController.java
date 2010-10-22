package de.lmu.ifi.dbs.medmon.sensor.controller;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

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
	
	public void importData() {
		//ISensorDataContainer data = SDRConverter.importDialog(viewer.getControl().getShell());
		//viewer.setInput(data);
		MessageDialog.openInformation(viewer.getControl().getShell(), "Warnung", "Diese Funktion ist noch nicht moeglich");
	}
	
}
