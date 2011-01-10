package de.lmu.ifi.dbs.medmon.developer.ui.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

public class ProcessorDragListener implements DragSourceListener {

	private final Viewer viewer;

	public ProcessorDragListener(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
	
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		// Here you do the convertion to the type which is expected.
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		Object[] array = selection.toArray();
		XMLDataProcessor[] processors = new XMLDataProcessor[array.length];
		//Maybe a bit eaiser?
		for (int i = 0; i < processors.length; i++) {
			processors[i] = (XMLDataProcessor) array[i];			
		}
		
		if (ProcessorTransfer.getInstance().isSupportedType(event.dataType))
			event.data = processors;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		
	}

}
