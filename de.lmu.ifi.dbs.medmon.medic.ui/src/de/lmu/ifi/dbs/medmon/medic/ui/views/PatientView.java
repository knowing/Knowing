package de.lmu.ifi.dbs.medmon.medic.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.base.ui.viewer.ClusterTableViewer;
import de.lmu.ifi.dbs.medmon.database.model.Archiv;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.medic.core.service.IPatientService;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ArchivLabelProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ISharedImages;
import de.lmu.ifi.dbs.medmon.medic.ui.util.MedicUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.TrainClusterWizard;

public class PatientView extends ViewPart implements PropertyChangeListener {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.CommentView"; //$NON-NLS-1$
	private TabFolder tabFolder;

	/* Personal Data */
	private Text tLastname, tFirstname, tGender, tComment;
	private CDateTime dBirth;

	private Action saveAction;
	private Action openAction;

	/* Archiv Tab */
	private Text tArchivSearch;
	private Table archivTable;
	private TableViewer archivViewer;

	/* Cluster Tab */
	private TableViewer clusterViewer;
	private Control tClusterSearch;
	private Text tPatientCluster;
	private TabItem tabCluster;

	public PatientView() {
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

		tabFolder = new TabFolder(container, SWT.BOTTOM);

		createPersonalTab();
		createArchivTab();		
		createClusterTab();

		createActions();
		initializeToolBar();
		initializeMenu();
		update();
	}

	/**
	 * 
	 */
	private void createPersonalTab() {
		/****** Personal Data Tab *******/
		/* 								*/
		TabItem tPersonalData = new TabItem(tabFolder, SWT.NONE);
		tPersonalData.setText("Persoenliche Daten");
		tPersonalData.setImage(Activator.getImageDescriptor(ISharedImages.IMG_PATIENTS_16).createImage());

		Composite cPatient = new Composite(tabFolder, SWT.NONE);
		tPersonalData.setControl(cPatient);
		GridLayout cPatientLayout = new GridLayout(4, false);
		cPatientLayout.horizontalSpacing = 10;
		cPatient.setLayout(cPatientLayout);

		Label lName = new Label(cPatient, SWT.NONE);
		lName.setText("Name");
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		tLastname = new Text(cPatient, SWT.BORDER | SWT.READ_ONLY);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 150;
		tLastname.setLayoutData(data);

		Label lFirstname = new Label(cPatient, SWT.NONE);
		lFirstname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lFirstname.setText("Vorname");

		tFirstname = new Text(cPatient, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		data.widthHint = 150;
		tFirstname.setLayoutData(data);

		Label lBirth = new Label(cPatient, SWT.NONE);
		lBirth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lBirth.setText("Geb.");

		dBirth = new CDateTime(cPatient, CDT.BORDER | CDT.DATE_SHORT);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 160;
		dBirth.setLayoutData(data);

		Label lGender = new Label(cPatient, SWT.NONE);
		lGender.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lGender.setText("Geschlecht");

		tGender = new Text(cPatient, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 150;
		tGender.setLayoutData(data);

		Label label = new Label(cPatient, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("Vergleichsdaten");
		
		Composite cPatientCluster = new Composite(cPatient, SWT.NONE);
		GridLayout gl_cPatientCluster = new GridLayout(2, false);
		gl_cPatientCluster.marginWidth = 0;
		cPatientCluster.setLayout(gl_cPatientCluster);
		cPatientCluster.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		
		tPatientCluster = new Text(cPatientCluster, SWT.BORDER);
		tPatientCluster.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tPatientCluster.setBounds(0, 0, 75, 25);
		
		Button bPatientLoadCluster = new Button(cPatientCluster, SWT.NONE);
		data = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 81;
		bPatientLoadCluster.setLayoutData(data);
		bPatientLoadCluster.setText("laden");
		bPatientLoadCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tabFolder.setSelection(tabCluster);
			}
		});

		
		Label lComment = new Label(cPatient, SWT.NONE);
		lComment.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lComment.setText("Kommentar");

		tComment = new Text(cPatient, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		tComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	}

	/**
	 * 
	 */
	private void createArchivTab() {

		TabItem tArchiv = new TabItem(tabFolder, SWT.NONE);
		tArchiv.setText("Patienten Akte");
		tArchiv.setImage(Activator.getImageDescriptor(ISharedImages.IMG_COMMENT_16).createImage());

		Composite cArchiv = new Composite(tabFolder, SWT.NONE);
		tArchiv.setControl(cArchiv);
		cArchiv.setLayout(new GridLayout(3, false));

		tArchivSearch = new Text(cArchiv, SWT.BORDER);
		tArchivSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		archivViewer = createArchivViewer(cArchiv);
		archivTable = archivViewer.getTable();
		archivTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		Composite cArchivButtonBar = new Composite(cArchiv, SWT.NONE);
		cArchivButtonBar.setLayout(new RowLayout());
		cArchivButtonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 3, 1));

		Button bNewArchiv = new Button(cArchivButtonBar, SWT.NONE);
		bNewArchiv.setLayoutData(new RowData(100, SWT.DEFAULT));
		bNewArchiv.setText("Neuer Eintrag");
		bNewArchiv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});

		Button bLoadArchiv = new Button(cArchivButtonBar, SWT.NONE);
		bLoadArchiv.setLayoutData(new RowData(100, SWT.DEFAULT));
		bLoadArchiv.setText("Eintrag laden");

		Button bRemoveArchiv = new Button(cArchivButtonBar, SWT.NONE);
		bRemoveArchiv.setLayoutData(new RowData(100, SWT.DEFAULT));
		bRemoveArchiv.setText("Eintrag entfernen");
	}

	/**
	 * 
	 */
	private void createClusterTab() {		 		 
	    tabCluster = new TabItem(tabFolder, SWT.NONE);
		tabCluster.setText("Trainingsdaten");
		tabCluster.setImage(Activator.getImageDescriptor(ISharedImages.IMG_CLUSTER_16).createImage());

		Composite cCluster = new Composite(tabFolder, SWT.NONE);
		cCluster.setLayout(new GridLayout());
		tabCluster.setControl(cCluster);

		tClusterSearch = new Text(cCluster, SWT.BORDER);
		tClusterSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		clusterViewer = new ClusterTableViewer(cCluster);
		clusterViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		Composite cClusterButtonBar = new Composite(cCluster, SWT.NONE);
		cClusterButtonBar.setLayout(new RowLayout());
		cClusterButtonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 3, 1));

		Button bNewCluster = new Button(cClusterButtonBar, SWT.NONE);
		bNewCluster.setLayoutData(new RowData(100, SWT.DEFAULT));
		bNewCluster.setText("Training");
		bNewCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IWizard wizard = new TrainClusterWizard();
				WizardDialog dialog = new WizardDialog(clusterViewer.getTable().getShell(), wizard);
				if(dialog.open() == Window.OK) {
					
				}
			}
		});

		Button bLoadCluster = new Button(cClusterButtonBar, SWT.NONE);
		bLoadCluster.setLayoutData(new RowData(100, SWT.DEFAULT));
		bLoadCluster.setText("Daten laden");

		Button bRemoveCluster = new Button(cClusterButtonBar, SWT.NONE);
		bRemoveCluster.setLayoutData(new RowData(100, SWT.DEFAULT));
		bRemoveCluster.setText("Daten entfernen");
		
		Button bClusterImport = new Button(cClusterButtonBar, SWT.NONE);
		bClusterImport.setLayoutData(new RowData(100, SWT.DEFAULT));
		bClusterImport.setText("Importieren");
	}

	private TableViewer createArchivViewer(Composite parent) {
		TableViewer archivViewer = new TableViewer(parent, SWT.BORDER | SWT.SINGLE);
		archivViewer.setContentProvider(new ArrayContentProvider());

		// Set visible
		archivViewer.getTable().setHeaderVisible(true);
		archivViewer.getTable().setLinesVisible(true);

		// Columns
		TableViewerColumn viewerColumn = new TableViewerColumn(archivViewer, SWT.LEAD);
		viewerColumn.getColumn().setText("Datum");
		viewerColumn.getColumn().setWidth(120);

		viewerColumn = new TableViewerColumn(archivViewer, SWT.LEAD);
		viewerColumn.getColumn().setText("Titel");
		viewerColumn.getColumn().setWidth(300);

		viewerColumn = new TableViewerColumn(archivViewer, SWT.LEAD);
		viewerColumn.getColumn().setText("Daten");
		viewerColumn.getColumn().setWidth(60);

		archivViewer.setLabelProvider(new ArchivLabelProvider());

		return archivViewer;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {

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
	}

	/**
	 * Initalize input
	 */
	private void update() {
		Patient patient = (Patient) Activator.getPatientService().getSelection(IPatientService.PATIENT);
		if (patient == null)
			return;
		
		//Update Personal Data
		tFirstname.setText(patient.getFirstname());
		tLastname.setText(patient.getLastname());
		tGender.setText(String.valueOf(patient.getGender()));
		dBirth.setSelection(patient.getBirth());
		if (patient.getComment() != null)
			tComment.setText(patient.getComment());

		//Update Archiv
		EntityManager em = JPAUtil.createEntityManager();
		List<Archiv> archives = em.createNamedQuery("Archiv.findByPatient", Archiv.class)
				.setParameter("patientId", patient.getId()).getResultList();
		archivViewer.setInput(archives);
		
		//Update Cluster
		clusterViewer.setInput(MedicUtil.loadClusterUnits(patient));
	}

	@Override
	public void setFocus() {
		tComment.setFocus();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		update();
	}
}
