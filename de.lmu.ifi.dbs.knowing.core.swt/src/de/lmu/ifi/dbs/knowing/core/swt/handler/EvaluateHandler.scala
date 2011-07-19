package de.lmu.ifi.dbs.knowing.core.swt.handler

import org.eclipse.ui.PlatformUI
import org.eclipse.swt.widgets.Composite
import java.io.File
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.Unmarshaller
import org.eclipse.core.commands.{ AbstractHandler, ExecutionEvent, ExecutionException }
import org.eclipse.swt.widgets.FileDialog
import org.eclipse.ui.IViewPart
import org.eclipse.ui.PartInitException
import org.eclipse.ui.handlers.HandlerUtil
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.graph.GraphSupervisor
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.swt.view.PresenterView
import de.lmu.ifi.dbs.knowing.core.swt.wizard.SelectDPUWizard
import akka.actor.Actor.actorOf
import java.net.{ URL, URI }
import org.eclipse.jface.wizard.WizardDialog

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class EvaluateHandler extends AbstractHandler {

  def execute(event: ExecutionEvent): Object = {
    val wizard = new SelectDPUWizard
    val dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard)
    val ret = dialog.open
    //    val pathname = openDPU(event)
    //    if (pathname == null || pathname.isEmpty())
    //      return null;
    //    val dpu = unmarshallDPU(pathname)
    //    EvaluateHandler.evaluate(dpu, getDirectory(pathname))
    null;
  }

}

object EvaluateHandler {

  def evaluate(dpu: DataProcessingUnit, dpuPath: URI) {
    try {
      val view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PresenterView.ID)
      val pView = view.asInstanceOf[PresenterView]
      pView.clearTabs
      val supervisor = actorOf(new GraphSupervisor(dpu, pView.uifactory, dpuPath)).start
      supervisor ! Start
      null
    } catch {
      case pEx: PartInitException =>
        pEx.printStackTrace
        null
      case e: Exception =>
        e.printStackTrace
        null
    }
  }

}