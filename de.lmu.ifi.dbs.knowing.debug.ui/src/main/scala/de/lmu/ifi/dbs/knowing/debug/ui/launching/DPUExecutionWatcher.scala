package de.lmu.ifi.dbs.knowing.debug.ui.launching

import org.eclipse.debug.core.ILaunchesListener2
import org.eclipse.debug.core.ILaunch
import org.eclipse.debug.core.DebugException
import org.eclipse.core.commands.ExecutionException
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IStatus
import org.eclipse.jface.dialogs.ErrorDialog
import org.eclipse.ui.PartInitException
import org.eclipse.jdi.TimeoutException
import de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate
import de.lmu.ifi.dbs.knowing.debug.presenter.ProgressReader
import de.lmu.ifi.dbs.knowing.debug.presenter.DebugUIFactory
import de.lmu.ifi.dbs.knowing.debug.ui.views.DebugPresenterView
import de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator
import de.lmu.ifi.dbs.knowing.core.events.Shutdown
import java.nio.file._
import java.nio.file.StandardWatchEventKinds._
import java.nio.charset.Charset
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.lang.Thread.UncaughtExceptionHandler
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

class DPUExecutionWatcher(view: DebugPresenterView) extends ILaunchesListener2 with UncaughtExceptionHandler {
  require(view != null, "DebugPresenterView must be not null")

  private val log = LoggerFactory.getLogger("DPUExecutionWatcher")
  var currentLaunch: ILaunch = null;

  // Inform user -> show DebugPresenterView
  view.getSite().getShell().getDisplay().asyncExec(new Runnable() {

    def run() {
      try {
        view.getSite().getWorkbenchWindow().getActivePage().showView(DebugPresenterView.ID);
      } catch {
        case e: PartInitException =>
          ErrorDialog.openError(view.getSite().getShell(), "Error while opening DebugPresenterView", e.getMessage(),
            new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
      }
    }
  });

  def uncaughtException(t: Thread, e: Throwable) {

  }

  /* ================================= */
  /* ======== Launch Handling ======== */
  /* ================================= */

  def launchesTerminated(launches: Array[ILaunch]) {

  }

  def launchesAdded(launches: Array[ILaunch]) {
    try {
      if (launches.isEmpty) return

      val launch = launches(0)

      // Only interested in DPU launch configurations
      val launchType = launch.getLaunchConfiguration().getType();

      if (!DPULaunchConfigurationDelegate.LAUNCH_TYPE_ID.equals(launchType.getIdentifier()))
        return ;

      if (currentLaunch != null) {
        /*
        getSite().getShell().getDisplay().asyncExec(new Runnable() {

          def run() {
            MessageDialog.openError(getSite().getShell(), "Another DPU is currently running",
              "Stop the other instance running.");
          }
        });
        */

        // Ask what to do. Stop current or new launch
        launch.terminate();
        return ;
      }

      val executionPath = launch.getLaunchConfiguration().getAttribute(DPULaunchConfigurationDelegate.DPU_EXECUTION_PATH, "");
      val watcherRunnable = new ExecutionWatcherRunnable(Paths.get(executionPath), launch, view)
      val thread = new Thread(watcherRunnable)
      thread.setName("ExecutionWatcherThread")
      thread.start()
      currentLaunch = launch

    } catch {
      case e: CoreException =>
        log.error("Error on adding launch", e);
        ErrorDialog.openError(view.getSite().getShell(), "Error while launching DPU", e.getMessage(), new org.eclipse.core.runtime.Status(
          IStatus.ERROR, Activator.PLUGIN_ID, null, e))
      case e: IOException =>
        log.error("Error on adding launch", e);
        ErrorDialog.openError(view.getSite().getShell(), "Error while launching DPU", e.getMessage(), new org.eclipse.core.runtime.Status(
          IStatus.ERROR, Activator.PLUGIN_ID, null, e))
    }
  }

  def launchesRemoved(launches: Array[ILaunch]) {

  }

  def launchesChanged(launches: Array[ILaunch]) {

  }
}

class ExecutionWatcherRunnable(executionPath: Path, launch: ILaunch, view: DebugPresenterView) extends Runnable {

  private var terminated = false
  private var progressReader: ProgressReader = null;
  private val log = LoggerFactory.getLogger("ExecutionWatcherRunnable")

  def run() {
    log.info("Watching execution path [" + executionPath + "]")
    try {
      val watchService = FileSystems.getDefault().newWatchService();
      executionPath.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
      while (!terminated) {
        // TODO make the timeout configurable
        val watchKey = watchService.poll(5, TimeUnit.SECONDS)
        if (watchKey == null) {
          // inform user
          return
        }
        val events = watchKey.pollEvents
        for (event <- events) {
          terminated = processEvent(event)
        }

        if (!watchKey.reset()) {
          terminated = true
        }
      }

      log.info("Terminating launch")
      launch.terminate()
    } catch {
      case e: IOException => log.error("Error on waiting for progress", e);
      case e: InterruptedException =>
      case e: ExecutionException =>
      case e: TimeoutException =>
      case e: DebugException => log.error("Error on termination", e);
    }

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
  def processEvent(event: WatchEvent[_]): Boolean = {
    val kind = event.kind
    val ev = event.asInstanceOf[WatchEvent[Path]]
    val filename = ev.context
    filename.toString match {
      case ".log" => handleLogChanged(ev); false
      case ".progress" => handleProgressChange(ev)
      case _ => false
    }
  }

  def handleLogChanged(event: WatchEvent[Path]) {
    val filename = event.context
  }

  /**
   *
   * @param event
   * @return true if progress terminated
   * @throws IOException
   */
  private def handleProgressChange(event: WatchEvent[Path]): Boolean = {
    if (!openReader(event))
      return false;

    // Read from .progress file. List<Tuple2<String, Status>>
    val status = progressReader.readAllStatus

    // Update UI
    view.getSite().getShell().getDisplay().asyncExec(new Runnable() {

      def run() {
        for (val classStatus <- status) {
          // TODO update dialog here
        }
      }
    });

    // check for termination
    status.find(_._2.isInstanceOf[Shutdown]).isDefined
  }

  private def openReader(event: WatchEvent[Path]): Boolean = {
    if (progressReader != null)
      return true;
    if (event.kind.equals(ENTRY_DELETE) && progressReader != null) {
      progressReader.close();
      progressReader = null;
      return false;
    }
    try {
      val progressFile = this.executionPath.resolve(DebugUIFactory.PROGRESS_FILENAME);
      if (!Files.exists(progressFile))
        return false;
      progressReader = new ProgressReader(Files.newBufferedReader(progressFile, Charset.defaultCharset()));
    } catch {

      case e: IOException =>
        log.error("IOError on reading progress file", e);
        return false;
    }
    return true;
  }

  private def showProgress(progressFile: String) {

  }

}
