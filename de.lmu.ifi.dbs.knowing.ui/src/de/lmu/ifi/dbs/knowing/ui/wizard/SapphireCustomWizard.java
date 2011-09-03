package de.lmu.ifi.dbs.knowing.ui.wizard;

import static org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil.toImageDescriptor;

import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.SapphirePart.ImageChangedEvent;
import org.eclipse.sapphire.ui.SapphirePartEvent;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphireWizardPagePart;
import org.eclipse.sapphire.ui.SapphireWizardPart;
import org.eclipse.sapphire.ui.def.ISapphireWizardDef;
import org.eclipse.sapphire.ui.def.SapphireUiDefFactory;
import org.eclipse.sapphire.ui.swt.SapphireWizardPage;
import org.eclipse.sapphire.workspace.WorkspaceFileResourceStore;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.model.IEdge;
import de.lmu.ifi.dbs.knowing.core.model.INode;

public class SapphireCustomWizard extends Wizard implements INewWizard {

	private WizardNewFileCreationPage createFilePage;
	private IStructuredSelection selection;

	private IDataProcessingUnit element;
	private final SapphireWizardPart part;
	private final SapphirePartListener listener;
	private final ISapphireWizardDef definition;

	public SapphireCustomWizard(IDataProcessingUnit element, final String wizardDefPath) {
		this.element = element;
		definition = SapphireUiDefFactory.getWizardDef(wizardDefPath);

		part = new SapphireWizardPart();
		part.init(null, element, definition, Collections.<String, String> emptyMap());

		setWindowTitle(this.part.getLabel());

		listener = new SapphirePartListener() {
			@Override
			public void handleEvent(final SapphirePartEvent event) {
				if (event instanceof ImageChangedEvent) {
					refreshImage();
				}
			}
		};

		part.addListener(listener);
		refreshImage();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		if(selection == null )
			selection = new StructuredSelection();
		addPage(createFilePage = new WizardNewFileCreationPage("DPU Source", (IStructuredSelection) selection));
		for (SapphireWizardPagePart pagePart : part.getPages())
			addPage(new SapphireWizardPage(pagePart));
	}


	@Override
	public boolean performFinish() {
		try {
			IFile file = createFilePage.createNewFile();
			try {
				XmlResourceStore store = new XmlResourceStore(new WorkspaceFileResourceStore(file));
				RootXmlResource resource = new RootXmlResource(store);
				IDataProcessingUnit dpu = element.TYPE.instantiate(resource);
				copyContents(element, dpu);
				store.save();
			} catch (ResourceStoreException e) {
				e.printStackTrace();
			}
			element.resource().save();
		} catch (ResourceStoreException e) {
			e.printStackTrace();
		}
		return true;
	}

	private final void refreshImage() {
		setDefaultPageImageDescriptor(toImageDescriptor(this.part.getImage()));
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	protected void copyContents(IDataProcessingUnit source, IDataProcessingUnit destination) {
		destination.setName(source.getName().getContent());
		destination.setDescription(source.getDescription().getContent());
		destination.setTags(source.getTags().getContent());
		for(INode node : source.getNodes()) {
			INode nodeNew = destination.getNodes().addNewElement();
			nodeNew.setId(node.getId().getContent());
			nodeNew.setFactoryId(node.getFactoryId().getContent());
			nodeNew.setType(node.getType().getContent());
		}
		for(IEdge edge : source.getEdges()) {
			IEdge edgeNew = destination.getEdges().addNewElement();
			edgeNew.setEdgeId(edge.getEdgeId().getContent());
			edgeNew.setSourceID(edge.getSourceId().getContent());
			edgeNew.setTargetID(edge.getTargetId().getContent());
		}
	}

}
