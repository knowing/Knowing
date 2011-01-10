package de.lmu.ifi.dbs.medmon.developer.ui.dnd;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

public class ProcessorDropListener extends ViewerDropAdapter {

	public ProcessorDropListener(Viewer viewer) {
		super(viewer);
	}

	// This method performs the actual drop
	@Override
	public boolean performDrop(Object data) {
		if(data == null)
			return false;
		XMLDataProcessor[] toDrop = (XMLDataProcessor[])data;
		DataProcessingUnit dpu = (DataProcessingUnit) getViewer().getInput();
		
		for (XMLDataProcessor dataProcessor : toDrop) 
			dpu.add(dataProcessor);
			
		
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;
	}
	
	@Override
	public void drop(DropTargetEvent event) {
		int location = this.determineLocation(event);
		XMLDataProcessor target = (XMLDataProcessor)determineTarget(event);
		String translatedLocation ="";
		switch (location){
		case 1 :
			translatedLocation = "Dropped before the target ";
			break;
		case 2 :
			translatedLocation = "Dropped after the target ";
			break;
		case 3 :
			translatedLocation = "Dropped on the target ";
			break;
		case 4 :
			translatedLocation = "Dropped into nothing ";
			break;
		}
		//System.out.println(translatedLocation);
		//System.out.println("The drop was done on the element: " + target );
		super.drop(event);
		getViewer().refresh();
	}

}
