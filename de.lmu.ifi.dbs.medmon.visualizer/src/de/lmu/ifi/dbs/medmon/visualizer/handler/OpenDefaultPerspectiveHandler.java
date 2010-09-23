package de.lmu.ifi.dbs.medmon.visualizer.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;

public class OpenDefaultPerspectiveHandler extends AbstractHandler {

	public static final String ID = "de.lmu.ifi.dbs.medmon.visualizer.defaultPerspective";
	public static final String ALGORITHM_PARAMETER = "de.lmu.ifi.dbs.medmon.visualizer.defaultPerspective.algorithm";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Open Perspective");
		System.out.println("Parameter: " + 	event.getParameter(ALGORITHM_PARAMETER));
		String parmValue = event.getParameter(ALGORITHM_PARAMETER);
		System.out.println("Parameter Object: " + event.getObjectParameterForExecution(ALGORITHM_PARAMETER));

		try {
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = window.getWorkbench().showPerspective(
					IMedmonConstants.VISUALIZE_PERSPECTIVE_DEFAULT, window);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void excuteCommand() {
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class);
		Command showView = commandService.getCommand(ID);
		IParameter algorithmParm;
		try {
			algorithmParm = showView.getParameter(ALGORITHM_PARAMETER);
			System.out.println("AlgorithmParm: " + algorithmParm);
			// the viewId parameter provides a list of valid values ... if you
			// knew the id of the problem view, you could skip this step.
			// This method is supposed to be used in places like the keys
			// preference page, to allow the user to select values
			IParameterValues parmValues = algorithmParm.getValues();
			System.out.println("parmValues: " + parmValues);
			ISensorDataAlgorithm algorithm = null;
			Iterator it = parmValues.getParameterValues().values().iterator();
			while (it.hasNext()) {
				algorithm = (ISensorDataAlgorithm)it.next();
				System.out.println("Iterator: " + algorithm);
				
			}
			
			Parameterization parm = new Parameterization(algorithmParm, algorithm.getName());
			System.out.println("parm: " + parm + " - " + parm.getValue());
			ParameterizedCommand parmCommand = new ParameterizedCommand(showView, new Parameterization[] { parm });

			handlerService.executeCommand(parmCommand, null);
		} catch (NotDefinedException e) {
			e.printStackTrace();
		} catch (ParameterValuesException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (NotEnabledException e) {
			e.printStackTrace();
		} catch (NotHandledException e) {
			e.printStackTrace();
		}

	}

}
