package de.lmu.ifi.dbs.medmon.patient.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import de.lmu.ifi.dbs.medmon.sensor.ui.pages.SensorEditorPage;

public class PatientEditor extends FormEditor {
	
	public static final String ID = "de.lmu.ifi.dbs.medmon.patient.PatientEditor";
	
	private PatientEditorPage patientPage;
	private SensorEditorPage sensorPage;

	public PatientEditor() {
		patientPage = new PatientEditorPage(this);
		sensorPage = new SensorEditorPage(this);
	}


	@Override
	protected void addPages() {
		try {
			addPage(patientPage);
			addPage(sensorPage);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setPartName("Patienten-Editor");
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("PatientEditr doSave");
		
	}


	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

}
