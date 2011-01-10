package de.lmu.ifi.dbs.medmon.developer.ui.pages;


import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorUnitEditorInput;


public class ProcessorUnitConfigurationPage extends FormPage {
	
	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorListEditor";
	
	
	public ProcessorUnitConfigurationPage(FormEditor editor) {
		super(editor, ID, "Configuration");
		DataProcessingUnit dpu = ((ProcessorUnitEditorInput) editor.getEditorInput()).getDpu();
		
		for (XMLDataProcessor processor : dpu.getProcessors()) {
			processor.loadParameters();
		}
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		MasterDetailsBlock block = new ConfigurationMasterBlock((ProcessorUnitEditorInput) getEditorInput());
		block.createContent(managedForm);
	}
	
}
