package de.lmu.ifi.dbs.medmon.sensor.wizard.pages;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.sensor.converter.SDRConverter;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensorDataContainer;

public class SourcePage extends WizardPage {

	private Text tPatient, tSDRFile;
	private Button bPatient, bSDRFile;

	private ISensorDataContainer data;
	private Patient patient;
	
	private boolean flip;

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
		tPatient = new Text(container, SWT.BORDER);
		tPatient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1, 1));

		bPatient = new Button(container, SWT.NONE);
		bPatient.setText("Patient auswaehlen");
		bPatient.addListener(SWT.Selection, controller);

		tSDRFile = new Text(container, SWT.BORDER);
		tSDRFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		bSDRFile = new Button(container, SWT.NONE);
		bSDRFile.setText("Sensordatei");
		bSDRFile.addListener(SWT.Selection, controller);

	}

	public void importData() {
		// Use Sample begin and end
		try {
			data = SDRConverter.convertSDRtoData(tSDRFile.getText(), 1, 20);
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Fehler beim Import",
					e.getMessage());
		}
	}

	private void done() {
		flip = true;
		setPageComplete(true);
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
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider());
			dialog.setBlockOnOpen(true);
			dialog.setTitle("Patient auswaehlen");
			dialog.setElements(loadPatients());
			if (dialog.open() == Window.OK) {
				// Assuming that there's only one Patient Selection
				patient = (Patient) dialog.getResult()[0];
				done();
			}
		}
		
		private Patient[] loadPatients() {
			EntityManager em = JPAUtil.currentEntityManager();
			em.getTransaction().begin();
			Query allPatients = em.createNamedQuery("Patient.findAll");
			List<Patient> patients = allPatients.getResultList();
			
			return patients.toArray(new Patient[patients.size()]);
		}
	}

}
