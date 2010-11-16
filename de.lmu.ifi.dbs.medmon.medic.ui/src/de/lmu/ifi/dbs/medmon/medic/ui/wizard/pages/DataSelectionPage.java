package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.lmu.ifi.dbs.medmon.medic.ui.provider.PatientProposalProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.TextContentAdapter2;

public class DataSelectionPage extends WizardPage {
	private Text tPatient;

	/**
	 * Create the wizard.
	 */
	public DataSelectionPage() {
		super("wizardPage");
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
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.horizontalSpacing = 20;
		container.setLayout(gl_container);
		
		Label lPatient = toolkit.createLabel(container, "Patient ", SWT.NONE);
		lPatient.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			
		tPatient = toolkit.createText(container, "New Text", SWT.NONE);
		tPatient.setText("");
		tPatient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
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
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_label.heightHint = 15;
		lSeperator.setLayoutData(gd_label);
		
		Button bToday = toolkit.createButton(container, "heute", SWT.RADIO);
		bToday.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		
		Button bLastWeek = toolkit.createButton(container, "letzte Woche", SWT.RADIO);
		bLastWeek.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		
		Button bCustom = toolkit.createButton(container, "Von Bis", SWT.RADIO);
		bCustom.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		
		toolkit.paintBordersFor(container);
	}
	
	

}
