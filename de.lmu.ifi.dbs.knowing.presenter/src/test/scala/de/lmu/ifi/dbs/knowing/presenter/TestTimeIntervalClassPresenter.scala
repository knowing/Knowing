package de.lmu.ifi.dbs.knowing.presenter

import java.util.{ Date, Properties }
import weka.core.{ Instance, Instances }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.test.TestPresenter

class TestTimeIntervalClassPresenter extends ITimeIntervalClassPresenter[Any] with TestPresenter {

  /**
   * Add classes to the TimeIntervalChart
   */
  def buildCategories(classes: Array[String]) {
	  self.sender.get ! (classes.toList)
  }

  /**
   *
   */
  def addInterval(clazz: String, from: Date, to: Date) {
	  self.sender.get ! (clazz, from.getTime, to.getTime)
  }
  
  def update() = {}

}