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

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import de.lmu.ifi.dbs.knowing.core.swt.internal.Activator.directoryService
import org.eclipse.ui.dialogs.ElementListSelectionDialog
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.jface.viewers.LabelProvider
import de.lmu.ifi.dbs.knowing.core.factory.TFactory

class FactoryDirectoryHandler extends AbstractHandler {

  def execute(event: ExecutionEvent): Object = {
    val labelProvider = new LabelProvider {
      override def getText(element: Object): String = element.asInstanceOf[TFactory].id
    }
    val dialog = new ElementListSelectionDialog(HandlerUtil.getActiveShell(event), labelProvider)
    dialog.setElements(directoryService.getFactories map (_.asInstanceOf[Object]))
    dialog.setMessage("Show registered factories")
    dialog.open
    null
  }

}