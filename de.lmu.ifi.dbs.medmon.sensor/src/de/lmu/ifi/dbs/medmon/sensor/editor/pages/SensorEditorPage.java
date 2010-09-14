package de.lmu.ifi.dbs.medmon.sensor.editor.pages;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class SensorEditorPage extends FormPage {

	public static final String ID = "de.lmu.ifi.dbs.medmon.sensor.editor.pages.SensorEditorPage";
	
	private SensorEditorBlock block;
	
	public SensorEditorPage(FormEditor editor) {
		super(editor, ID, "Sensorverwaltung");
		block = new SensorEditorBlock();
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText("Sensorverwaltung");
		block.createContent(managedForm);
	}
	
	

}
