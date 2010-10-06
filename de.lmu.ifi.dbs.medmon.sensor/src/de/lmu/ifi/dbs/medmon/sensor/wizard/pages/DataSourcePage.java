package de.lmu.ifi.dbs.medmon.sensor.wizard.pages;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.sensor.converter.SDRConverter;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensorDataContainer;

public class DataSourcePage extends WizardPage {

	private boolean flip = false;

	private Composite container;

	private Button rFile, rUSB, rDatabase;
	private Button bFile, usb, testConnection;
	private Text file, url, user, pw;

	private ImportPageController controller;

	private ISensorDataContainer sensorData;

	public DataSourcePage() {
		super("Datenquelle");
		setTitle("Datenquellen");
		setDescription("Waehlen sie einen Datenquellen fuer die Sensordaten aus");
	}

	@Override
	public boolean canFlipToNextPage() {
		return flip;
	}

	@Override
	public void createControl(Composite parent) {
		controller = new ImportPageController();
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 15;
		container.setLayout(layout);

		rFile = new Button(container, SWT.RADIO);
		rFile.addListener(SWT.Selection, controller);

		Composite cFile = new Composite(container, SWT.NONE);
		cFile.setLayout(new GridLayout(2, false));

		file = new Text(cFile, SWT.BORDER);
		file.setLayoutData(new GridData(220, SWT.DEFAULT));
		bFile = new Button(cFile, SWT.PUSH);
		bFile.setText("Suche Sensordatei");
		bFile.addListener(SWT.Selection, controller);

		/* External Database */
		rDatabase = new Button(container, SWT.RADIO);
		rDatabase.addListener(SWT.Selection, controller);
		GridData data = new GridData();
		data.verticalAlignment = GridData.BEGINNING;
		rDatabase.setLayoutData(data);

		Composite database = new Composite(container, SWT.NONE);
		database.setLayout(new GridLayout(2, false));

		new Label(database, SWT.NONE).setText("URL: ");
		url = new Text(database, SWT.BORDER);
		url.setLayoutData(new GridData(150, SWT.DEFAULT));

		new Label(database, SWT.NONE).setText("Benutzer: ");
		user = new Text(database, SWT.BORDER);
		user.setLayoutData(new GridData(150, SWT.DEFAULT));

		new Label(database, SWT.NONE).setText("Passwort: ");
		pw = new Text(database, SWT.BORDER | SWT.PASSWORD);
		pw.setLayoutData(new GridData(150, SWT.DEFAULT));

		testConnection = new Button(database, SWT.PUSH);
		testConnection.setText("Verbindung testen");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		testConnection.setLayoutData(data);

		/* USB */

		rUSB = new Button(container, SWT.RADIO);
		rUSB.addListener(SWT.Selection, controller);
		usb = new Button(container, SWT.PUSH);
		usb.setText("Suche USB-Sensor");
		usb.addListener(SWT.Selection, controller);

		rFile.setSelection(true);
		setControl(container);
		setPageComplete(false);
	}

	private void done() {
		flip = true;
		setPageComplete(true);
	}

	public ISensorDataContainer getSensorData() {
		return sensorData;
	}

	public void importData() {
		//Use Sample begin and end
		try {
			sensorData = SDRConverter.convertSDRtoData(file.getText(), 0, 20);
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Fehler beim Import", e.getMessage());
		}
	}

	private class ImportPageController implements Listener {

		@Override
		public void handleEvent(Event e) {
			if(e.type == SWT.Selection) {
				//Enable the right selection
				file.setEnabled(rFile.getSelection());
				bFile.setEnabled(rFile.getSelection());
				
				url.setEnabled(rDatabase.getSelection());
				user.setEnabled(rDatabase.getSelection());
				pw.setEnabled(rDatabase.getSelection());
				testConnection.setEnabled(rDatabase.getSelection());
				
				usb.setEnabled(rUSB.getSelection());
				
				//Real logic
				if(e.widget == bFile)
					importFile();
				else if(e.widget == usb)
					;
					
				
			}		
		}

		/**
		 * Opens a dialog to select sdr/csv File and sets
		 * the path in the file variable.
		 */
		private void importFile() {
			String path = SDRConverter.importSDRFileDialog(getShell());
			if (path != null && !path.isEmpty()) {
				file.setText(path);
				done();
			}
		}
		
	}
}
