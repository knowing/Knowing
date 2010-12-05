package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
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
import de.lmu.ifi.dbs.medmon.medic.ui.provider.PatientProposalProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.TextContentAdapter2;
import de.lmu.ifi.dbs.medmon.medic.ui.viewer.SensorTableViewer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SourcePage extends WizardPage {

	private Text tPatient, tSDRFile;

	private ISensorDataContainer data;
	
	private boolean flip;
	private Button bVorschau, bPatient, bSDRFile;
	private TableViewer sensorViewer;

	/**
	 * Create the wizard.
	 */
	public SourcePage() {
		super("Data");
		setTitle("Daten");
		setDescription("Daten auswaehlen");
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
		tPatient = new Text(container, SWT.BORDER);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false,	1, 1);
		data.widthHint = 170;
		tPatient.setLayoutData(data);
		createContentAssistent(tPatient);

		bPatient = new Button(container, SWT.NONE);
		bPatient.setText("Patient auswaehlen");
		bPatient.addListener(SWT.Selection, controller);

		sensorViewer = new SensorTableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		sensorViewer.setInput(this); //TODO search for sensors
		Table table = sensorViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		table.addListener(SWT.Selection, controller);

		tSDRFile = new Text(container, SWT.BORDER);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false,	1, 1);
		data.widthHint = 170;
		tSDRFile.setLayoutData(data);
		
		bSDRFile = new Button(container, SWT.NONE);
		bSDRFile.setText("Sensordatei");
		bSDRFile.setEnabled(false);
		bSDRFile.addListener(SWT.Selection, controller);

		bVorschau = new Button(container, SWT.CHECK);
		bVorschau.setSelection(true);
		bVorschau.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,	false, 2, 1));
		bVorschau.setText("Vorschau (benoetigt mehr Zeit)");

		setPageComplete(false);
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return flip;
	}
	
	public void createContentAssistent(Text text) {
		ControlDecoration deco = new ControlDecoration(text, SWT.LEFT);
		deco.setDescriptionText("Use CNTL + SPACE to see possible values");
		deco.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
		deco.setShowOnlyOnFocus(false);
		// Help the user with the possible inputs
		// "." and "#" will also activate the content proposals
		char[] autoActivationCharacters = new char[] { '.', '#' };
		KeyStroke keyStroke;
		try {
			// 
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
			// assume that myTextControl has already been created in some way
			ContentProposalAdapter adapter = new ContentProposalAdapter(text,
					new TextContentAdapter2(),
					new PatientProposalProvider(),
					keyStroke, autoActivationCharacters);
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
						IConverter converter = getSensor().getConverter();
						data = converter.readFile(tSDRFile.getText(), ContainerType.WEEK, ContainerType.HOUR, null);
							
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
		flip = !tSDRFile.getText().isEmpty() && (getPatient() != null);
		setPageComplete(flip);
	}

	public ISensorDataContainer getData() {
		return data;
	}

	public Patient getPatient() {
		return PatientProposalProvider.parsePatient(tPatient.getText());
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
				else if(event.widget == sensorViewer.getTable())
					bSDRFile.setEnabled(!sensorViewer.getSelection().isEmpty());
			}
		}

		private void importFile() {
			IConverter converter = getSensor().getConverter();
			String path = converter.openChooseInputDialog(getShell());
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
				Patient patient = (Patient) dialog.getResult()[0];
				tPatient.setText(patient.toString() + "<" + patient.getId() + ">");
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
