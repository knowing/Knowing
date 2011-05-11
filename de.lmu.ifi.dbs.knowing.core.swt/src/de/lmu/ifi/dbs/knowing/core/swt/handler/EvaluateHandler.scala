package de.lmu.ifi.dbs.knowing.core.swt.handler

import org.eclipse.ui.PlatformUI
import org.eclipse.swt.widgets.Composite
import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.{ AbstractHandler, ExecutionEvent, ExecutionException }
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.knowing.core.factory.UIFactory;
import de.lmu.ifi.dbs.knowing.core.graph.GraphSupervisor;
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.swt.view.PresenterView;

import akka.actor.Actor.actorOf

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class EvaluateHandler extends AbstractHandler {

  def execute(event: ExecutionEvent): Object = {
    val pathname = openDPU(event)
    if (pathname == null || pathname.isEmpty())
      return null;
    val dpu = unmarshallDPU(pathname)
    EvaluateHandler.evaluate(dpu)
    null;
  }

  /**
   * @param pathname
   */
  def unmarshallDPU(pathname: String): DataProcessingUnit = {
    try {
      val context = JAXBContext.newInstance(classOf[DataProcessingUnit])
      val um = context.createUnmarshaller()
      um.unmarshal(new File(pathname)).asInstanceOf[DataProcessingUnit]
    } catch {
      case jaxb: JAXBException =>
        jaxb.printStackTrace
        null
      case e: Exception =>
        e.printStackTrace()
        null
    }
  }

  /**
   * @param event
   * @return
   */
  def openDPU(event: ExecutionEvent): String = {
    val dialog = new FileDialog(HandlerUtil.getActiveShell(event))
    dialog.setFilterExtensions(Array("*.xml", "*.dpu"))
    dialog.setFilterNames(Array("XML DataProcessingUnit", "DPU DataProcessingUnit"))
    dialog.open()
  }

}

object EvaluateHandler {

  def evaluate(dpu: DataProcessingUnit) {
    try {
      val view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PresenterView.ID)
      val pView = view.asInstanceOf[PresenterView]
      val supervisor = actorOf(new GraphSupervisor(dpu, pView.uifactory)).start
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