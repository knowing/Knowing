package de.lmu.ifi.dbs.medmon.sensor.ui.wizard.pages;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.TimeSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;
import de.lmu.ifi.dbs.medmon.sensor.ui.viewer.SensorTableViewer;
import de.sendsor.accelerationSensor.converter.SDRConverter;

public class SourcePage extends WizardPage {

	private Text tPatient, tSDRFile;

	private ISensorDataContainer data;
	private Patient patient;

	private boolean flip;
	private Button btnVorschau, bPatient, bSDRFile;
	private TableViewer sensorViewer;

	/**
	 * Create the wizard.
	 */
	public SourcePage() {
		super("Data");
		setTitle("Daten");
		setDescription("Wizard Page description");
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

		PageController controller = new PageController();
		tPatient = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false,	1, 1);
		data.widthHint = 170;
		tPatient.setLayoutData(data);

		bPatient = new Button(container, SWT.NONE);
		bPatient.setText("Patient auswaehlen");
		bPatient.addListener(SWT.Selection, controller);

		sensorViewer = new SensorTableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		sensorViewer.setInput(this); //TODO search for sensors
		Table table = sensorViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		tSDRFile = new Text(container, SWT.BORDER);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false,	1, 1);
		data.widthHint = 170;
		tSDRFile.setLayoutData(data);
		
		bSDRFile = new Button(container, SWT.NONE);
		bSDRFile.setText("Sensordatei");
		bSDRFile.addListener(SWT.Selection, controller);

		btnVorschau = new Button(container, SWT.CHECK);
		btnVorschau.setSelection(true);
		btnVorschau.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,	false, 2, 1));
		btnVorschau.setText("Vorschau (benoetigt mehr Zeit)");

		setPageComplete(false);
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return flip;
	}

	public void importData() {
		// Data already imported
		if (data != null)
			return;
		// Use Sample begin and end
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Daten laden", 20);
					try {
						//data = SDRConverter.convertSDRtoContainer(tSDRFile.getText(), 0, 1200);
						Block[] blocks = SDRConverter.convertSDRtoBlock(tSDRFile.getText(), Calendar.DAY_OF_YEAR);
						data = new RootSensorDataContainer();
						for(Block block : blocks) 
							data.addChild(new TimeSensorDataContainer(ISensorDataContainer.DAY, block));
						
							
					} catch (IOException e) {
						e.printStackTrace();
						MessageDialog.openError(getShell(),	"Fehler beim Import", e.getMessage());
					}
				}
			});

		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void done() {
		flip = !tSDRFile.getText().isEmpty() && (patient != null);
		getContainer().updateButtons();
	}

	public ISensorDataContainer getData() {
		return data;
	}

	public Patient getPatient() {
		return patient;
	}
	
	public ISensor getSensor() {
		IStructuredSelection selection = (IStructuredSelection) sensorViewer.getSelection();
		if(selection.isEmpty())
			return null;
		return (ISensor) selection.getFirstElement();
	}

	private class PageController implements Listener {

		@Override
		public void handleEvent(Event event) {
			if (event.type == SWT.Selection) {
				if (event.widget == bPatient)
					selectPatient();
				else if (event.widget == bSDRFile)
					importFile();
			}
		}

		private void importFile() {
			String path = SDRConverter.importSDRFileDialog(getShell());
			if (path != null && !path.isEmpty()) {
				tSDRFile.setText(path);
				done();
			}
		}

		private void selectPatient() {
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(	getShell(), new LabelProvider());
			dialog.setBlockOnOpen(true);
			dialog.setTitle("Patient auswaehlen");
			dialog.setElements(loadPatients());
			if (dialog.open() == Window.OK) {
				// Assuming that there's only one Patient Selection
				patient = (Patient) dialog.getResult()[0];
				tPatient.setText(patient.toString());
				done();
			}
		}

		private Patient[] loadPatients() {
			EntityManager em = JPAUtil.currentEntityManager();
			em.getTransaction().begin();
			Query allPatients = em.createNamedQuery("Patient.findAll");
			List<Patient> patients = allPatients.getResultList();
			em.getTransaction().commit();
			return patients.toArray(new Patient[patients.size()]);
		}
	}

}
