package de.lmu.ifi.dbs.knowing.core.swt.handler

import java.net.URI
import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.jface.wizard.WizardDialog
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.ui.IViewPart
import org.eclipse.ui.PartInitException
import org.eclipse.ui.PlatformUI
import akka.actor.Actor.actorOf
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.graph.GraphSupervisor
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.swt.internal.Activator
import de.lmu.ifi.dbs.knowing.core.swt.view.PresenterView
import de.lmu.ifi.dbs.knowing.core.swt.wizard.SelectDPUWizard
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit

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
    null
  }

}

object EvaluateHandler {

  def evaluate(dpu: IDataProcessingUnit, dpuPath: URI): ActorRef = {
    try {
      val view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PresenterView.ID)
      val pView = view.asInstanceOf[PresenterView]
      pView.clearTabs
      val supervisor = actorOf(new GraphSupervisor(dpu, pView.uifactory, dpuPath, Activator.directoryService)).start
      supervisor ! Start
      supervisor
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