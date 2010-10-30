package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import de.lmu.ifi.dbs.medmon.developer.ui.pages.ProcessorUnitConfigurationPage;
import de.lmu.ifi.dbs.medmon.developer.ui.pages.ProcessorUnitManagePage;

public class ProcessorListFormEditor extends FormEditor {
	
	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorListFormEditor";

	@Override
	protected void addPages() {
		
		try {
			addPage(new ProcessorUnitManagePage(this));
			addPage(new ProcessorUnitConfigurationPage(this));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		
		
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
