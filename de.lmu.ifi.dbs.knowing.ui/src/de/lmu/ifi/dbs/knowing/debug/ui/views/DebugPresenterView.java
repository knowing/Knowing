package de.lmu.ifi.dbs.knowing.debug.ui.views;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate;
import de.lmu.ifi.dbs.knowing.debug.presenter.DebugUIFactory;

public class DebugPresenterView extends ViewPart implements ILaunchesListener2, UncaughtExceptionHandler {
	public DebugPresenterView() {
	}

	public static final String	ID	= "de.lmu.ifi.dbs.knowing.debug.ui.views.DebugPresenterView";	//$NON-NLS-1$
	private Text				txtConsole;

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

		createActions();
		initializeToolBar();
		initializeMenu();
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void launchesRemoved(ILaunch[] launches) {
	}

	@Override
	public void launchesAdded(ILaunch[] launches) {
	}

	@Override
	public void launchesChanged(ILaunch[] launches) {
		try {
			// TODO check launch
			ILaunch launch = launches[0];

			// Only interested in DPU launch configurations
			ILaunchConfigurationType type = launch.getLaunchConfiguration().getType();
			
			if (!DPULaunchConfigurationDelegate.LAUNCH_TYPE_ID.equals(type.getIdentifier()))
				return;
			String executionPath = launch.getLaunchConfiguration().getAttribute(DPULaunchConfigurationDelegate.DPU_EXECUTION_PATH, "");
			ExecutionWatcherRunnable watcherRunnable = new ExecutionWatcherRunnable(executionPath);
			Thread thread = new Thread(watcherRunnable);
			thread.setName("ExecutionWatcherThread");
			thread.start();

		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void launchesTerminated(ILaunch[] launches) {
//		final Shell shell = getSite().getShell();
//		shell.getDisplay().asyncExec(new Runnable() {
//
//			@Override
//			public void run() {
//				MessageDialog.openInformation(shell, "Execution finished", "Finished");
//			}
//		});

	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void dispose() {
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
		super.dispose();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {

	}

	/**
	 * Watches for .log and .progress files in the execution path.
	 * 
	 * @author Nepomuk Seiler
	 * 
	 */
	private class ExecutionWatcherRunnable implements Runnable {

		boolean							terminated	= false;

		private final Path				executionPath;
		private AsynchronousFileChannel	progressChannel;
		private long					position	= 0;

		ExecutionWatcherRunnable(String executionPath) throws IOException {
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
						processEvent(event);
					}

					if (!watchKey.reset()) {
						terminated = true;
					}
				}
			} catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
			}

		}

		private void processEvent(WatchEvent<?> event) throws InterruptedException, ExecutionException, TimeoutException, IOException {
			WatchEvent.Kind<?> kind = event.kind();
			WatchEvent<Path> ev = (WatchEvent<Path>) event;
			Path filename = ev.context();
			if (filename.startsWith(".log")) {
				System.out.println("Log: " + filename.getFileName());
			} else if (filename.startsWith(DebugUIFactory.PROGRESS_FILENAME)) {
				if (!openFileChannel(event))
					return;

				final ByteBuffer buffer = ByteBuffer.allocate(512);
				Integer bytesRead = progressChannel.read(buffer, position).get(5, TimeUnit.SECONDS);
				if (bytesRead == -1)
					return;

				position += bytesRead;
				getSite().getShell().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (buffer.hasArray())
							txtConsole.append(new String(buffer.array()));
					}
				});
			}
		}

		private boolean openFileChannel(WatchEvent<?> event) throws IOException {
			if (progressChannel != null)
				return true;
			if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE) && progressChannel != null) {
				progressChannel.close();
				progressChannel = null;
				return false;
			}
			try {
				Path progressFile = this.executionPath.resolve(DebugUIFactory.PROGRESS_FILENAME);
				if (!Files.exists(progressFile))
					return false;
				progressChannel = AsynchronousFileChannel.open(progressFile);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

	}

}
