package de.lmu.ifi.dbs.knowing.core.swt.charts

import java.util.Properties
import java.awt.{ Color, Font }
import org.eclipse.swt.widgets.Composite
import org.jfree.util.Rotation
import org.jfree.chart.plot.{ Plot, PiePlot3D }
import org.jfree.chart.{ ChartFactory, JFreeChart }
import org.jfree.data.general.{ Dataset, PieDataset, DefaultPieDataset }

import de.lmu.ifi.dbs.knowing.presenter.{PresenterFactory,IPieChartPresenter}
import scala.collection.mutable.HashMap

class PieChartPresenter extends AbstractChartPresenter("Pie Chart Presenter") with IPieChartPresenter[Composite] {

  /** label -> probability || label -> count */
  val internalModel = new HashMap[String, Double]

  /* ========================== */
  /* == Dataset manipulation == */
  /* ========================== */

  def addOrUpdatePiece(label: String, value: Double) = internalModel.get(label) match {
    case None => internalModel += (label -> value)
    case Some(e) => internalModel += (label -> (e + value))
  }

  def update() = {
    updateDataset()
    updateChart()
  }

  private def updateDataset() {
    val pieset = dataset.asInstanceOf[DefaultPieDataset]
    pieset clear ()
    internalModel foreach { case (key, propability) => pieset.setValue(key, propability) }
  }

  /* ========================== */
  /* ===== Chart creation ===== */
  /* ========================== */

  protected def createChart(dataset: Dataset): JFreeChart = ChartFactory.createPieChart3D("Pie Chart", dataset.asInstanceOf[PieDataset], true, false, false)

  protected def createDataset(): Dataset = new DefaultPieDataset

  override protected def configurePlot(plot: Plot) = {
    val plot3D = plot.asInstanceOf[PiePlot3D]
    plot3D.setLabelFont(new Font("SansSerif", Font.PLAIN, 12))
    plot3D.setNoDataMessage("No data available")
    plot3D.setCircular(false)
    plot3D.setLabelGap(0.02)

    plot3D.setStartAngle(270)
    plot3D.setDirection(Rotation.ANTICLOCKWISE)
    plot3D.setForegroundAlpha(0.60f)
    plot3D.setBackgroundPaint(Color.white)
  }
}

class PieChartPresenterFactory extends PresenterFactory(classOf[PieChartPresenter], classOf[IPieChartPresenter[Composite]])
