package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;

import de.lmu.ifi.dbs.medmon.medic.ui.viewer.SensorTableViewer;
import de.lmu.ifi.dbs.medmon.medic.ui.widgets.CSVSourceWidget;
import de.lmu.ifi.dbs.medmon.medic.ui.widgets.DBSourceWidget;

public class SelectDataSourcePage extends WizardPage {

	private static final String CSV = "CSV";
	private static final String DB = "Datenbank";
	private static final String SENSOR = "Sensor";

	private ComboViewer sourceViewer;
	
	private SensorTableViewer sensorSource;
	private DBSourceWidget dbSource;
	private CSVSourceWidget csvSource;
	
	private Composite current;
	private Group gConfiguration;

	/**
	 * Create the wizard.
	 */
	public SelectDataSourcePage() {
		super("SelectDataSourcePage");
		setTitle("Datenquelle");
		setDescription("Waehlen Sie eine Datenquelle aus");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		Label lSource = new Label(container, SWT.NONE);
		lSource.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lSource.setText("Quelle");

		sourceViewer = new ComboViewer(container, SWT.NONE);
		sourceViewer.add(new String[] { DB, CSV, SENSOR });
		sourceViewer.addSelectionChangedListener(new ISelectionChangedListener(	) {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection().isEmpty())
					return;
				if(current != null)
					current.dispose();
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				current = updateConfiguration((String) sel.getFirstElement());
				gConfiguration.layout(true);
			}
		});

		gConfiguration = new Group(container, SWT.NONE);
		gConfiguration.setLayout(new FillLayout());
		gConfiguration.setText("Konfiguration");
		gConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
			
	}
	
	private Composite updateConfiguration(String key) {
		if(key.equals(CSV)) 
			return csvSource = new CSVSourceWidget(gConfiguration, SWT.NONE);
		else if(key.equals(DB))
			return dbSource = new DBSourceWidget(gConfiguration, SWT.NONE);
		else if(key.equals(SENSOR)) {
			sensorSource = new SensorTableViewer(gConfiguration, SWT.BORDER | SWT.SINGLE);
			return sensorSource.getTable();
		}
		return new Composite(gConfiguration, SWT.NONE);
	}


}
