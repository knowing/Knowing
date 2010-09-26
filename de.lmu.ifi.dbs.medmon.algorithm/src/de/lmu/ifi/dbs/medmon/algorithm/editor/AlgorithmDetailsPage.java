package de.lmu.ifi.dbs.medmon.algorithm.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.algorithm.ui.AlgorithmConfigurationPart;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.CommandUtil;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;

public class AlgorithmDetailsPage implements IDetailsPage {
	public AlgorithmDetailsPage() {
	}

	private IManagedForm managedForm;
	private Text name, version, description;
	private AlgorithmConfigurationPart algorithmPart;

	@Override
	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {	
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			ISensorDataAlgorithm algorithm = (ISensorDataAlgorithm) sel.getFirstElement();
			algorithmPart.setFormInput(algorithm);
			name.setText(algorithm.getName());
			version.setText(algorithm.getVersion());
			description.setText(algorithm.getDescription());
		}

	}

	@Override
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();	
		parent.setLayout(new ColumnLayout());
		
		Section gSection = toolkit.createSection(parent,Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		gSection.setText("Allgemein");
		
		Composite gClient = toolkit.createComposite(gSection);
		gClient.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(gClient, "Name");
		name = toolkit.createText(gClient, "<Algorithmusname>", SWT.READ_ONLY);
		
		toolkit.createLabel(gClient, "Version");
		version = toolkit.createText(gClient, "<Algorithmusversion>", SWT.READ_ONLY);
		
		Group gDescription = new Group(gClient, SWT.SHADOW_ETCHED_IN);
		gDescription.setText("Beschreibung");
		gDescription.setLayout(new FillLayout());
		GridData data = new GridData(GridData.FILL_BOTH);
		data.minimumHeight = 150;
		data.horizontalSpan = 2;
		gDescription.setLayoutData(data);
		
		description = toolkit.createText(gDescription, "<Algorithmusbeschreibung>", SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		toolkit.adapt(gDescription);
		//toolkit.paintBordersFor(gDescription);
		
		gSection.setClient(gClient);
		
		/* Configuration */
		
		Section pSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		pSection.setDescription("Algorithmus konfigurieren");
		
		algorithmPart = new AlgorithmConfigurationPart(pSection);
		managedForm.addPart(algorithmPart);
		
		/* Button Section */
		
		Section bSection = toolkit.createSection(parent,Section.NO_TITLE);
		Composite bClient = toolkit.createComposite(bSection);
		bClient.setLayout(new FillLayout());
		
		ImageHyperlink visualizeLink = toolkit.createImageHyperlink(bClient, SWT.NONE);
		visualizeLink.setText("Analyisieren und Anzeigen");
		visualizeLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.CHART_48));
		visualizeLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(IMedmonConstants.OPEN_DEFAULT_VISUALIZE_PERSPECTIVE, null);
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new RuntimeException(IMedmonConstants.OPEN_DEFAULT_VISUALIZE_PERSPECTIVE);
				}
			}
		});
		bSection.setClient(bClient);
		
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void commit(boolean onSave) {

	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
	}
}
