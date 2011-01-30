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

public class OpenAnalysePerspectiveHandler extends AbstractHandler {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.OpenDefaultPerspective";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Try to open the corresponding perspective
		try {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			window.getWorkbench().showPerspective(IMedmonConstants.VISUALIZE_PERSPECTIVE_DEFAULT, window);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}

		return null;
	}


}
