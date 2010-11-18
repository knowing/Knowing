package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import java.io.File;
import java.util.LinkedList;

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
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;

public class ProcessorUnitEditorInput implements IEditorInput {

	private DataProcessingUnit dpu;
	private File dpuXML;

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
			m.marshal(dpu, dpuXML);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public DataProcessingUnit getDpu() {
		return dpu;
	}

	public boolean addDataProcessor(DataProcessor processor) {
		if (dpu.getProcessors() == null)
			dpu.setProcessors(new LinkedList<DataProcessor>());
		return dpu.getProcessors().add(processor);
	}

	public boolean removeDataProcessor(DataProcessor processor) {
		if (dpu.getProcessors() == null)
			return false;
		return dpu.getProcessors().remove(processor);
	}

	public DataProcessor[] getProcessors() {
		if (dpu.getProcessors() == null || dpu.getProcessors().isEmpty())
			return new DataProcessor[0];

		DataProcessor[] returns = new DataProcessor[dpu.getProcessors().size()];
		int index = 0;
		for (DataProcessor processor : dpu.getProcessors())
			returns[index++] = processor;
		return returns;
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
		return "";
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

}
