package de.lmu.ifi.dbs.medmon.visualizer.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.sensor.core.algorithm.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.visualizer.ui.Activator;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class MonitorView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.lmu.ifi.dbs.medmon.visualizer.views.MonitorView";

	public MonitorView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		IAnalyzedData data = (IAnalyzedData) Activator.getPatientService()
								.getSelection(IPatientService.ANALYZED_DATA);
		if(data != null)
			data.createContent(parent);

		//JFreeChart chart = createChart(createDataset());
		//new ChartComposite(parent, SWT.NONE, chart, true);
	}

	public void setFocus() {

	}
}