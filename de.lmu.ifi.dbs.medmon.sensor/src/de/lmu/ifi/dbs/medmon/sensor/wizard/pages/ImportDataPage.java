package de.lmu.ifi.dbs.medmon.sensor.wizard.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.medmon.sensor.viewer.SensorTableViewer;

public class ImportDataPage extends WizardPage{

	private Composite container;
	private SensorTableViewer viewer;
	
	public ImportDataPage() {
		super("Daten auswaehlen");
		setMessage("Die zu analysierenden Daten auswaehlen");
		setTitle("Sensordaten");
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
	
		setControl(container);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Table table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		viewer = new SensorTableViewer(table);
		//setPageComplete(false);
	}
}
