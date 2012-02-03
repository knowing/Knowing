package de.lmu.ifi.dbs.knowing.presenter

import java.util.Date
import weka.core.{ Instances, Attribute }

/**
 *
 */
class TestTimeSeriesPresenter extends ITimeSeriesPresenter[Any] with TestPresenter {

  def buildSeries(series: Array[Attribute]) = {
    self.sender.get ! (series.toList)
  }

  def addPoint(date: Date, values: Array[Double]) = {
    self.sender.get ! date
    for(v <- values) {
      self.sender.get ! v
    }
  }

  def update() = {
    self.sender.get ! "update"
  }

}