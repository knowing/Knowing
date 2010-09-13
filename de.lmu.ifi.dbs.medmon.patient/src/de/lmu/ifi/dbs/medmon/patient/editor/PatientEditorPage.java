package de.lmu.ifi.dbs.medmon.patient.editor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class PatientEditorPage extends FormPage {

	public static final String id = "de.lmu.ifi.dbs.medmon.patient.editor.GeneralEditorPage";
	
	private PatientEditorBlock block;
	

	public PatientEditorPage(FormEditor editor) {
		super(editor, id, "Patientenverwaltung");
		block = new PatientEditorBlock();
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText("Patientenverwaltung");
		block.createContent(managedForm);
		
		
	}

}
