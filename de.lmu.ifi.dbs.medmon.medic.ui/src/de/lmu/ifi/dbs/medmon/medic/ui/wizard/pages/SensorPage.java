package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.jface.viewers.StructuredSelection;
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

import de.lmu.ifi.dbs.medmon.base.ui.viewer.SensorTableViewer;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.PatientProposalProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.TextContentAdapter2;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SensorPage extends WizardPage {

	private Button bSDRFile, bPatient;
	private Text tPatient, tSDRFile;
	private TableViewer sensorViewer;
	
	private IStructuredSelection initialSelection;
	private String initialPatient;
	
	private ISensorDataContainer data;
	
	private boolean flip = false;
		

	/**
	 * Create the wizard.
	 */
	public SensorPage() {
		super("Data");
		setTitle("Daten");
		setDescription("Daten auswaehlen");
	}
	
	public SensorPage(Patient patient, ISensor sensor) {
		this();
		initialPatient = PatientProposalProvider.parseString(patient);
		initialSelection = new StructuredSelection(sensor);
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
		if(initialPatient != null)
			tPatient.setText(initialPatient);
		createContentAssistent(tPatient);

		bPatient = new Button(container, SWT.NONE);
		bPatient.setText("Patient auswaehlen");
		bPatient.addListener(SWT.Selection, controller);

		sensorViewer = new SensorTableViewer(container, SWT.BORDER | SWT.FULL_SELECTION, initialSelection);
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

		setPageComplete(true);
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return flip;
	}
	
	private void createContentAssistent(Text text) {
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

	public ISensorDataContainer importData() {
		// Use Sample begin and end
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Daten laden", 0);
					try {
						IConverter converter = getSensor().getConverter();
						data = converter.convertToContainer(tSDRFile.getText(), ContainerType.WEEK, ContainerType.HOUR, null);
							
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
		return data;
	}

	private void done() {
		flip = !tSDRFile.getText().isEmpty() && (getPatient() != null);
		setPageComplete(flip);
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
				tPatient.setText(PatientProposalProvider.parseString(patient));
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
