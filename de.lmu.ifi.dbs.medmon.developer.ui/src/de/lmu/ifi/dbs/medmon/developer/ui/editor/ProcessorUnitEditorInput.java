package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

public class ProcessorUnitEditorInput implements IEditorInput {

	private DataProcessingUnit dpu;
	private File dpuXML;

	public ProcessorUnitEditorInput(DataProcessingUnit dpu, File dpuXML) {
		this.dpu = dpu;
		this.dpuXML = dpuXML;
	}
	
	public ProcessorUnitEditorInput(DataProcessingUnit dpu) {
		this.dpu = dpu;
		init();
	}

	private void init() {
		// Check out the workspace location
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath location = root.getLocation();

		try {
			dpuXML = new File(location.toOSString() + File.separator + getName() + ".xml");
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dpu, dpuXML);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public File getDpuXML() {
		return dpuXML;
	}

	public DataProcessingUnit getDpu() {
		return dpu;
	}

	public boolean addDataProcessor(XMLDataProcessor processor) {
		return dpu.add(processor);
	}

	public boolean removeDataProcessor(XMLDataProcessor processor) {
		return dpu.remove(processor);
	}

	public XMLDataProcessor[] getProcessors() {
		return dpu.toArray();
	}

	@Override
	public String getName() {
		return dpu.getName();
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Tooltip";
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

}
