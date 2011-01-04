package de.lmu.ifi.dbs.medmon.medic.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;

public class PatientManagementHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().showPerspective(IMedmonConstants.MANAGEMENT_PERSPECTIVE, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return null;
	}

}
