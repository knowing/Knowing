package de.lmu.ifi.dbs.medmon.sensor.dataImport.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class ImportPage2Source extends WizardPage {

	private boolean flip = true;

	private Composite container;

	protected ImportPage2Source() {
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
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 15;
		container.setLayout(layout);

		Button rFile = new Button(container, SWT.RADIO);
		Button file = new Button(container, SWT.PUSH);
		file.setText("Suche Sensordatei");

		/* External Database */
		Button rDatabase = new Button(container, SWT.RADIO);
		GridData data = new GridData();
		data.verticalAlignment = GridData.BEGINNING;
		rDatabase.setLayoutData(data);

		Composite database = new Composite(container, SWT.NONE);
		database.setLayout(new GridLayout(2, false));

		new Label(database, SWT.NONE).setText("URL: ");
		Text url = new Text(database, SWT.BORDER);
		url.setLayoutData(new GridData(150, SWT.DEFAULT));

		new Label(database, SWT.NONE).setText("Benutzer: ");
		Text user = new Text(database, SWT.BORDER);
		user.setLayoutData(new GridData(150, SWT.DEFAULT));

		new Label(database, SWT.NONE).setText("Passwort: ");
		Text pw = new Text(database, SWT.BORDER | SWT.PASSWORD);
		pw.setLayoutData(new GridData(150, SWT.DEFAULT));

		Button testConnection = new Button(database, SWT.PUSH);
		testConnection.setText("Verbindung testen");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		testConnection.setLayoutData(data);

		/* USB */

		Button rUSB = new Button(container, SWT.RADIO);
		Button usb = new Button(container, SWT.PUSH);
		usb.setText("Suche USB-Sensor");

		setControl(container);
	}

	public void importData() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) {
					monitor.beginTask("Importing Data", 100);
					for (int i = 0; i < 10; i++) {
						if (monitor.isCanceled())
							return;
						monitor.subTask("Dataset " + i);
						sleep(1000);
						monitor.worked(i);
					}
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Just for testing the job scheduler
	 * @param waitTime
	 */
	private void sleep(Integer waitTime) {
		try {
			Thread.sleep(waitTime);
		} catch (Throwable t) {
			System.out.println("Wait time interrupted");
		}
	}
	
	private class ImportPageController implements Listener {

		@Override
		public void handleEvent(Event e) {
			if(e.type == SWT.Selection) {
				
			}
			
		}
		
	}


}
