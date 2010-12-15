package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import java.io.File;

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
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.developer.ui.pages.ProcessorUnitConfigurationPage;
import de.lmu.ifi.dbs.medmon.developer.ui.pages.ProcessorUnitManagePage;

public class DPUFormEditor extends FormEditor {

	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.editor.DPUFormEditor";
	
	private File dpuXML;

	private ProcessorUnitManagePage processorUnitManagePage;
	
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
		ProcessorUnitEditorInput dpuInput = (ProcessorUnitEditorInput)input;
		saveDPUFile(dpuInput.getDpu());
	}

	@Override
	protected void addPages() {
		try {
			processorUnitManagePage = new ProcessorUnitManagePage(this);
			//TODO setSelectionProvider
			//getSite().setSelectionProvider(processorUnitManagePage.getSite().getSelectionProvider());
			addPage(processorUnitManagePage);
			addPage(new ProcessorUnitConfigurationPage(this));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		ProcessorUnitEditorInput dpuInput = (ProcessorUnitEditorInput)getEditorInput();
		saveDPUFile(dpuInput.getDpu());
		setPartName(dpuInput.getDpu().getName());
		getActivePageInstance().doSave(monitor);
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public boolean isSaveAsAllowed() {	
		return false;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
        if (adapter == IPropertySheetPage.class)
            return new TabbedPropertySheetPage(processorUnitManagePage);
		return super.getAdapter(adapter);
	}
	
	/**
	 * Creates a DataProcessingUnit XML file
	 * @param input
	 */
	private void saveDPUFile(DataProcessingUnit dpu) {
		
		//Check out the workspace location
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();	
		IPath location = root.getLocation();
		
		try {
			if(dpuXML == null) {
				dpuXML = new File(location.toOSString() + File.separator + dpu.getName() + ".xml");
			} else 	if(!dpuXML.getName().equals(dpu.getName() + ".xml")) {
				if(dpuXML.delete()) {
					dpuXML = new File(location.toOSString() + File.separator + dpu.getName() + ".xml");
				}		
			}
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Marshaller m = context.createMarshaller();
			m.marshal(dpu, dpuXML);
		} catch (JAXBException e) {
			e.printStackTrace();
		}	
	}
	
}
