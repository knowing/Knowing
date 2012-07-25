package de.lmu.ifi.dbs.knowing.core.swt.charts

import java.util.{Date,Properties}
import de.lmu.ifi.dbs.knowing.core.swt.SWTPresenter
import de.lmu.ifi.dbs.knowing.core.swt.listener.SWTListener
import de.lmu.ifi.dbs.knowing.core.swt.charts.handler.{ TChartHandler, ZoomChartHandler, ChartHandlerAdapter }
import de.lmu.ifi.dbs.knowing.core.swt.charts.events._
import org.jfree.data.general.Dataset
import org.jfree.chart.JFreeChart
import org.jfree.chart.{ ChartMouseListener, ChartMouseEvent }
import org.jfree.chart.event.{ ChartChangeListener, ChartChangeEvent, ChartProgressListener, ChartProgressEvent }
import org.jfree.chart.plot.{ Plot, Zoomable }
import org.jfree.data.time.{ RegularTimePeriod, Millisecond, Second, Minute, Hour }
import org.jfree.experimental.chart.swt.ChartComposite
import org.eclipse.swt.widgets.{ Composite, Listener, Event, Widget }
import org.eclipse.swt.SWT
import org.eclipse.swt.events.{ MouseEvent, MouseWheelListener }
import weka.core.Instances


/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 24.04.2011
 *
 */
abstract class AbstractChartPresenter(val name: String) extends SWTPresenter {

  import AbstractChartPresenter._

  private var chart: JFreeChart = _

  protected var chartComposite: ChartComposite = _
  protected var plot: Plot = _
  protected var dataset: Dataset = createDataset
  protected var timeUnit = TIME_UNIT_MILLISECOND

  private var progressListener: List[ChartProgressListener] = Nil
  private var changeListener: List[ChartChangeListener] = Nil

  override def presenterReceive = {
    case ChartProgressListenerRegister(l) => addProgressListener(l)
    case ChartChangeListenerRegister(l) => addChangeListener(l)
    case SWTListener(typ, listener) => super.customReceive { SWTListener(typ, listener) }
  }

  def configure(properties: Properties) {
    timeUnit = properties.getProperty(TIME_UNIT, TIME_UNIT_MILLISECOND)
  }

  /**
   *
   */
  def createContainer(parent: Composite) = {
    chart = createChart(dataset)
    configurePlot(chart.getPlot)
    chartComposite = new ChartComposite(parent, SWT.NONE, chart, true)
    configureChartHandler(chartComposite)
    plot = chart.getPlot

    progressListener foreach (l => chart.addProgressListener(l))
    progressListener = Nil
    changeListener foreach (l => chart.addChangeListener(l))
    changeListener = Nil
    setParent(parent)
  }

  def updateChart() {
    if (chart != null)
      chart.fireChartChanged
  }

  def addListener(typ: Int, listener: Listener) {
    typ match {
      case SWT.MouseDown => chartComposite.addChartMouseListener(new SWTMouseListenerProxy(typ, listener))
      case SWT.MouseMove => chartComposite.addChartMouseListener(new SWTMouseListenerProxy(typ, listener))
      case _ => chartComposite.addListener(typ, listener)
    }
  }

  private def addProgressListener(l: ChartProgressListener) {
    if (chart != null)
      chart.addProgressListener(l)
    else
      progressListener ::= l
  }

  private def addChangeListener(l: ChartChangeListener) {
    if (chart != null)
      chart.addChangeListener(l)
    else
      changeListener ::= l
  }

  /**
   * Override this method for special behaviour. It's called
   * after the dataset was created;
   * @param plot
   */
  protected def configurePlot(plot: Plot) {}

  /**
   * @param chartComposite to which listeners can be added
   */
  protected def configureChartHandler(parent: ChartComposite) = {
    val handler = new ChartHandlerAdapter with ZoomChartHandler {
      val chartComposite = parent
    }
    chartComposite.addMouseWheelListener(handler)
    chartComposite.addChartMouseListener(handler)
    chart.addChangeListener(handler)
    chart.addProgressListener(handler)
  }

  /**
   * Is called once to create the JFreeChart
   */
  protected def createChart(dataset: Dataset): JFreeChart

  /**
   * Is called once to create the Dataset
   */
  protected def createDataset: Dataset

  def newRegularTimePeriod(date: Date): RegularTimePeriod = timeUnit match {
    case TIME_UNIT_MILLISECOND => new Millisecond(date)
    case TIME_UNIT_SECOND => new Second(date)
    case TIME_UNIT_MINUTE => new Minute(date)
    case TIME_UNIT_HOUR => new Hour(date)
  }

}

class SWTMouseListenerProxy(typ: Int, listener: Listener) extends ChartMouseListener {

  def chartMouseClicked(event: ChartMouseEvent) {

    if (typ != SWT.MouseDown)
      return
    listener.handleEvent(convert(event))
  }

  def chartMouseMoved(event: ChartMouseEvent) {
    if (typ != SWT.MouseMove)
      return
    listener.handleEvent(convert(event))
  }

  private def convert(event: ChartMouseEvent): Event = {
    val e = new Event
    val trigger = event.getTrigger
    e.x = trigger.getX
    e.y = trigger.getY
    e.button = trigger.getButton
    e.count = trigger.getClickCount
    e.time = trigger.getWhen.toInt
    e.data = event.getChart
    e
  }
}

object AbstractChartPresenter {

  val TIME_UNIT = "jfreechart.time.unit"
  val TIME_UNIT_MILLISECOND = "millisecond"
  val TIME_UNIT_SECOND = "second"
  val TIME_UNIT_MINUTE = "minute"
  val TIME_UNIT_HOUR = "hour"
}

