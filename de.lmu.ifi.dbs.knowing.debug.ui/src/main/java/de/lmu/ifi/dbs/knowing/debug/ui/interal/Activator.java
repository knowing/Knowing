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
package de.lmu.ifi.dbs.knowing.debug.ui.interal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.lmu.ifi.dbs.knowing.core.service.IEvaluateService;
import de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate;
import de.lmu.ifi.dbs.knowing.debug.ui.views.DebugPresenterView;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements ILaunchesListener2 {

    // The plug-in ID
    public static final String PLUGIN_ID = "de.lmu.ifi.dbs.knowing.debug.ui"; //$NON-NLS-1$

    public static String DPU_SDEF = "/de/lmu/ifi/dbs/knowing/debug/ui/editor/DataProcessingUnitEditor.sdef";

    // The shared instance
    private static Activator plugin;

    private static ServiceTracker<IEvaluateService, IEvaluateService> evaluateServiceTracker;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        evaluateServiceTracker = new ServiceTracker<>(context, IEvaluateService.class, null);
        evaluateServiceTracker.open();
        DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        evaluateServiceTracker.close();
        evaluateServiceTracker = null;
        plugin = null;
        DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
        super.stop(context);
    }

    /* ================================================= */
    /* ========== Launch Listener Delegates ============ */
    /* ================================================= */

    @Override
    public void launchesRemoved(final ILaunch[] launches) {
        getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (delegate(launches)) {
                    openPresenterView().launchesRemoved(launches);
                }
            }
        });

    }

    @Override
    public void launchesAdded(final ILaunch[] launches) {
        getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (delegate(launches)) {
                    openPresenterView().launchesAdded(launches);
                }
            }
        });
    }

    @Override
    public void launchesChanged(final ILaunch[] launches) {
        getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (delegate(launches)) {
                    openPresenterView().launchesChanged(launches);
                }
            }
        });
    }

    @Override
    public void launchesTerminated(final ILaunch[] launches) {
        getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (delegate(launches)) {
                    openPresenterView().launchesTerminated(launches);
                }
            }
        });
    }

    private boolean delegate(ILaunch[] launches) {
        if (launches.length == 0) {
            return false;
        }
        // Only interested in DPU launch configurations
        ILaunchConfigurationType type;
        try {
            ILaunchConfiguration conf = launches[0].getLaunchConfiguration();
            if (conf == null) {
                return false;
            }
            type = launches[0].getLaunchConfiguration().getType();
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }

        if (!DPULaunchConfigurationDelegate.LAUNCH_TYPE_ID.equals(type.getIdentifier())) {
            return false;
        }
        return true;
    }

    /**
     * Must be called inside the UI thread!
     * 
     * @return
     */
    private ILaunchesListener2 openPresenterView() {
        try {
            //@formatter:off
            ILaunchesListener2 view = (ILaunchesListener2) getDefault()
                    .getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .showView(DebugPresenterView.ID);
            //@formatter:on
            return view;
        } catch (PartInitException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ================================================= */
    /* ========== Static Access Methods ================ */
    /* ================================================= */

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String imageFilePath) {
        return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
    }

    public static IEvaluateService getEvaluateService() {
        return evaluateServiceTracker.getService();
    }

}
