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
package de.lmu.ifi.dbs.knowing.core.swt.charts.handler

import org.eclipse.swt.events.MouseEvent
import org.jfree.chart.event.{ ChartProgressEvent, ChartChangeEvent }
import org.jfree.chart.ChartMouseEvent

/**
 * <p> Adapter class. Should be extended via traits </p>
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 05.06.2011
 *
 */
class ChartHandlerAdapter extends TChartHandler {

  /* =========================== */
  /* == ChartProgressListener == */
  /* =========================== */
  
  def chartProgress(e: ChartProgressEvent) = {}
  
  /* =========================== */
  /* === ChartChangeListener === */
  /* =========================== */

  def chartChanged(e: ChartChangeEvent) = {}

  /* =========================== */
  /* ==== ChartMouseListener === */
  /* =========================== */
  
  def chartMouseClicked(e: ChartMouseEvent) = {}

  def chartMouseMoved(e: ChartMouseEvent) = {}

  /* =========================== */
  /* == SWT MouseWheelListener = */
  /* =========================== */
  
  def mouseScrolled(e: MouseEvent) = {}

}