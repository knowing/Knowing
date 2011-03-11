package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

import de.lmu.ifi.dbs.medmon.developer.ui.pages.XDPUPage;

public class XDPUEditor extends FormEditor implements ITabbedPropertySheetPageContributor {

	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.editor.XDPUFormEditor";

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
	}
	
	@Override
	protected void addPages() {
		try {
			addPage(new XDPUPage(this));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public String getContributorId() {
		return "";
	}

}
