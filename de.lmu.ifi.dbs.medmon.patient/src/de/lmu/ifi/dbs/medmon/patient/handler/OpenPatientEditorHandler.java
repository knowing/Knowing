package de.lmu.ifi.dbs.medmon.patient.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.medmon.patient.editor.PatientEditor;
import de.lmu.ifi.dbs.medmon.patient.editor.PatientEditorInput;
import de.lmu.ifi.dbs.medmon.patient.sampledata.Patient;
import de.lmu.ifi.dbs.medmon.patient.sampledata.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.patient.views.PatientListView;

public class OpenPatientEditorHandler extends AbstractHandler {

	private Object[] result;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, new LabelProvider());
		dialog.setElements(SampleDataFactory.getData());
		dialog.setTitle("Patient waehlen");
		// User pressed cancel
		if (dialog.open() != Window.OK) {
			return false;
		}

		result = dialog.getResult();
		for (Object o : result) {
			if (o instanceof Patient) {
				// If we had a selection lets open the editor
				Patient patient = (Patient)o;
				PatientEditorInput input = new PatientEditorInput(patient);
				try {
					page.openEditor(input, PatientEditor.ID);
				} catch (PartInitException e) {
					System.out.println(e.getStackTrace());
				}
			}
		}
		

		return true;
	}

}
