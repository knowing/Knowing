package de.lmu.ifi.dbs.medmon.medic.ui.controller;

import javax.persistence.EntityManager;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;

public class PatientManagementController implements Listener {

	public static final Integer BUTTON_ADD = 0;
	public static final Integer BUTTON_SAVE = 1;
	public static final Integer BUTTON_DEL = 2;
	public static final Integer BUTTON_REFRESH = 3;

	private final TableViewer viewer;

	public PatientManagementController(TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void handleEvent(Event event) {
		if (event.type == SWT.Selection) {
			if (event.widget.getData() == BUTTON_ADD)
				addPatient();
			else if (event.widget.getData() == BUTTON_SAVE)
				savePatient();
			else if (event.widget.getData() == BUTTON_DEL)
				removePatient();
			else if(event.widget.getData() == BUTTON_REFRESH)
				viewer.setInput(this);
		}
	}

	private void addPatient() {
		Patient patient = new Patient("<Neuer", "Patient>");
		viewer.add(patient);
		viewer.setSelection(new StructuredSelection(patient));
	}

	private void savePatient() {
		Patient patient = selectedPatient();
		if(patient == null)
			return;

		if (MessageDialog.openConfirm(viewer.getTable().getShell(),
				"Patient eintragen", "Wollen sie " + patient + " speichern?")) {
			EntityManager em = JPAUtil.currentEntityManager();
			em.getTransaction().begin();
			em.persist(patient);
			em.getTransaction().commit();
			viewer.refresh(patient);
		}
	}

	private boolean removePatient() {
		Patient patient = selectedPatient();
		if(patient == null)
			return false;
		if (MessageDialog.openConfirm(viewer.getTable().getShell(),
				"Patient entfernen", "Wollen sie " + patient + " entfernen?")) {
			EntityManager em = JPAUtil.currentEntityManager();
			em.getTransaction().begin();
			em.remove(patient);
			em.getTransaction().commit();
			viewer.refresh(true);
		}

		return true;
	}

	private Patient selectedPatient() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (selection.isEmpty())
			return null;
		return (Patient) selection.getFirstElement();
	}
}
