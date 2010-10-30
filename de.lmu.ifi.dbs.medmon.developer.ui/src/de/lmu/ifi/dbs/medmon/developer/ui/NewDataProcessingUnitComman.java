package de.lmu.ifi.dbs.medmon.developer.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorListEditorInput;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorListFormEditor;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.DataProcessingList;

public class NewDataProcessingUnitComman extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ProcessorListEditorInput input = new ProcessorListEditorInput(new DataProcessingList("New List"));
			
		try {
			System.out.println("Opening Editor");
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, ProcessorListFormEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
