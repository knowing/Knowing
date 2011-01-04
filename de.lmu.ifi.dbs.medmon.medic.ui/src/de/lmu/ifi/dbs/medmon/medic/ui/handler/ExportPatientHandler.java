package de.lmu.ifi.dbs.medmon.medic.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.ExportPatientWizard;

public class ExportPatientHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ExportPatientWizard wizard = new ExportPatientWizard();
		WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}

}
