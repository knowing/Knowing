package de.lmu.ifi.dbs.knowing.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import de.lmu.ifi.dbs.knowing.ui.editor.pages.ConfigurationPage;
import de.lmu.ifi.dbs.knowing.ui.editor.pages.GraphicPage;

public class DPUEditor extends FormEditor {

	public static final String ID = "de.lmu.ifi.dbs.knowing.ui.editor.DPUEditor"; //$NON-NLS-1$

	private ConfigurationPage configPage;
	private int configPageIndex;

	private StructuredTextEditor sourcePage;
	private int sourcePageIndex;
	
	private GraphicPage graphicPage;
	private int graphicPageIndex;

	@Override
	protected void addPages() {
		try {
			configPageIndex = addPage(configPage = new ConfigurationPage(this));
			sourcePageIndex = addPage(sourcePage = new StructuredTextEditor(), getEditorInput());
			graphicPageIndex = addPage(graphicPage = new GraphicPage(this));
			setPageText(1, "Source");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		int page = getActivePage();
		if (page == configPageIndex)
			configPage.doSave(monitor);
		else if (page == sourcePageIndex)
			sourcePage.doSave(monitor);

		try {
			configPage.update(getEditorInput());
			sourcePage.init(getEditorSite(), getEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
	}

}
