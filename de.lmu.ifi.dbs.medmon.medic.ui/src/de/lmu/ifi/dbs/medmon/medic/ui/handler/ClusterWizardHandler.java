package de.lmu.ifi.dbs.medmon.medic.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.TrainClusterWizard;

public class ClusterWizardHandler extends AbstractHandler {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.clusterwizard";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TrainClusterWizard wizard = new TrainClusterWizard();
		WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}

}
