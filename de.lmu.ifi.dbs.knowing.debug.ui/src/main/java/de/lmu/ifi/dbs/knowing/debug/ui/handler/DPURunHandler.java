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
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate;
import de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator;
import de.lmu.ifi.dbs.knowing.debug.ui.launching.BundlesResolver;
import de.lmu.ifi.dbs.knowing.debug.ui.views.DebugPresenterView;
import de.lmu.ifi.dbs.knowing.launcher.LaunchConfiguration;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-23
 * 
 */
public class DPURunHandler extends SapphireActionHandler {

    private final ILaunchManager launchManager;
    private static final Logger log = LoggerFactory.getLogger(DPURunHandler.class);

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

    @SuppressWarnings("restriction")
    private ILaunchConfiguration setLaunchAttributes(ILaunchConfigurationWorkingCopy configuration, IDataProcessingUnit dpu,
            SapphireRenderingContext context) throws CoreException {

        // Add OSGi settings
        configuration.setAttribute(LaunchConfiguration.SOURCE_PATH_PROVIDER_KEY(), LaunchConfiguration.SOURCE_PATH_PROVIDER());
        configuration.setAttribute(LaunchConfiguration.VM_ARGUMENTS_KEY(), LaunchConfiguration.VM_ARGUMENTS());
        configuration.setAttribute(LaunchConfiguration.PROGRAM_ARGUMENTS_KEY(), LaunchConfiguration.PROGRAM_ARGUMENTS());

        configuration.setAttribute(IPDELauncherConstants.AUTOMATIC_ADD, Boolean.TRUE);
        configuration.setAttribute(IPDELauncherConstants.AUTOMATIC_VALIDATE, Boolean.TRUE);
        configuration.setAttribute(IPDELauncherConstants.BOOTSTRAP_ENTRIES, "");
        configuration.setAttribute(IPDELauncherConstants.TRACING_CHECKED, IPDELauncherConstants.TRACING_NONE);
        configuration.setAttribute(IPDELauncherConstants.CONFIG_CLEAR_AREA, Boolean.TRUE);
        configuration.setAttribute(IPDELauncherConstants.CONFIG_LOCATION, "${workspace_loc}/.metadata/.plugins/org.eclipse.pde.core/"
                + dpu.getName().getContent());
        configuration.setAttribute(IPDELauncherConstants.USE_DEFAULT, Boolean.TRUE);
        configuration.setAttribute(IPDELauncherConstants.DEFAULT_AUTO_START, Boolean.TRUE);
        // configuration.setAttribute("deselected_workspace_plugins", true);
        configuration.setAttribute(IPDELauncherConstants.INCLUDE_OPTIONAL, Boolean.FALSE);

        configuration.setAttribute("pde.version", "3.3"); // TODO Determine
                                                          // correct PDE version

        // Add Bundles
        BundlesResolver resolver = new BundlesResolver(configuration);
        if (resolver.isBundleMissing()) {
            log.error("Bundles Missing: " + resolver.missingBundles());
        }
        configuration.setAttribute(IPDELauncherConstants.TARGET_BUNDLES, resolver.selectedBundles());

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
        if (dir == null)
            throw new CoreException(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Execution aborted. No execution path."));

        configuration.setAttribute(DPULaunchConfigurationDelegate.DPU_EXECUTION_PATH, dir);

        // TODO check for parameters
        return configuration.doSave();
    }

    private void launch(ILaunchConfiguration configuration) throws CoreException {
        launchManager.addLaunch(configuration.launch("run", new NullProgressMonitor()));
    }

}
