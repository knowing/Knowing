package de.lmu.ifi.dbs.medmon.therapy.pages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.core.algorithm.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.therapy.Activator;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.ITherapy;

public class TherapyDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;
	private Text tName;
	private Text tDescription;

	/**
	 * Create the details page.
	 */
	public TherapyDetailsPage() {
		// Create the details page
	}

	/**
	 * Initialize the details page.
	 * @param form
	 */
	public void initialize(IManagedForm form) {
		managedForm = form;
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		parent.setLayout(new FillLayout());
		//		
		Section section = toolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		section.setText("Therapie");
		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Label lName = new Label(composite, SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toolkit.adapt(lName, true, true);
		lName.setText("Name");
		
		tName = new Text(composite, SWT.BORDER);
		tName.setEditable(false);
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolkit.adapt(tName, true, true);
		
		Group gDescription = new Group(composite, SWT.NONE);
		gDescription.setText("Beschreibung");
		gDescription.setLayout(new FillLayout(SWT.HORIZONTAL));
		gDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		toolkit.adapt(gDescription);
		toolkit.paintBordersFor(gDescription);
		
		tDescription = new Text(gDescription, SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		tDescription.setEditable(false);
		toolkit.adapt(tDescription, true, true);
		
		ImageHyperlink analyse = toolkit.createImageHyperlink(composite, SWT.NONE);
		analyse.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_SEARCH_48));
		analyse.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		toolkit.paintBordersFor(analyse);
		analyse.setText("Analysieren und anzeigen");
		analyse.addHyperlinkListener(new HyperlinkAdapter() {
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
		new Label(composite, SWT.NONE);
	}

	public void dispose() {
		// Dispose
	}

	public void setFocus() {
		// Set focus
	}

	private void update() {
		// Update
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(!selection.isEmpty()) {
			ITherapy therapy = (ITherapy) structuredSelection.getFirstElement();
			tName.setText(therapy.getName());
			tDescription.setText(therapy.getDescription());
			Activator.getPatientService().setSelection(therapy.getAnalysers(), ISensorDataAlgorithm.class.getName());
		}
		update();
	}

	public void commit(boolean onSave) {
		// Commit
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		update();
	}
}
