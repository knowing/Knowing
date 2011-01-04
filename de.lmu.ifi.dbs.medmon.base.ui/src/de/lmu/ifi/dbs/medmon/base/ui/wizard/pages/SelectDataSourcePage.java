package de.lmu.ifi.dbs.medmon.base.ui.wizard.pages;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.lmu.ifi.dbs.medmon.base.ui.widgets.CSVSourceWidget;
import de.lmu.ifi.dbs.medmon.base.ui.widgets.DBSourceWidget;
import de.lmu.ifi.dbs.medmon.base.ui.widgets.SensorSourceWidget;

public class SelectDataSourcePage extends WizardPage {

	public static final String CSV = "CSV";
	public static final String DB = "Datenbank";
	public static final String SENSOR = "Sensor";

	private ComboViewer sourceViewer;
	
	private SensorSourceWidget sensorSource;
	private DBSourceWidget dbSource;
	private CSVSourceWidget csvSource;
	
	private Composite current;
	private String currentKey;
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
		sourceViewer.add(new String[] { CSV, SENSOR });
		sourceViewer.addSelectionChangedListener(new ISelectionChangedListener(	) {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection().isEmpty())
					return;
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				current = updateConfiguration((String) sel.getFirstElement());
				currentKey = (String) sel.getFirstElement();
				gConfiguration.layout(true);
			}
		});

		gConfiguration = new Group(container, SWT.NONE);
		gConfiguration.setLayout(new FillLayout());
		gConfiguration.setText("Konfiguration");
		gConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
			
	}
	
	public Object getConfiguration() {
		if(currentKey.equals(CSV))
			return csvSource.getDescriptor();
		else if(currentKey.equals(SENSOR))
			return sensorSource;
		else
			return null;
	}
	
	public String getCurrentKey() {
		return currentKey;
	}
	
	private Composite updateConfiguration(String key) {
		if(current != null)
			current.dispose();
		if(key.equals(CSV)) 
			return csvSource = new CSVSourceWidget(gConfiguration, SWT.NONE);
		else if(key.equals(DB))
			return dbSource = new DBSourceWidget(gConfiguration, SWT.NONE);
		else if(key.equals(SENSOR)) {
			return sensorSource = new SensorSourceWidget(gConfiguration, SWT.NONE);
		}
		return new Composite(gConfiguration, SWT.NONE);
	}


}
