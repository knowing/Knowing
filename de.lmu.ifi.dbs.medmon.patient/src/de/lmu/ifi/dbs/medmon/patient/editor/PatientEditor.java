package de.lmu.ifi.dbs.medmon.patient.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import de.lmu.ifi.dbs.medmon.patient.sampledata.Patient;

public class PatientEditor extends FormEditor {
	
	public static final String ID = "de.lmu.ifi.dbs.medmon.patient.PatientEditor";
	
	private GeneralEditorPage generalPage;

	private Patient patient;

	public PatientEditor() {
		generalPage = new GeneralEditorPage(this);
	}


	@Override
	protected void addPages() {
		try {
			addPage(generalPage);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		patient = ((PatientEditorInput)input).getPatient();
		setPartName(patient.toString());
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
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
