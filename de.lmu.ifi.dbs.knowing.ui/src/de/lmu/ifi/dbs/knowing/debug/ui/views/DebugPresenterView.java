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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import scala.Tuple2;
import de.lmu.ifi.dbs.knowing.core.events.Shutdown;
import de.lmu.ifi.dbs.knowing.core.events.Status;
import de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate;
import de.lmu.ifi.dbs.knowing.debug.presenter.DebugUIFactory;
import de.lmu.ifi.dbs.knowing.debug.presenter.ProgressReader;
import de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator;

public class DebugPresenterView extends ViewPart implements ILaunchesListener2, UncaughtExceptionHandler {

	public static final String	ID	= "de.lmu.ifi.dbs.knowing.debug.ui.views.DebugPresenterView";	//$NON-NLS-1$
	private Text				txtConsole;

	private ILaunch				currentLaunch;

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		txtConsole = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);

		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
	}

	@Override
	public void setFocus() {
		txtConsole.setFocus();
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
				MessageDialog.openError(getSite().getShell(), "Another DPU is currently running", "Stop the other instance running.");
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
			e.printStackTrace();
			ErrorDialog.openError(getSite().getShell(), "Error while launching DPU", e.getMessage(), new org.eclipse.core.runtime.Status(
					IStatus.ERROR, Activator.PLUGIN_ID, null, e));
		}
	}


	@Override
	public void launchesTerminated(ILaunch[] launches) {
		if (launches.length == 0)
			return;
		
		for (int i = 0; i < launches.length; i++) {
			if(currentLaunch.equals(launches[i]))
				currentLaunch = null;
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
		//TODO implement exception handler
	}

	/* ============================================================================ */
	/* ============================ Execution Thread ============================== */
	/* ============================================================================ */
	
	
	/**
	 * Watches for .log and .progress files in the execution path.
	 * 
	 * @author Nepomuk Seiler
	 * 
	 */
	private class ExecutionWatcherRunnable implements Runnable {

		boolean					terminated	= false;

		private final Path		executionPath;
		private final ILaunch	launch;
		private ProgressReader	progressReader;

		ExecutionWatcherRunnable(String executionPath, ILaunch launch) throws IOException {
			this.launch = launch;
			this.executionPath = Paths.get(executionPath);
		}

		@Override
		public void run() {

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
				
				launch.terminate();
			} catch (IOException | InterruptedException | ExecutionException | TimeoutException | DebugException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
			//Inform user
			getSite().getShell().getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					MessageDialog.openInformation(getSite().getShell(), "Finished", "Execution finished");
				}
			});
			
			

		}

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

		private boolean handleProgressChange(WatchEvent<Path> event) throws IOException {
			if (!openReader(event))
				return false;

			//Read from .progress file
			final List<Tuple2<String, Status>> status = progressReader.readAllStatus();

			//Update UI
			getSite().getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					for (Tuple2<String, Status> classStatus : status) {
						txtConsole.append(classStatus._2() + "\n");
					}
				}
			});
			
			//check for termination
			for (Tuple2<String, Status> classStatus : status) {
				Status s = classStatus._2();
				if(s instanceof Shutdown)
					return true;
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
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private void showProgress(String progressFile) {

		}

	}

}
