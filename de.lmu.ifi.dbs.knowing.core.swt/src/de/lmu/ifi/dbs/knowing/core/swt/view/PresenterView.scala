package de.lmu.ifi.dbs.knowing.core.swt.view

import akka.actor.{ ActorRef, Actor, TypedActor }
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import java.util.concurrent.{ ArrayBlockingQueue, SynchronousQueue }
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.custom.{ CTabFolder, CTabItem }
import org.eclipse.ui.part.ViewPart
import org.eclipse.ui.IPropertyListener
import org.eclipse.core.runtime.{ Status => JobStatus, IStatus, IProgressMonitor }
import de.lmu.ifi.dbs.knowing.core.swt.dialog.ProgressDialog
import de.lmu.ifi.dbs.knowing.core.swt.factory.TabUIFactory
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.model._

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 22.04.2011
 *
 */
class PresenterView extends ViewPart {

  var uifactory: UIFactory[Composite] = _
  val rendevouz = new SynchronousQueue[Composite]

  /**
   * Creates UIFactory.
   */
  def createPartControl(parent: Composite) = {
    uifactory = TypedActor.newInstance(classOf[UIFactory[Composite]], new TabUIFactory(parent))
  }

  def setFocus() = {}

}

object PresenterView { val ID = "de.lmu.ifi.dbs.knowing.core.swt.presenterView" }

