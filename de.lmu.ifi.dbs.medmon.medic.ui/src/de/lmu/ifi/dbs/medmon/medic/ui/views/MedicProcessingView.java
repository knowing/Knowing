package de.lmu.ifi.dbs.medmon.medic.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.base.ui.dialog.DialogFactory;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.pages.MPUMasterBlock;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ISharedImages;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;

public class MedicProcessingView extends ViewPart implements PropertyChangeListener {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.views.MedicProcessingView"; //$NON-NLS-1$
	private FormToolkit toolkit;
	private ManagedForm managedForm;
	
	private Table table;
	private Text tPatient;
	private Text tCluster;
	

	public MedicProcessingView() {
		Activator.getPatientService().addPropertyChangeListener(IPatientService.PATIENT, this);
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		managedForm = createManagedForm(parent);
		toolkit = managedForm.getToolkit();
		managedForm.getForm().getBody().setLayout(new GridLayout(2, false));
		managedForm.getToolkit().adapt(parent);
		Composite container = managedForm.getForm().getBody();
		toolkit.paintBordersFor(container);
		
		MPUMasterBlock block = new MPUMasterBlock();
		Composite blockComposite = toolkit.createComposite(container);
		blockComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		block.createContent(managedForm, blockComposite);
		
		// Patient Section
		Section sPatient = toolkit.createSection(container, Section.TWISTIE | Section.TITLE_BAR);
		sPatient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		toolkit.paintBordersFor(sPatient);
		sPatient.setText("Patient");
		sPatient.setExpanded(true);

		Composite patientClient = toolkit.createComposite(sPatient, SWT.NONE);
		toolkit.paintBordersFor(patientClient);
		sPatient.setClient(patientClient);
		patientClient.setLayout(new GridLayout(3, false));

		Label lPatient = toolkit.createLabel(patientClient, "Name", SWT.NONE);
		lPatient.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		tPatient = toolkit.createText(patientClient, "New Text", SWT.NONE);
		Patient p = (Patient) Activator.getPatientService().getSelection(IPatientService.PATIENT);
		if(p != null)
			tPatient.setText(p.toString());	
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 150;
		tPatient.setLayoutData(data);

		Button bPatient = toolkit.createButton(patientClient, "Auswaehlen", SWT.NONE);
		bPatient.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Patient patient = DialogFactory.openPatientSelectionDialog(getSite().getShell());
				if(patient == null)
					return;
				tPatient.setText(patient.toString());
				Activator.getPatientService().setSelection(patient, IPatientService.PATIENT);
			}
		});
		

		Label lCluster = toolkit.createLabel(patientClient, "Vergleichsdaten", SWT.NONE);
		lCluster.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		tCluster = toolkit.createText(patientClient, "New Text", SWT.NONE);
		tCluster.setText("");
		tCluster.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button bCluster = toolkit.createButton(patientClient, "Auswaehlen", SWT.NONE);

		// Date Section
		Section sData = toolkit.createSection(container, Section.TWISTIE | Section.TITLE_BAR);
		sData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		toolkit.paintBordersFor(sData);
		sData.setText("Daten");
		sData.setExpanded(true);

		Composite dataClient = toolkit.createComposite(sData, SWT.NONE);
		toolkit.paintBordersFor(dataClient);
		sData.setClient(dataClient);
		dataClient.setLayout(new GridLayout(1, false));
		
		// Running Section
		Section sRunning = toolkit.createSection(container, Section.TITLE_BAR);
		sRunning.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		sRunning.setText("Ausfuehren");
		sRunning.setExpanded(true);
		
		Composite runningClient = toolkit.createComposite(sRunning, SWT.NONE);
		runningClient.setLayout(new ColumnLayout());
		sRunning.setClient(runningClient);
		
		ImageHyperlink run = new ImageHyperlink(runningClient, SWT.NONE);
		run.setText("Daten verarbeiten");
		run.setImage(Activator.getImageDescriptor(ISharedImages.IMG_GO_NEXT_24).createImage());
		run.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(IMedmonConstants.OPEN_DEFAULT_VISUALIZE_PERSPECTIVE, null);
				} catch (Exception ex) {
					throw new RuntimeException(IMedmonConstants.OPEN_DEFAULT_VISUALIZE_PERSPECTIVE, ex);
				}
			}
		});
		
		ImageHyperlink runAndPersist = new ImageHyperlink(runningClient, SWT.NONE);
		runAndPersist.setText("Daten verarbeiten und in Datenbank speichern");
		
		runAndPersist.setImage(Activator.getImageDescriptor(ISharedImages.IMG_GO_NEXT_24).createImage());
		runAndPersist.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(IMedmonConstants.OPEN_DEFAULT_VISUALIZE_PERSPECTIVE, null);
				} catch (Exception ex) {
					throw new RuntimeException(IMedmonConstants.OPEN_DEFAULT_VISUALIZE_PERSPECTIVE, ex);
				}
			}
		});
		
		
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	public void dispose() {
		managedForm.dispose();
		super.dispose();
	}
	
	protected ManagedForm createManagedForm(final Composite parent) {
		ManagedForm managedForm = new ManagedForm(parent);
		managedForm.setContainer(this);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		managedForm.getForm().setLayoutData(gridData);
		return managedForm;
	}


	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		//
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

}
