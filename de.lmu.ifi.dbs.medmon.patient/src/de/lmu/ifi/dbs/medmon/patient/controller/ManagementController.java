package de.lmu.ifi.dbs.medmon.patient.controller;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.lmu.ifi.dbs.medmon.database.model.Patient;

public class ManagementController implements Listener {

	public static final Integer BUTTON_ADD  = 0;
	public static final Integer BUTTON_SAVE = 1;
	public static final Integer BUTTON_DEL  = 2;

	
	private final TableViewer viewer;
		
	public ManagementController(TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void handleEvent(Event event) {
		if(event.type == SWT.Selection) {
			if(event.widget.getData() == BUTTON_ADD) 
				addPatient();
			else if(event.widget.getData() == BUTTON_SAVE) 
				savePatient();
			else if(event.widget.getData() == BUTTON_DEL)
				System.out.println("Del");	
		}
	}
	
	
	private void addPatient() {
		Patient patient = new Patient("<Neuer", "Patient>");
		viewer.add(patient);
		viewer.setSelection(new StructuredSelection(patient));
	}
	
	private void savePatient() {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if(!selection.isEmpty()) {
			Patient patient = (Patient) selection.getFirstElement();
			if(MessageDialog.openConfirm(viewer.getTable().getShell(), "Patient eintragen", "Wollen sie " + patient + " speichern?")) {
				viewer.refresh(patient);
			}
		}
	}
}
