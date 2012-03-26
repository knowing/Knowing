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

import de.lmu.ifi.dbs.knowing.core.events.UIEvent
import org.eclipse.swt.widgets.Listener
import org.eclipse.jface.viewers.ISelectionChangedListener

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 11.06.2011
 *
 */
case class SWTListener(typ: Int, listener: Listener) extends UIEvent

case class SelectionChangedListener(listener:ISelectionChangedListener) extends UIEvent