package de.lmu.ifi.dbs.medmon.developer.ui.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import de.lmu.ifi.dbs.medmon.datamining.core.csv.io.CSVDescriptor;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.widget.CSVConfiguration;
import de.lmu.ifi.dbs.medmon.datamining.core.launch.DPULaunchDelegate;

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

		ModifyListener dirtyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		};

		Label lDPU = new Label(container, SWT.NONE);
		lDPU.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lDPU.setText("DPU");

		tDPU = new Text(container, SWT.BORDER);
		tDPU.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tDPU.addModifyListener(dirtyListener);

		Button bDPUFile = new Button(container, SWT.NONE);
		bDPUFile.setText("choose");
		bDPUFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Check out the workspace location
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IPath location = root.getLocation();
				//
				FileDialog dialog = new FileDialog(tDPU.getShell(), SWT.SINGLE);
				dialog.setFilterExtensions(new String[] { "*.xml" });
				dialog.setFilterNames(new String[] { "DPU XML File" });
				dialog.setFilterPath(location.toOSString());
				String file = dialog.open(); // Of the last selected file
				if (file != null)
					tDPU.setText(file);
			}
		});

		Label lCSVData = new Label(container, SWT.NONE);
		lCSVData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lCSVData.setText("CSV File");

		tCSV = new Text(container, SWT.BORDER);
		tCSV.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tCSV.addModifyListener(dirtyListener);

		Button bCSVFile = new Button(container, SWT.NONE);
		bCSVFile.setText("choose");
		bCSVFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(tCSV.getShell(), SWT.SINGLE);
				dialog.setFilterExtensions(new String[] { "*.csv" });
				dialog.setFilterNames(new String[] { "CSV File" });
				String file = dialog.open(); // Of the last selected file
				if (file != null) {
					csvConfiguration.setTestfile(file);
					tCSV.setText(file);
				}

			}
		});

		Group gCSVConfig = new Group(container, SWT.NONE);
		gCSVConfig.setText("CSV Configuration");
		gCSVConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		gCSVConfig.setLayout(new FillLayout());

		csvConfiguration = new CSVConfiguration(gCSVConfig, SWT.NONE);
		csvConfiguration.addModifyListener(dirtyListener);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(DPULaunchDelegate.CSV_SEPARATOR, ",");
		//configuration.setAttribute(DPULaunchDelegate.CSV_TEXT_QUALIFIER, "\"");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			// Initialize the descriptor
			String sep = configuration.getAttribute(DPULaunchDelegate.CSV_SEPARATOR, ",");
			//String qua = configuration.getAttribute(DPULaunchDelegate.CSV_TEXT_QUALIFIER, "\"");
			String pattern = configuration.getAttribute(DPULaunchDelegate.CSV_DATE_PATTERN,
					CSVDescriptor.DEFAULT_DATE_PATTERN);
			Map<String, String> fields = configuration.getAttribute(DPULaunchDelegate.CSV_FIELDS,
					new HashMap<String, String>());
			System.out.println("Fields: " + fields);
			CSVDescriptor descriptor = new CSVDescriptor();
			descriptor.setFieldSeparator(sep.charAt(0));
			//descriptor.setTextQualifier(qua.charAt(0));
			descriptor.setDatePattern(pattern);
			descriptor.setFields(DPULaunchDelegate.xmlToNative(fields));

			csvConfiguration.setDescriptor(descriptor);

			// Initialize the rest
			tCSV.setText(configuration.getAttribute(DPULaunchDelegate.CSV_FILE, ""));
			tDPU.setText(configuration.getAttribute(DPULaunchDelegate.DPU_FILE, ""));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// Save Descriptor
		CSVDescriptor descriptor = csvConfiguration.getDescriptor();
		configuration.setAttribute(DPULaunchDelegate.CSV_SEPARATOR,	new String(new char[] { descriptor.getFieldSeparator() }));
		//configuration.setAttribute(DPULaunchDelegate.CSV_TEXT_QUALIFIER,new String(new char[] { descriptor.getTextQualifier() }));
		configuration.setAttribute(DPULaunchDelegate.CSV_DATE_PATTERN, descriptor.getDatePattern());
		configuration.setAttribute(DPULaunchDelegate.CSV_FIELDS, DPULaunchDelegate.nativeToXML(descriptor.getFields()));
		// Save the rest
		configuration.setAttribute(DPULaunchDelegate.CSV_FILE, tCSV.getText());
		configuration.setAttribute(DPULaunchDelegate.DPU_FILE, tDPU.getText());
	}

	@Override
	public String getName() {
		return "DPU Configuration";
	}

}
