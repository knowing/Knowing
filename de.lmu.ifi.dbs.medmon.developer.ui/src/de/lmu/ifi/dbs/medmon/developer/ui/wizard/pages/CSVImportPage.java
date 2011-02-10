package de.lmu.ifi.dbs.medmon.developer.ui.wizard.pages;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FileSystemElement;
import org.eclipse.ui.dialogs.WizardResourceImportPage;
import org.eclipse.ui.model.WorkbenchContentProvider;

public class CSVImportPage extends WizardResourceImportPage {



	public CSVImportPage(IStructuredSelection selection) {
		super("wizardPage", selection);
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}



	@Override
	protected void createSourceGroup(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new FillLayout());
		new Label(composite, SWT.NONE).setText("Source Group Composite");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.WizardResourceImportPage#getFileProvider()
	 */
	protected ITreeContentProvider getFileProvider() {
		return new WorkbenchContentProvider() {

			public Object[] getChildren(Object element) {
				if (element instanceof FileSystemElement) {
					return ((FileSystemElement) element).getFiles()
						.getChildren(element);
				}
				return new Object[0];
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.WizardResourceImportPage#getFolderProvider()
	 */
	protected ITreeContentProvider getFolderProvider() {
		return new WorkbenchContentProvider() {

			public Object[] getChildren(Object element) {
				if (element instanceof FileSystemElement) {
					return ((FileSystemElement) element).getFolders()
						.getChildren(element);
				}
				return new Object[0];
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.WizardResourceImportPage#getSelectedResources()
	 */
	public List getSelectedResources() {
		return super.getSelectedResources();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.WizardResourceImportPage#getSpecifiedContainer()
	 */
	public IContainer getSpecifiedContainer() {
		return super.getSpecifiedContainer();
	}

}
