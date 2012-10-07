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
package de.lmu.ifi.dbs.knowing.debug.ui.views;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Tuple2;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import de.lmu.ifi.dbs.knowing.core.events.Status;
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory;
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.swt.factory.UIFactories;
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil;
import de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate;
import de.lmu.ifi.dbs.knowing.debug.presenter.DebugUIFactory;
import de.lmu.ifi.dbs.knowing.debug.presenter.PresentationDPUBuilder;
import de.lmu.ifi.dbs.knowing.debug.presenter.ProgressReader;
import de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator;

public class DebugPresenterView extends ViewPart implements ILaunchesListener2, UncaughtExceptionHandler {

    public static final String ID = "de.lmu.ifi.dbs.knowing.debug.ui.views.DebugPresenterView"; //$NON-NLS-1$
    private static final Logger log = LoggerFactory.getLogger(ID);

    private ILaunch currentLaunch;
    private UIFactory<Composite> uiFactory;

    /**
     * Create contents of the view part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        uiFactory = UIFactories.newTabUIFactoryInstance(container, ID);

        // FIXME issue #61
        DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void launchesAdded(ILaunch[] launches) {
        try {
            if (launches.length == 0)
                return;

            ILaunch launch = launches[0];

            // Only interested in DPU launch configurations
            ILaunchConfigurationType type = launch.getLaunchConfiguration().getType();

            if (!DPULaunchConfigurationDelegate.LAUNCH_TYPE_ID.equals(type.getIdentifier()))
                return;

            if (currentLaunch != null) {
                getSite().getShell().getDisplay().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        MessageDialog.openError(getSite().getShell(), "Another DPU is currently running",
                                "Stop the other instance running.");
                    }
                });

                // Ask what to do. Stop current or new launch
                launch.terminate();
                return;
            }

            String executionPath = launch.getLaunchConfiguration().getAttribute(DPULaunchConfigurationDelegate.DPU_EXECUTION_PATH, "");
            ExecutionWatcherRunnable watcherRunnable = new ExecutionWatcherRunnable(executionPath, launch);
            Thread thread = new Thread(watcherRunnable);
            thread.setName("ExecutionWatcherThread");
            thread.start();
            currentLaunch = launch;

        } catch (CoreException | IOException e) {
            log.error("Error on adding launch", e);
            ErrorDialog.openError(getSite().getShell(), "Error while launching DPU", e.getMessage(), new org.eclipse.core.runtime.Status(
                    IStatus.ERROR, Activator.PLUGIN_ID, null, e));
        }
    }

    @Override
    public void launchesTerminated(ILaunch[] launches) {
        if (launches.length == 0 || currentLaunch == null)
            return;

        for (int i = 0; i < launches.length; i++) {
            if (currentLaunch.equals(launches[i]))
                handleTermination(launches[i]);
        }
    }

    /**
     * <ol>
     * <li>Read DPU</li>
     * <li>Create DPU with ArffLoader -> Presenter for each file</li>
     * <li>Execute DPU in the IDE</li>
     * </ol>
     */
    private void handleTermination(ILaunch launch) {
        currentLaunch = null;
        try {
            String executionPathString = launch.getLaunchConfiguration()
                    .getAttribute(DPULaunchConfigurationDelegate.DPU_EXECUTION_PATH, "");
            Path executionPath = Paths.get(executionPathString);
            Path appConf = executionPath.resolve("application.conf");

            Config config = ConfigFactory.parseURL(appConf.toUri().toURL());
            String dpuUriString = config.getString("dpu.uri");

            IDataProcessingUnit dpu = DPUUtil.deserialize(new URI(dpuUriString).toURL());
            IDataProcessingUnit presentation = PresentationDPUBuilder.create(dpu);

            Activator.getEvaluateService().evaluate(presentation, executionPath.toUri(), uiFactory, null, null, null, null);
        } catch (CoreException | MalformedURLException | URISyntaxException e) {
            log.error("Error on handling termination", e);
        } catch (Exception e) {
            log.error("Error on handling termination", e);
        }
    }

    @Override
    public void launchesChanged(ILaunch[] launches) {
    }

    @Override
    public void launchesRemoved(ILaunch[] launches) {
    }

    @Override
    public void dispose() {
        DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
        super.dispose();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Uncaught exception in thread " + t.getName(), e);
        // TODO implement exception handler
    }

    /* =================================================== */
    /* ============== Execution Thread Class ============ */
    /* =================================================== */

    /**
     * Watches for .log and .progress files in the execution path.
     * 
     * @author Nepomuk Seiler
     * 
     */
    private class ExecutionWatcherRunnable implements Runnable {

        boolean terminated = false;

        private final Path executionPath;
        private final ILaunch launch;
        private ProgressReader progressReader;

        ExecutionWatcherRunnable(String executionPath, ILaunch launch) throws IOException {
            this.launch = launch;
            this.executionPath = Paths.get(executionPath);
        }

        @Override
        public void run() {
            log.info("Watching execution path [" + executionPath + "]");
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                executionPath.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                while (!terminated) {
                    // TODO make the timeout configurable
                    WatchKey watchKey = watchService.poll(5, TimeUnit.SECONDS);
                    if (watchKey == null) {
                        // inform user
                        return;
                    }
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    for (WatchEvent<?> event : events) {
                        terminated = processEvent(event);
                    }

                    if (!watchKey.reset()) {
                        terminated = true;
                    }
                }

                log.info("Terminating launch");
                launch.terminate();
            } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
                log.error("Error on waiting for progress", e);
                throw new RuntimeException(e);
            } catch (DebugException e) {
                log.error("Error on termination", e);
            }

            // Inform user -> show DebugPresenterView
            getSite().getShell().getDisplay().asyncExec(new Runnable() {

                @Override
                public void run() {
                    try {
                        getSite().getWorkbenchWindow().getActivePage().showView(ID);
                    } catch (PartInitException e) {
                        ErrorDialog.openError(getSite().getShell(), "Error while opening DebugPresenterView", e.getMessage(),
                                new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
                    }
                }
            });

        }

        /**
         * 
         * @param event
         * @return true if terminated
         * @throws InterruptedException
         * @throws ExecutionException
         * @throws TimeoutException
         * @throws IOException
         */
        private boolean processEvent(WatchEvent<?> event) throws InterruptedException, ExecutionException, TimeoutException, IOException {
            WatchEvent.Kind<?> kind = event.kind();
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path filename = ev.context();
            switch (filename.toString()) {
            case ".log":
                handleLogChanged(ev);
                return false;
            case ".progress":
                return handleProgressChange(ev);
            default:
                return false;
            }

        }

        private void handleLogChanged(WatchEvent<Path> event) {
            Path filename = event.context();
        }

        /**
         * 
         * @param event
         * @return true if progress terminated
         * @throws IOException
         */
        private boolean handleProgressChange(WatchEvent<Path> event) throws IOException {
            if (!openReader(event))
                return false;

            // Read from .progress file
            final List<Tuple2<String, Status>> status = progressReader.readAllStatus();

            // Update UI
            getSite().getShell().getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    for (Tuple2<String, Status> classStatus : status) {
                        // TODO update dialog here
                    }
                }
            });

            // check for termination
            for (Tuple2<String, Status> classStatus : status) {
                Status s = classStatus._2();
                if (s instanceof de.lmu.ifi.dbs.knowing.core.events.Shutdown) {
                    return true;
                }

            }
            return false;
        }

        private boolean openReader(WatchEvent<Path> event) throws IOException {
            if (progressReader != null)
                return true;
            if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE) && progressReader != null) {
                progressReader.close();
                progressReader = null;
                return false;
            }
            try {
                Path progressFile = this.executionPath.resolve(DebugUIFactory.PROGRESS_FILENAME);
                if (!Files.exists(progressFile))
                    return false;
                progressReader = new ProgressReader(Files.newBufferedReader(progressFile, Charset.defaultCharset()));
            } catch (IOException e) {
                log.error("IOError on reading progress file", e);
                return false;
            }
            return true;
        }

        private void showProgress(String progressFile) {

        }

    }

}
