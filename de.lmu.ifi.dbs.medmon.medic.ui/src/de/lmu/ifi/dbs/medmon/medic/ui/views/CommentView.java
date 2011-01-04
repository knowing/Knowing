package de.lmu.ifi.dbs.medmon.medic.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;

public class CommentView extends ViewPart implements PropertyChangeListener {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.CommentView"; //$NON-NLS-1$
	private Text tLastname, tFirstname, tGender, tComment;
	private CDateTime dBirth;
	private TabFolder tabFolder;
	
	private Action saveAction;

	public CommentView() {
		Activator.getPatientService().addPropertyChangeListener(IPatientService.PATIENT, this);
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new TabFolder(container, SWT.NONE);

		TabItem tPersonalData = new TabItem(tabFolder, SWT.NONE);
		tPersonalData.setText("Persoenliche Daten");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tPersonalData.setControl(composite);
		GridLayout gl_composite = new GridLayout(4, false);
		gl_composite.horizontalSpacing = 10;
		composite.setLayout(gl_composite);

		Label lName = new Label(composite, SWT.NONE);
		lName.setText("Name");
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		tLastname = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_tLastname = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_tLastname.widthHint = 150;
		tLastname.setLayoutData(gd_tLastname);

		Label lFirstname = new Label(composite, SWT.NONE);
		lFirstname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lFirstname.setText("Vorname");

		tFirstname = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_tFirstname = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_tFirstname.widthHint = 150;
		tFirstname.setLayoutData(gd_tFirstname);

		Label lBirth = new Label(composite, SWT.NONE);
		lBirth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lBirth.setText("Geb.");

		dBirth = new CDateTime(composite, CDT.BORDER | CDT.DATE_SHORT);
		dBirth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lGender = new Label(composite, SWT.NONE);
		lGender.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lGender.setText("Geschlecht");

		tGender = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_tGender = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_tGender.widthHint = 150;
		tGender.setLayoutData(gd_tGender);

		Label lComment = new Label(composite, SWT.NONE);
		lComment.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lComment.setText("Kommentar");

		tComment = new Text(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		tComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		Button bSave = new Button(composite, SWT.NONE);
		bSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bSave.setText("Speichern");

		TabItem tArchiv = new TabItem(tabFolder, SWT.NONE);
		tArchiv.setText("Patienten Akte");

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		saveAction = new Action("Speichern", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT)) {
			@Override
			public void run() {
				System.out.println("CommentView.createActions().new Action() {...}.run()");
			}
		};

	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
		menuManager.add(saveAction);
	}

	@Override
	public void setFocus() {
		tComment.setFocus();
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Patient patient = (Patient) Activator.getPatientService().getSelection(IPatientService.PATIENT);
		if(patient == null)
			return;
		tFirstname.setText(patient.getFirstname());
		tLastname.setText(patient.getLastname());
		tGender.setText(String.valueOf(patient.getGender()));
		dBirth.setSelection(patient.getBirth());
		if(patient.getComment() != null)
			tComment.setText(patient.getComment());
	}
	
	

}
