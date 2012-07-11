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
package de.lmu.ifi.dbs.knowing.debug.ui.handler;

import java.nio.file.Paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate;
import de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator;
import de.lmu.ifi.dbs.knowing.debug.ui.launching.BundlesResolver;
import de.lmu.ifi.dbs.knowing.debug.ui.views.DebugPresenterView;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-23
 * 
 */
public class DPURunHandler extends SapphireActionHandler {

	private final ILaunchManager	launchManager;

	public DPURunHandler() {
		super();
		launchManager = DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	protected Object run(SapphireRenderingContext context) {
		try {
			// TODO create own perspective
			Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(DebugPresenterView.ID);
			IDataProcessingUnit dpu = (IDataProcessingUnit) getModelElement();
			ILaunchConfiguration configuration = createLaunchConfiguration(dpu, context);
			launch(configuration);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ILaunchConfiguration createLaunchConfiguration(IDataProcessingUnit dpu, SapphireRenderingContext context) throws CoreException {
		String dpuName = dpu.getName().getContent();
		if (launchManager.isExistingLaunchConfigurationName(dpuName)) {
			ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations();
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals(dpuName))
					return configurations[i];
			}
		}

		// Create valid launch configuration name

		ILaunchConfigurationType dpuLaunchType = launchManager.getLaunchConfigurationType(DPULaunchConfigurationDelegate.LAUNCH_TYPE_ID);
		ILaunchConfigurationWorkingCopy configuration = dpuLaunchType.newInstance(null, dpuName);
		return setLaunchAttributes(configuration, dpu, context);
	}

	private ILaunchConfiguration setLaunchAttributes(ILaunchConfigurationWorkingCopy configuration, IDataProcessingUnit dpu,
			SapphireRenderingContext context) throws CoreException {
		// Add OSGi settings
		configuration.setAttribute(DPULaunchConfigurationDelegate.SOURCE_PATH_PROVIDER, "org.eclipse.pde.ui.workbenchClasspathProvider");
		configuration.setAttribute(DPULaunchConfigurationDelegate.VM_ARGUMENTS, "-Declipse.ignoreApp=true -Dosgi.noShutdown=true");
		configuration.setAttribute(DPULaunchConfigurationDelegate.PROGRAM_ARGUMENTS,
				"-os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog -console");

		configuration.setAttribute("automaticAdd", true);
		configuration.setAttribute("automaticValidate", false);
		configuration.setAttribute("bootstrap", "");
		configuration.setAttribute("checked", "[NONE]");
		configuration.setAttribute("clearConfig", true);
		configuration.setAttribute("configLocation", "${workspace_loc}/.metadata/.plugins/org.eclipse.pde.core/"
				+ dpu.getName().getContent());
		configuration.setAttribute("default", true);
		configuration.setAttribute("default_auto_start", true);
		configuration.setAttribute("default_start_level", 4);
		// configuration.setAttribute("deselected_workspace_plugins", true);
		configuration.setAttribute("includeOptional", false);

		configuration.setAttribute("pde.version", "3.3"); // ?!

		// Add Bundles
		BundlesResolver resolver = new BundlesResolver(configuration);
		if (resolver.isBundleMissing()) {
			// TODO print error
			System.err.println("Bundles Missing: " + resolver.missingBundles());
		}
		configuration.setAttribute("target_bundles", resolver.selectedBundles());

		// Add DPU location
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IEditorInput input = editor.getEditorInput();
		IFile dpuFile = ((IFileEditorInput) input).getFile();
		IProject project = dpuFile.getProject();
		configuration.setAttribute(DPULaunchConfigurationDelegate.DPU_PROJECT, project.getName());
		configuration.setAttribute(DPULaunchConfigurationDelegate.DPU_PATH, dpuFile.getProjectRelativePath().toOSString());
		
		// Add Execution path
		DirectoryDialog execPathDialog = new DirectoryDialog(context.getShell());
		
		String projectPath = Paths.get(project.getLocationURI()).toString();
		
		execPathDialog.setFilterPath(projectPath);
		execPathDialog.setText("Select execution path");
		execPathDialog.setMessage("Select the execution where dpu should be executed.");

        String dir = execPathDialog.open();
        if(dir == null) 
        	throw new CoreException(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Execution aborted. No execution path."));

        configuration.setAttribute(DPULaunchConfigurationDelegate.DPU_EXECUTION_PATH, dir);

		// TODO check for parameters
		return configuration.doSave();
	}

	private void launch(ILaunchConfiguration configuration) throws CoreException {
		launchManager.addLaunch(configuration.launch("run", new NullProgressMonitor()));
	}

}
