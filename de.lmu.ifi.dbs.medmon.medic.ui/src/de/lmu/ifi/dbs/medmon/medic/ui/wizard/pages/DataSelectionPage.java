package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import java.util.Date;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.PatientProposalProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.TextContentAdapter2;

public class DataSelectionPage extends WizardPage {
	
	private Text tPatient;
	private FormattedText tWeekStart,tWeekEnd, tToday;
	

	/**
	 * Create the wizard.
	 */
	public DataSelectionPage() {
		super("wizardPage");
		setMessage("Waehlt momentan alle Daten fuer jeweiligen Patienten");
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Composite container = toolkit.createComposite(parent);

		setControl(container);
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.horizontalSpacing = 8;
		container.setLayout(gl_container);
		
		toolkit.createLabel(container, "Patient ", SWT.NONE);
			
		tPatient = toolkit.createText(container, "New Text", SWT.NONE);
		tPatient.setText("");
		tPatient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		ControlDecoration deco = new ControlDecoration(tPatient, SWT.LEFT);
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
			ContentProposalAdapter adapter = new ContentProposalAdapter(tPatient,
					new TextContentAdapter2(),
					new PatientProposalProvider(),
					keyStroke, autoActivationCharacters);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Label lSeperator = toolkit.createLabel(container, "", SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_label.heightHint = 15;
		lSeperator.setLayoutData(gd_label);
		
		toolkit.paintBordersFor(container);
		
		Button bToday = new Button(container, SWT.RADIO);
		toolkit.adapt(bToday, true, true);
		bToday.setText("Heute");
		
		tToday = new FormattedText(container, SWT.NONE | SWT.READ_ONLY);
		tToday.setFormatter(new DateFormatter("dd.MM.yyyy"));
		tToday.setValue(new Date());
		tToday.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		toolkit.adapt(tToday.getControl(), true, false);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button bWeek = new Button(container, SWT.RADIO);
		toolkit.adapt(bWeek, true, true);
		bWeek.setText("Letzte Woche");
		
		tWeekStart = new FormattedText(container, SWT.NONE | SWT.READ_ONLY);
		tWeekStart.setFormatter(new DateFormatter("dd.MM.yyyy"));
		tWeekStart.setValue(new Date());
		tWeekStart.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		toolkit.adapt(tWeekStart.getControl(), true, false);
		
		toolkit.createLabel(container, "bis");
		
		tWeekEnd = new FormattedText(container, SWT.NONE | SWT.READ_ONLY);
		tWeekEnd.setFormatter(new DateFormatter("dd.MM.yyyy"));
		tWeekEnd.setValue(new Date());
		toolkit.adapt(tWeekEnd.getControl(), true, false);
		
		Button bCustomSelection = new Button(container, SWT.RADIO);
		toolkit.adapt(bCustomSelection, true, true);
		bCustomSelection.setText("Von");
		
		DateChooserCombo dateCustomStart = new DateChooserCombo(container, SWT.BORDER | SWT.FLAT);
		toolkit.adapt(dateCustomStart);
		toolkit.paintBordersFor(dateCustomStart);
		
		toolkit.createLabel(container, "bis");
		
		DateChooserCombo dateCustomEnd = new DateChooserCombo(container, SWT.BORDER | SWT.FLAT);
		dateCustomEnd.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		toolkit.adapt(dateCustomEnd);
		toolkit.paintBordersFor(dateCustomEnd);
	}

	public Patient getPatient() {
		return PatientProposalProvider.parsePatient(tPatient.getText());
	}
		

}
