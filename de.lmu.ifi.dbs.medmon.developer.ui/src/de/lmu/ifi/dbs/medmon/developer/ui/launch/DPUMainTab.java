package de.lmu.ifi.dbs.medmon.developer.ui.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.lmu.ifi.dbs.medmon.developer.ui.csv.CSVConfiguration;

public class DPUMainTab extends AbstractLaunchConfigurationTab {
	private Text tDPU;
	private Text tCSV;
	private CSVConfiguration csvConfiguration;

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
		
		Button bDPUFile = new Button(container, SWT.NONE);
		bDPUFile.setText("choose");
		
		Label lCSVData = new Label(container, SWT.NONE);
		lCSVData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lCSVData.setText("CSV File");
		
		tCSV = new Text(container, SWT.BORDER);
		tCSV.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button bCSVFile = new Button(container, SWT.NONE);
		bCSVFile.setText("choose");
		bCSVFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(tCSV.getShell(), SWT.SINGLE);
				dialog.setFilterExtensions(new String[] {"*.csv"});
				dialog.setFilterNames(new String[] {"CSV File"});
				String file = dialog.open(); //Of the last selected file
				csvConfiguration.setTestfile(file);
				tCSV.setText(file);
			}
		});
		
		Group gCSVConfig = new Group(container, SWT.NONE);
		gCSVConfig.setText("CSV Configuration");
		gCSVConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		gCSVConfig.setLayout(new FillLayout());
		
		csvConfiguration = new CSVConfiguration(gCSVConfig, SWT.NONE);

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
