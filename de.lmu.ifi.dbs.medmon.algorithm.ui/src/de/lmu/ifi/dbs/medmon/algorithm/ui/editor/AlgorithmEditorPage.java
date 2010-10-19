package de.lmu.ifi.dbs.medmon.algorithm.ui.editor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class AlgorithmEditorPage extends FormPage {

	public static final String ID = "de.lmu.ifi.dbs.medmon.algorithm.editor.AlgorithmEditorPage";
	private AlgorithmEditorBlock block;
	
	public AlgorithmEditorPage(FormEditor editor) {
		super(editor, ID, "Algorithmen");
		block = new AlgorithmEditorBlock();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText("Patientenverwaltung");
		block.createContent(managedForm);
		
	}
}
