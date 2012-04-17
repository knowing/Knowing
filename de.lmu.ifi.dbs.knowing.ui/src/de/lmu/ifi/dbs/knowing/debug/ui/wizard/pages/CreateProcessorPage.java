package de.lmu.ifi.dbs.knowing.debug.ui.wizard.pages;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;

import de.lmu.ifi.dbs.knowing.debug.ui.wizard.NewProcessorWizard;

public class CreateProcessorPage extends WizardPage {

	private Text tName;
	private Text tPackage;
	private Text tExtends;
	private final IStructuredSelection selection;
	private IPackageFragment classPackage;

	/**
	 * Create the wizard.
	 */
	public CreateProcessorPage(IStructuredSelection selection) {
		super("Create Processor");
		this.selection = selection;
		setTitle("Create Processor");
		setDescription("Create processor implementation");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		Label lName = new Label(container, SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lName.setText("Name");

		tName = new Text(container, SWT.BORDER);
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		new Label(container, SWT.NONE);

		Label lPackage = new Label(container, SWT.NONE);
		lPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lPackage.setText("Package");

		tPackage = new Text(container, SWT.BORDER);
		tPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button bBrowsePackage = new Button(container, SWT.NONE);
		bBrowsePackage.setText("browse");
		bBrowsePackage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handlePackageBrowse();
			}
		});

		Label lExtends = new Label(container, SWT.NONE);
		lExtends.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lExtends.setText("Extends");

		tExtends = new Text(container, SWT.BORDER);
		tExtends.setText("");
		tExtends.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button tBrowseExtends = new Button(container, SWT.NONE);
		tBrowseExtends.setText("browse");
		validate();
	}

	private void handlePackageBrowse() {
		Object obj = selection.getFirstElement();

		IResource res = (IResource) Platform.getAdapterManager().getAdapter(obj, IResource.class);
		IProject project = res.getProject();
		try {
			SelectionDialog dialog = JavaUI.createPackageDialog(getShell(), JavaCore.create(project),
					IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS);
			if (dialog.open() != Window.OK)
				return;

			Object[] result = dialog.getResult();
			for (Object r : result) {
				IJavaElement e = (IJavaElement) r;
				classPackage = (IPackageFragment) e;
				tPackage.setText(e.getElementName());
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		validate();
	}

	private void validate() {
		String name = tName.getText();
		if (name == null) {
			updateStatus("Classname cannot be empty");
			return;
		}
		if (!name.endsWith(".java") && !name.endsWith(".scala")) {
			updateStatus("Classname needs to end with .java or .scala");
			return;
		}
		if (classPackage != null) {
			IFile ifile = NewProcessorWizard.locatePath(getPath());
			if (ifile.exists()) {
				updateStatus("File already exists");
				return;
			}
		}

		updateStatus(null);
		
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		if(message == null) setMessage("Create processor implementation", IMessageProvider.INFORMATION);
		setPageComplete(message == null);
	}

	public IPath getPath() {
		return classPackage.getPath().append(getClassName());
	}

	public String getClassName() {
		return tName.getText();
	}

	public IPackageFragment getClassPackage() {
		return classPackage;
	}

	public boolean isJava() {
		return tName.getText().endsWith(".java");
	}

	public boolean isScala() {
		return tName.getText().endsWith(".scala");
	}
}
