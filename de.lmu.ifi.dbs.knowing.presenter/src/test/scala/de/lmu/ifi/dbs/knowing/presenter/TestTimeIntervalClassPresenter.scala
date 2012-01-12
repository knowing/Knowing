package de.lmu.ifi.dbs.knowing.presenter

import java.util.{ Date, Properties }
import weka.core.{ Instance, Instances }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor

class TestTimeIntervalClassPresenter extends ITimeIntervalClassPresenter[Any] {

  val name = "Test TimeIntervalClassPresenter"
  
  /**
   * Add classes to the TimeIntervalChart
   */
  def buildCategories(classes: Array[String]) {
	  self.sender.get ! (classes)
  }

  /**
   *
   */
  def addInterval(clazz: String, from: Date, to: Date) {
	  self.sender.get ! (clazz, from, to)
  }

  def configure(properties: Properties) = {}
  
  def sync(parent: Any)(syncFun: => Unit) = syncFun

  def sync(parent: Any, runnable: Runnable) = runnable.run
  
  def createContainer(parent: Any) = {}

  def getParent(): Any = null
  
  def getContainerClass(): String = "Any"
}