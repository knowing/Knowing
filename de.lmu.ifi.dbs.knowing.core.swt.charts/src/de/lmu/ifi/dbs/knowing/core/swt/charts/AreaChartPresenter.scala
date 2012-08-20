package de.lmu.ifi.dbs.knowing.core.swt.charts

import java.util.Properties
import java.text.{ DecimalFormat, SimpleDateFormat }
import de.lmu.ifi.dbs.knowing.presenter.{PresenterFactory, IAreaChartPresenter}
import org.eclipse.swt.widgets.{ Composite, Listener }
import org.jfree.chart.{ JFreeChart, ChartFactory }
import org.jfree.chart.plot.{ Plot, XYPlot, PlotOrientation }
import org.jfree.chart.renderer.xy.{ XYItemRenderer, XYLineAndShapeRenderer }
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.xy.XYDataset
import org.jfree.data.general.Dataset
import org.jfree.data.time.{ TimeSeriesCollection, TimeSeries, Second, Millisecond }

class AreaChartPresenter extends TimeSeriesPresenter with IAreaChartPresenter[Composite] {

  override protected def createChart(dataset: Dataset): JFreeChart = {
    ChartFactory.createXYAreaChart(
      name,
      "", "",
      dataset.asInstanceOf[XYDataset],
      PlotOrientation.VERTICAL,
      true, // legend
      true, // tool tips
      false // URLs
      )
  }

  override def configurePlot(plot: Plot) {
    val xyplot = plot.asInstanceOf[XYPlot]
    xyplot.setDomainCrosshairVisible(true)

    //Additional

    
    val domainAxis = new DateAxis("Time")
    domainAxis.setLowerMargin(0.0)
    domainAxis.setUpperMargin(0.0)
    xyplot.setDomainAxis(domainAxis)
    xyplot.setForegroundAlpha(0.5f)

    val renderer = xyplot.getRenderer.asInstanceOf[XYItemRenderer]
    renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
      StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
      new SimpleDateFormat("d-MMM-yyyy"),
      new DecimalFormat("#,##0.00")))

    //ChartUtilities.applyCurrentTheme(chart)
  }
}

class AreaChartPresenterFactory extends PresenterFactory(classOf[AreaChartPresenter], classOf[IAreaChartPresenter[Composite]])
  
