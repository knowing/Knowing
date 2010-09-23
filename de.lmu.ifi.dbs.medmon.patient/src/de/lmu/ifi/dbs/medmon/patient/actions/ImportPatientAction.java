package de.lmu.ifi.dbs.medmon.patient.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.handlers.IHandlerService;

public class ImportPatientAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {
		System.out.println("Action: " + action);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("SelectionChanged for ImportPatientAction " + selection);
	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
