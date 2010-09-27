package de.lmu.ifi.dbs.medmon.visualizer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.algorithm.extension.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.visualizer.Activator;

public class OpenDefaultPerspectiveHandler extends AbstractHandler {

	public static final String ID = "de.lmu.ifi.dbs.medmon.visualizer.OpenDefaultPerspective";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IPatientService service = Activator.getPatientService();
		ISensorDataAlgorithm algorithm = (ISensorDataAlgorithm) service.getSelection(IPatientService.ALGORITHM);
		
		//Analyze data -> implement job to provide progressbar
		Data[] sensorData = (Data[]) service.getSelection(IPatientService.SENSOR_DATA);
		IAnalyzedData data = algorithm.analyze(sensorData);
		//Set the new analyzed data
		service.setSelection(data, IPatientService.ANALYZED_DATA);
		//Try to open the corresponding perspective
		try {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			window.getWorkbench().showPerspective(IMedmonConstants.VISUALIZE_PERSPECTIVE_DEFAULT, window);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void excuteCommand() {
		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand(ID, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ID + " error");
		}

	}

}
