package de.lmu.ifi.dbs.medmon.developer.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.developer.ui.wizard.ClusterWizard;

public class ClusterWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ClusterWizard wizard = new ClusterWizard();
		//WizardDialog dialog = new WizardDialog(wizard);
		return null;
	}

}
