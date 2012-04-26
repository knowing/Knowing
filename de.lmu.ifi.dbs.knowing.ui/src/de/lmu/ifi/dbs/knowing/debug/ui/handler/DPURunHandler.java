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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate;
import de.lmu.ifi.dbs.knowing.debug.ui.launching.BundlesResolver;

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
		IDataProcessingUnit dpu = (IDataProcessingUnit) getModelElement();
		try {
			ILaunchConfiguration configuration = createLaunchConfiguration(dpu);
			launch(configuration);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ILaunchConfiguration createLaunchConfiguration(IDataProcessingUnit dpu) throws CoreException {
		String dpuName = dpu.getName().getContent();
		if (launchManager.isExistingLaunchConfigurationName(dpuName)) {
			ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations();
			for (int i = 0; i < configurations.length; i++) {
				if(configurations[i].getName().equals(dpuName))
					return configurations[i];
			}
		}
			
		// Create valid launch configuration name

		ILaunchConfigurationType dpuLaunchType = launchManager.getLaunchConfigurationType(DPULaunchConfigurationDelegate.LAUNCH_TYPE);
		ILaunchConfigurationWorkingCopy configuration = dpuLaunchType.newInstance(null, dpuName);
		return setLaunchAttributes(configuration, dpu);
	}

	private ILaunchConfiguration setLaunchAttributes(ILaunchConfigurationWorkingCopy configuration, IDataProcessingUnit dpu) throws CoreException {
		// Add OSGi settings
		configuration.setAttribute(DPULaunchConfigurationDelegate.SOURCE_PATH_PROVIDER, "org.eclipse.pde.ui.workbenchClasspathProvider");
		configuration.setAttribute(DPULaunchConfigurationDelegate.VM_ARGUMENTS, "-Declipse.ignoreApp=true -Dosgi.noShutdown=false");
		configuration.setAttribute(DPULaunchConfigurationDelegate.PROGRAM_ARGUMENTS, "-os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog -console");
		
		configuration.setAttribute("automaticAdd", true);
		configuration.setAttribute("automaticValidate", false);
		configuration.setAttribute("bootstrap", "");
		configuration.setAttribute("checked", "[NONE]");
		configuration.setAttribute("clearConfig", true);
		configuration.setAttribute("configLocation", "${workspace_loc}/.metadata/.plugins/org.eclipse.pde.core/" + dpu.getName().getContent());
		configuration.setAttribute("default", true);
		configuration.setAttribute("default_auto_start", true);
		configuration.setAttribute("default_start_level", 4);
//		configuration.setAttribute("deselected_workspace_plugins", true);
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

		// TODO check for parameters
		return configuration.doSave();
	}

	private void launch(ILaunchConfiguration configuration) throws CoreException {
		launchManager.addLaunch(configuration.launch("run", new NullProgressMonitor()));
	}

}
