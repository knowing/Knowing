package de.lmu.ifi.dbs.medmon.medic.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.lmu.ifi.dbs.medmon.database.model.Patient;

public class PatientEditorInput implements IEditorInput {

	private Patient patient;

	
	public PatientEditorInput() {
		// TODO Auto-generated constructor stub
	}
	
	public PatientEditorInput(Patient patient) {
		this.patient = patient;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Patientenliste";
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Editor Tooltip";
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj instanceof PatientEditorInput) {
			return patient.equals(((PatientEditorInput) obj).getPatient());
		}
		return false;
	}

	public Patient getPatient() {
		return patient;
	}


}
