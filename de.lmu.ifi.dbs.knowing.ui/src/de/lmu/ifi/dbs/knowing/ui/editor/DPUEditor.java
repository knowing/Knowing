package de.lmu.ifi.dbs.knowing.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.ui.editor.pages.ConfigurationPage;
import de.lmu.ifi.dbs.knowing.ui.editor.pages.GraphicPage;
import de.lmu.ifi.dbs.knowing.ui.editor.pages.MainPage;

public class DPUEditor extends FormEditor {

	public static final String ID = "de.lmu.ifi.dbs.knowing.ui.editor.DPUEditor"; //$NON-NLS-1$

	private MainPage mainPage;
	private int mainPageIndex;

	private ConfigurationPage configPage;
	private int configPageIndex;

	private GraphicPage graphicPage;
	private int graphicPageIndex;

	private StructuredTextEditor sourcePage;
	private int sourcePageIndex;

	@Override
	protected void addPages() {
		try {
			mainPageIndex = addPage(mainPage = new MainPage(this));
			configPageIndex = addPage(configPage = new ConfigurationPage(this));
			graphicPageIndex = addPage(graphicPage = new GraphicPage(this));
			sourcePageIndex = addPage(sourcePage = new StructuredTextEditor(), getEditorInput());
			setPageText(sourcePageIndex, "Source");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		site.setSelectionProvider(null);
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
		else if (page == mainPageIndex)
			mainPage.doSave(monitor);

		try {
			mainPage.update(getEditorInput());
			configPage.update(getEditorInput());
			graphicPage.update(getEditorInput());
			sourcePage.init(getEditorSite(), getEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
	}

	public static DataProcessingUnit convert(IEditorInput input) throws CoreException, JAXBException, IOException {
		IFile file = (IFile) input.getAdapter(IFile.class);
		InputStream in = file.getContents();
		JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
		Unmarshaller um = context.createUnmarshaller();
		DataProcessingUnit dpu = (DataProcessingUnit) um.unmarshal(in);
		in.close();
		return dpu;
	}

	public static void doSave(DataProcessingUnit dpu, IFile file) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(dpu, bos);
			bos.close();
			byte[] data = bos.toByteArray();
			ByteArrayInputStream source = new ByteArrayInputStream(data);
			file.setContents(source, IFile.FORCE, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
