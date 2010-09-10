package de.lmu.ifi.dbs.medmon.patient.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.medmon.patient.editor.PatientEditor;
import de.lmu.ifi.dbs.medmon.patient.editor.PatientEditorInput;
import de.lmu.ifi.dbs.medmon.patient.sampledata.Patient;
import de.lmu.ifi.dbs.medmon.patient.views.PatientListView;

public class OpenPatientEditorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		PatientListView view = (PatientListView)page.findView(PatientListView.ID);
		
		ISelection selection = view.getSite().getSelectionProvider().getSelection();
		if(!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			// If we had a selection lets open the editor
			if (obj != null) {
				Patient person = (Patient) obj;
				PatientEditorInput input = new PatientEditorInput(person);
				try {
					page.openEditor(input, PatientEditor.ID);
				} catch (PartInitException e) {
					System.out.println(e.getStackTrace());
				}
			}
		}
		return null;
	}



}
