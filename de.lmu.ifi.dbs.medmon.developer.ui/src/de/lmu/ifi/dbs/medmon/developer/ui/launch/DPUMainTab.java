package de.lmu.ifi.dbs.medmon.developer.ui.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;

public class DPUMainTab extends AbstractLaunchConfigurationTab {
	private Text tDPU;
	private Text tCSV;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label lDPU = new Label(container, SWT.NONE);
		lDPU.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lDPU.setText("DPU");
		
		tDPU = new Text(container, SWT.BORDER);
		tDPU.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnChoose = new Button(container, SWT.NONE);
		btnChoose.setText("choose");
		
		Label lblCsvData = new Label(container, SWT.NONE);
		lblCsvData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCsvData.setText("CSV File");
		
		tCSV = new Text(container, SWT.BORDER);
		tCSV.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnChoose_1 = new Button(container, SWT.NONE);
		btnChoose_1.setText("choose");
		
		Group grpCsvConfiguration = new Group(container, SWT.NONE);
		grpCsvConfiguration.setText("CSV Configuration");
		grpCsvConfiguration.setLayout(new GridLayout(4, false));
		grpCsvConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		Label lblSeparator = new Label(grpCsvConfiguration, SWT.NONE);
		lblSeparator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSeparator.setText("Separator");
		
		Combo combo = new Combo(grpCsvConfiguration, SWT.NONE);
		new Label(grpCsvConfiguration, SWT.NONE);
		new Label(grpCsvConfiguration, SWT.NONE);
		
		Label lblField = new Label(grpCsvConfiguration, SWT.NONE);
		lblField.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblField.setText("Field1");
		
		Combo combo_1 = new Combo(grpCsvConfiguration, SWT.NONE);
		combo_1.setText("Type");
		
		Button button = new Button(grpCsvConfiguration, SWT.NONE);
		GridData gd_button = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_button.widthHint = 30;
		button.setLayoutData(gd_button);
		button.setText("-");
		
		Button button_1 = new Button(grpCsvConfiguration, SWT.NONE);
		GridData gd_button_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_button_1.widthHint = 30;
		button_1.setLayoutData(gd_button_1);
		button_1.setText("+");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
	
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	@Override
	public String getName() {
		return "DPU Configuration";
	}

}
