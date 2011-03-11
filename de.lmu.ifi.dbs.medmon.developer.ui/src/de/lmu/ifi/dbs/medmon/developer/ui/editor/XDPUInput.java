package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.graph.XDataProcessingUnit;

public class XDPUInput implements IEditorInput {

	
	private XDataProcessingUnit dpu;
	
	public XDPUInput(XDataProcessingUnit dpu) {
		this.dpu = dpu;
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
	public String getName() {
		return dpu.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "No Tooltip";
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		if(adapter.equals(XDataProcessingUnit.class))
			return dpu;
		return null;
	}

}
