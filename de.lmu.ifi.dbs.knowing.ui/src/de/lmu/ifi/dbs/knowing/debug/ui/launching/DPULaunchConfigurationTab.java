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
package de.lmu.ifi.dbs.knowing.debug.ui.launching;

import static de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator.DPU_SDEF;
import static de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator.PLUGIN_ID;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sapphire.ui.swt.SapphireControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.model.IParameter;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2011-04-17
 */
public class DPULaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	private static final String	DPU_PROJECT		= "knowing.dpu.project";

	/** Relative to project */
	private static final String	DPU_PATH		= "knowing.dpu.path";

	/** Absolute */
	private static final String	DPU_URI			= "knowing.dpu.uri";

	private static final String	DPU_PARAMETERS	= "knowing.dpu.parameters";

	private Text				txtDPU;
	private Text				txtDPUPath;
	private List<IParameter>	parameters;

	private SapphireControl		dpuControl;
	private IFile				dpuFile;

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		/* DPU selection */

		Group dpuComposite = new Group(container, SWT.BORDER);
		dpuComposite.setText("Data Processing Unit");
		dpuComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dpuComposite.setLayout(new GridLayout(3, false));

		Label lblDPU = new Label(dpuComposite, SWT.NONE);
		lblDPU.setText("Name: ");
		txtDPU = new Text(dpuComposite, SWT.BORDER | SWT.READ_ONLY);
		txtDPU.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(dpuComposite, SWT.NONE);

		Label lblDPUPath = new Label(dpuComposite, SWT.NONE);
		lblDPUPath.setText("Path: ");
		txtDPUPath = new Text(dpuComposite, SWT.BORDER);
		txtDPUPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button btnDPU = new Button(dpuComposite, SWT.PUSH);
		btnDPU.setText("Browse");
		btnDPU.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Use ElementListSelectionDialog and fill with all dpus
				// located in the workspace
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), root, IContainer.FILE);
				dialog.setTitle("Select a Data Processing Unit");
				dialog.setMessage("Choose one DPU");
				if (dialog.open() == Dialog.OK) {
					dpuFile = (IFile) dialog.getResult()[0];
					update();
				}
			}
		});

		/* Sapphire DPU Configuration */

		Group dpuConfigComposite = new Group(container, SWT.BORDER);
		dpuConfigComposite.setText("Parameters");
		dpuConfigComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		dpuConfigComposite.setLayout(new FillLayout());

		String def = PLUGIN_ID + DPU_SDEF + "!dpu.composite.parameters";
		dpuControl = new SapphireControl(dpuConfigComposite, IDataProcessingUnit.TYPE.instantiate(), def);

		setControl(container);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String dpuProject = configuration.getAttribute(DPU_PROJECT, (String)null);
			String path = configuration.getAttribute(DPU_PATH, (String)null);
			String uri = configuration.getAttribute(DPU_URI, (String)null);
			
			List<?> stringParameters = configuration.getAttribute(DPU_PARAMETERS, new ArrayList<>());
			// TODO (de)serialize method for parameters
			if(dpuProject == null || path == null) 
				return;
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(dpuProject);
			dpuFile = (IFile) project.findMember(path);
			update();			
			
			System.err.println("DPU Project " + dpuProject);
			System.err.println("DPU File " + dpuFile);
			System.err.println("DPU URI " + uri);
			System.err.println("DPU Parameters " + parameters);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if(dpuFile == null)
			return;
		configuration.setAttribute(DPU_PROJECT, dpuFile.getProject().getName());
		configuration.setAttribute(DPU_PATH, dpuFile.getProjectRelativePath().toOSString());
		configuration.setAttribute(DPU_URI, dpuFile.getLocationURI().toString());
		configuration.setAttribute(DPU_PARAMETERS, parameters);
		update();
	}
	
	private void update() {
		txtDPU.setText(dpuFile.getName());
		txtDPUPath.setText(dpuFile.getFullPath().toOSString());
	}

	@Override
	public String getName() {
		return "DPU Configuration";
	}

}
