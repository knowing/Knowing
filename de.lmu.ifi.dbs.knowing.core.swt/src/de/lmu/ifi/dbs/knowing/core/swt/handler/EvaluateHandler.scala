/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
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
import de.lmu.ifi.dbs.knowing.core.processing.DPUExecutor
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
