package de.lmu.ifi.dbs.medmon.medic.ui.handler;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.DataConverter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.Processor;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorAdapter;

public class OpenDefaultPerspectiveHandler extends AbstractHandler {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.OpenDefaultPerspective";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Check the selections a provide solution to solve problem, e.g.
		// Dialog for user
		IPatientService service = Activator.getPatientService();

		// Analyze data -> implement job to provide progressbar
		Data[] sensorData = null;

		ISensorDataContainer<Data> container = (ISensorDataContainer<Data>) service.getSelection(IPatientService.SENSOR_CONTAINER);
		SensorAdapter sensor = (SensorAdapter) service.getSelection(IPatientService.SENSOR);
		try {
			sensorData = container.getSensorData(sensor.getSensorExtension().getConverter());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Try to open the corresponding perspective
		try {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			window.getWorkbench().showPerspective(IMedmonConstants.VISUALIZE_PERSPECTIVE_DEFAULT, window);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}

		// Processing Data
		DataProcessingUnit dpu = (DataProcessingUnit) service.getSelection(IPatientService.DPU);
		Processor processor = Processor.getInstance();
		Map<String, IAnalyzedData> data = processor.run(dpu, DataConverter.convert(sensorData));
		// Set the new analyzed data
		service.setSelection(data, IPatientService.ANALYZED_DATA);
		return null;
	}

	public static void excuteCommand() {
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand(ID, null);
		} catch (Exception ex) {
			throw new RuntimeException(ID + " error", ex);
		}

	}

}
