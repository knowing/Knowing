package de.lmu.ifi.dbs.medmon.medic.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenAnalysePerspectiveHandler extends AbstractHandler {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.OpenDefaultPerspective";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Try to open the corresponding perspective
		try {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			window.getWorkbench().showPerspective("de.lmu.ifi.dbs.medmon.medic.ui.default", window);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}

		return null;
	}


}
