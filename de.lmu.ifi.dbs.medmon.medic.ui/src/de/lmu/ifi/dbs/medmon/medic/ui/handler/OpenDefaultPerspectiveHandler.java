package de.lmu.ifi.dbs.medmon.medic.ui.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class OpenDefaultPerspectiveHandler extends AbstractHandler {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.OpenDefaultPerspective";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//TODO Check the selections a provide solution to solve problem, e.g. Dialog for user
		IPatientService service = Activator.getPatientService();
		IAlgorithm algorithm = (IAlgorithm) service.getSelection(IPatientService.ALGORITHM);
		
		//Analyze data -> implement job to provide progressbar
		Data[] sensorData = (Data[]) service.getSelection(IPatientService.SENSOR_DATA);
		if(sensorData == null) {
			ISensorDataContainer<Data> container = (ISensorDataContainer<Data>) service.getSelection(IPatientService.SENSOR_CONTAINER);
			ISensor<Data> sensor = (ISensor<Data>) service.getSelection(IPatientService.SENSOR);
			try {
				sensorData = container.getSensorData(sensor.getConverter());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		IAnalyzedData data = algorithm.process(sensorData);
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
			throw new RuntimeException(ID + " error", ex);
		}

	}

}
