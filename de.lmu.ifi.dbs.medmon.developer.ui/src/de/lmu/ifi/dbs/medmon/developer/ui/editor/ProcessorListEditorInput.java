package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;

public class ProcessorListEditorInput implements IEditorInput {

	private DataProcessingUnit dpu;
	
	public ProcessorListEditorInput(DataProcessingUnit dpu) {
		this.dpu = dpu;
	}
	
	public DataProcessingUnit getDpu() {
		return dpu;
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
