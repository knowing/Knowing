package de.lmu.ifi.dbs.medmon.patient.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.patient.editor.PatientEditor;
import de.lmu.ifi.dbs.medmon.patient.editor.PatientEditorInput;


public class OpenPatientEditorHandler extends AbstractHandler {

	private Object[] result;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		

		/*
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, new LabelProvider());
		dialog.setElements(SampleDataFactory.getData());
		dialog.setTitle("Patient waehlen");
		// User pressed cancel
		if (dialog.open() != Window.OK) {
			return false;
		}

		result = dialog.getResult();
		LinkedList<Patient> patients = new LinkedList<Patient>();
		for (Object o : result)
			if (o instanceof Patient)
				patients.add((Patient) o);
		*/
		
		PatientEditorInput input = new PatientEditorInput();
		try {
			page.openEditor(input, PatientEditor.ID);
		} catch (PartInitException e) {
			System.out.println(e.getStackTrace());
		}

		return true;
	}

}
