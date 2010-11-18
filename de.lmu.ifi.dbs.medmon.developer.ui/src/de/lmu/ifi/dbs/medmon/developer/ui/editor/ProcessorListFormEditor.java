package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.developer.ui.pages.ProcessorUnitConfigurationPage;
import de.lmu.ifi.dbs.medmon.developer.ui.pages.ProcessorUnitManagePage;

public class ProcessorListFormEditor extends FormEditor {

	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorListFormEditor";
	
	private DataProcessingUnit dpu;
	private File dpuXML;
	
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
		createDPUFile(input);
	}

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

	}

	@Override
	public boolean isSaveAsAllowed() {
		
		return false;
	}
	
	/**
	 * Creates a DataProcessingUnit XML file
	 * @param input
	 */
	private void createDPUFile(IEditorInput input) {
		ProcessorUnitEditorInput dpuInput = (ProcessorUnitEditorInput)input;
		dpu = dpuInput.getDpu();
		
		//Check out the workspace location
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();	
		IPath location = root.getLocation();
		
		try {
			dpuXML = new File(location.toOSString() + File.separator + input.getName() + ".xml");
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Marshaller m = context.createMarshaller();
			m.marshal(dpu, dpuXML);
		} catch (JAXBException e) {
			e.printStackTrace();
		}	
	}
	
	

}
