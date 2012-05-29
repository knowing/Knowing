package de.lmu.ifi.dbs.knowing.presenter

import java.util.Date
import weka.core.{ Instances, Attribute }

/**
 *
 */
class TestTimeSeriesPresenter extends ITimeSeriesPresenter[Any] with TestPresenter {

  def buildSeries(series: Array[Attribute]) = {
    sender ! (series.toList)
  }

  def addPoint(date: Date, values: Array[Double]) = {
    sender ! date
    for(v <- values) {
      sender ! v
    }
  }

  def update() = {
    sender ! "update"
  }

}