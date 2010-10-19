package de.lmu.ifi.dbs.medmon.algorithm.ui.views;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.algorithm.ui.Activator;
import de.lmu.ifi.dbs.medmon.algorithm.ui.viewer.AlgorithmConfigurationPart;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.sensor.core.algorithm.IAlgorithmParameter;
import de.lmu.ifi.dbs.medmon.sensor.core.algorithm.ISensorDataAlgorithm;

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

public class AlgorithmView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.lmu.ifi.dbs.medmon.algorithm.views.AlgorithmView";

	private ManagedForm managedForm;
	
	@Override
	public void createPartControl(Composite parent) {
		managedForm = createManagedForm(parent);
		managedForm.getForm().getBody().setLayout(new FillLayout());
		managedForm.getToolkit().adapt(parent);

		initialize(managedForm);
	}
	
	public void initialize(ManagedForm managedForm) {
		Section section = new Section(managedForm.getForm().getBody(), Section.NO_TITLE);
		AlgorithmConfigurationPart part = new AlgorithmConfigurationPart(section, getParameters());
		managedForm.addPart(part);
	}

	private Map<String, IAlgorithmParameter> getParameters() {
		ISensorDataAlgorithm algorithm = (ISensorDataAlgorithm) Activator
				.getPatientService().getSelection(IPatientService.ALGORITHM);
		if(algorithm != null)
			return algorithm.getParameters();
		return null;
	}

	protected ManagedForm createManagedForm(final Composite parent) {
		ManagedForm managedForm = new ManagedForm(parent);
		managedForm.setContainer(this);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		managedForm.getForm().setLayoutData(gridData);
		return managedForm;
	}

	@Override
	public void setFocus() {
		managedForm.getForm().setFocus();
	}

	private ISensorDataAlgorithm getAlgorithm() {
		return (ISensorDataAlgorithm) Activator.getPatientService()
				.getSelection(IPatientService.ALGORITHM);
	}
}