package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import de.lmu.ifi.dbs.medmon.developer.ui.pages.UnitFormPage;

public class ProcessorListFormEditor extends FormEditor {

	@Override
	protected void addPages() {
		
		try {
			addPage(new UnitFormPage("UnitFormPage", "Data Processing Unit"));
			addPage(new ProcessorListFormEditor(), getEditorInput());
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
