package de.lmu.ifi.dbs.knowing.core.swt.charts.handler

import org.eclipse.swt.events.{MouseWheelListener, MouseEvent }
import org.jfree.chart.ChartMouseListener
import org.jfree.chart.event.{ ChartProgressListener, ChartChangeListener }
import org.jfree.chart.plot.{Plot , Zoomable }
import org.jfree.experimental.chart.swt.ChartComposite
import java.awt.Point


/**
 * @author Nepomuk Seiler
 * @version 1.0
 * @since 05.06.2011
 *
 */
trait TChartHandler extends ChartMouseListener with MouseWheelListener with ChartChangeListener with ChartProgressListener

/**
 * @author Nepomuk Seiler
 * 
 * Original Author:  David Gilbert (for Object Refinery Limited)
 * Contributor(s):   Ulrich Voigt - patch 2686040
 * 
 * @version 1.0
 * @since 05.06.2011
 * @see http://chart4me.googlecode.com/svn-history/r5/trunk/chart4me/jfreechart/org/jfree/chart/MouseWheelHandler.java
 */
trait ZoomChartHandler { this: TChartHandler =>

  //This must be defined in the extending class
  val chartComposite: ChartComposite
  
  var zoomFactor = 0.10
  
  override def mouseScrolled(e: MouseEvent) {
    val plot = chartComposite.getChart.getPlot
    if (plot.isInstanceOf[Zoomable]) {
      val zoomable = plot.asInstanceOf[Zoomable]
      handleZoomable(zoomable, e);
    }
  }

  /**
   * Handle the case where a plot implements the {@link Zoomable} interface.
   *
   * @param zoomable  the zoomable plot.
   * @param e  the mouse wheel event.
   * @see http://chart4me.googlecode.com/svn-history/r5/trunk/chart4me/jfreechart/org/jfree/chart/MouseWheelHandler.java
   */
  private def handleZoomable(zoomable: Zoomable, e: MouseEvent) {
    val plot = zoomable.asInstanceOf[Plot]
    val info = chartComposite.getChartRenderingInfo
    val pinfo = info.getPlotInfo
    val p = chartComposite.translateScreenToJava2D(new Point(e.x, e.y))

    if (!pinfo.getDataArea().contains(p)) {
      return
    }
    val clicks = e.count
    var direction = 0
    if (clicks < 0) {
      direction = 1
    } else if (clicks > 0) {
      direction = -1
    }

    val old = plot.isNotify

    // do not notify while zooming each axis
    plot.setNotify(false)
    val increment = 1.0 + zoomFactor
    if (direction > 0) {
      zoomable.zoomDomainAxes(increment, pinfo, p, true)
      zoomable.zoomRangeAxes(increment, pinfo, p, true)
    } else if (direction < 0) {
      zoomable.zoomDomainAxes(1.0 / increment, pinfo, p, true)
      zoomable.zoomRangeAxes(1.0 / increment, pinfo, p, true)
    }
    // set the old notify status
    plot.setNotify(old)
  }
}