/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.debug.ui.wizard;

import static org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil.toImageDescriptor;

import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sapphire.modeling.IExecutableModelElement;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2011-08-04
 * 
 */
public abstract class SapphireCreateWizard<M extends IExecutableModelElement> extends Wizard implements INewWizard {
	// TODO This class could inherit from
	// org.eclipse.sapphire.ui.swt.SapphireWizard, but addPages and
	// performFinish are final

	// Sapphire specific
	private final M element;
	private final ISapphireWizardDef definition;
	private final SapphireWizardPart part;
	private final SapphirePartListener listener;

	// Wizard specific
	private IStructuredSelection selection;
	private WizardNewFileCreationPage createFilePage;

	/**
	 * @param element
	 * @param wizardDefPath
	 */
	public SapphireCreateWizard(M element, final String wizardDefPath) {
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
		// If the Wizard wasn't called via Strg+n
		if (selection == null)
			selection = new StructuredSelection();

		// Add WizardNewFileCreationPage
		String title = "Create " + element.getClass().getSimpleName();
		createFilePage = new WizardNewFileCreationPage(title, (IStructuredSelection) selection);
		addPage(createFilePage);

		// Add declared sapphire pages
		for (SapphireWizardPagePart pagePart : part.getPages())
			addPage(new SapphireWizardPage(pagePart));
	}

	@Override
	public boolean performFinish() {
		// Create file, WorkspaceResourceStore and copy content to
		// RootXmlResource handle by the store
		try {
			final IFile file = createFilePage.createNewFile();
			XmlResourceStore store = new XmlResourceStore(new WorkspaceFileResourceStore(file));
			RootXmlResource resource = new RootXmlResource(store);
			M newElement = element.getModelElementType().instantiate(resource);
			copyContents(element, newElement);
			store.save();
			
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IDE.openEditor(page, file, true);
					} catch (PartInitException e) {
					}
				}
			});
		} catch (ResourceStoreException e) {
			e.printStackTrace();
			return false;
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

	/**
	 * <p>
	 * Found no way how to copy contents from one to another IModelElement, so
	 * this method will do the work
	 * </p>
	 * 
	 * @param source
	 * @param destination
	 */
	abstract protected void copyContents(M source, M destination);

}
