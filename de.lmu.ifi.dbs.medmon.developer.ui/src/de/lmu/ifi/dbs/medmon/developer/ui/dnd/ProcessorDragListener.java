package de.lmu.ifi.dbs.medmon.developer.ui.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;

public class ProcessorDragListener implements DragSourceListener {

	private final Viewer viewer;

	public ProcessorDragListener(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		System.out.println("Drag start");
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		// Here you do the convertion to the type which is expected.
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		Object[] array = selection.toArray();
		DataProcessor[] processors = new DataProcessor[array.length];
		//Maybe a bit eaiser?
		for (int i = 0; i < processors.length; i++) {
			processors[i] = (DataProcessor) array[i];			
		}
		
		if (ProcessorTransfer.getInstance().isSupportedType(event.dataType))
			event.data = processors;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		System.out.println("Drag finished");
	}

}
