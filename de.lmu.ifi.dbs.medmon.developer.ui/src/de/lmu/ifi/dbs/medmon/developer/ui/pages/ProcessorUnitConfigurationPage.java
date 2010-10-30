package de.lmu.ifi.dbs.medmon.developer.ui.pages;


import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;


public class ProcessorUnitConfigurationPage extends FormPage {
	
	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorListEditor";
	
	
	public ProcessorUnitConfigurationPage(FormEditor editor) {
		super(editor, ID, "Configuration");
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		MasterDetailsBlock block = new ConfigurationMasterBlock();
		block.createContent(managedForm);
	}

}
