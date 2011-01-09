package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import java.util.Date;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.lmu.ifi.dbs.medmon.base.ui.dialog.DialogFactory;
import de.lmu.ifi.dbs.medmon.database.model.Patient;

public class CreateArchivPage extends WizardPage {
	
	public static final String PAGE_NAME = "Archiv.create";
	
	private Text tPatient;
	private Text tTitel;
	private Text tComment;
	private FormattedText tDate;
	
	private Patient patient;
	
	/**
	 * Create the wizard.
	 */
	public CreateArchivPage(Patient patient) {
		super(PAGE_NAME);
		this.patient = patient;
		setTitle("Akteneintrag erstellen");
		setDescription("");
	}
	
	public CreateArchivPage() {
		this(null);
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(4, false));
		
		Label LTitle = new Label(container, SWT.NONE);
		LTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		LTitle.setText("Title");
		
		tTitel = new Text(container, SWT.BORDER);
		tTitel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lDate = new Label(container, SWT.NONE);
		lDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lDate.setText("Datum");
		
		tDate = new FormattedText(container, SWT.BORDER | SWT.READ_ONLY);
	    tDate.setFormatter(new DateFormatter("dd-MM-yyyy"));
	    tDate.setValue(new Date());
	    GridData data = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
	    data.widthHint = 70;
	    tDate.getControl().setLayoutData(data);
		
		Label lComment = new Label(container, SWT.NONE);
		lComment.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lComment.setText("Kommentar");
		
		tComment = new Text(container, SWT.BORDER | SWT.MULTI);
		tComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		Label LPatient = new Label(container, SWT.NONE);
		LPatient.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		LPatient.setText("Patient");
		
		tPatient = new Text(container, SWT.BORDER);
		tPatient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		if(patient != null)
			tPatient.setText(patient.toString());
		
		Button bPatient = new Button(container, SWT.NONE);
		bPatient.setText("Auswaehlen");
		bPatient.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Patient p = DialogFactory.openPatientSelectionDialog(getShell());
				if(p != null) {
					tPatient.setText(p.toString());
					patient = p;
				}
					
			}
		});
	}
	
	public String getComment() {
		return tComment.getText();
	}
	
	public String getTitle() {
		return tTitel.getText();
	}
	
	public Patient getPatient() {
		return patient;
	}

}
