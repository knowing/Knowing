package de.lmu.ifi.dbs.medmon.medic.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.CreatePatientWizard;

public class NewPatientHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		CreatePatientWizard wizard = new CreatePatientWizard();
		WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}

}
