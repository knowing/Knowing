package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.lmu.ifi.dbs.medmon.sensor.core.processing.DataProcessingList;

public class ProcessorListEditorInput implements IEditorInput {

	private DataProcessingList list;
	
	public ProcessorListEditorInput(DataProcessingList list) {
		this.list = list;
	}
	
	public DataProcessingList getList() {
		return list;
	}
	
	@Override
	public String getName() {
		return list.getName();
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
