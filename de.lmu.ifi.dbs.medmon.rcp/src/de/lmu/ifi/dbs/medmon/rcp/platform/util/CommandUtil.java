package de.lmu.ifi.dbs.medmon.rcp.platform.util;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;

public class CommandUtil {

	public static void openView(String viewID) {
 		try {
			ICommandService commandService = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
	 		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
	 		Command showView = commandService.getCommand("org.eclipse.ui.views.showView");
	 		IParameter viewIdParm = showView.getParameter("org.eclipse.ui.views.showView.viewId");
	 		Parameterization parm = new Parameterization(viewIdParm, viewID);
	 		ParameterizedCommand parmCommand = new ParameterizedCommand(showView, new Parameterization[] { parm });
			handlerService.executeCommand(parmCommand, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (NotDefinedException e) {
			e.printStackTrace();
		} catch (NotEnabledException e) {
			e.printStackTrace();
		} catch (NotHandledException e) {
			e.printStackTrace();
		}
	}
	
	public static void openPerpsective(String perspectiveID) {
		try {
			PlatformUI.getWorkbench().showPerspective(perspectiveID, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}
}
