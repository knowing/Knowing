package de.lmu.ifi.dbs.knowing.presenter

import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import java.util.Date

trait ITimeSeriesPresenter[T] extends TPresenter[T] {

  /**
   * Add classes to the TimeIntervalChart
   */
  def buildCategories(classes: Array[String])

  /**
   *
   */
  def addPoint(clazz: String, from: Date, to: Date)

  /**
   * Update the chart. Is called after
   * adding all time.
   */
  def update()

}