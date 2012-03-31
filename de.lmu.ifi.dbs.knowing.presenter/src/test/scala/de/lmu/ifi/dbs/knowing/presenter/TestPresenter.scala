package de.lmu.ifi.dbs.knowing.presenter

import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import java.util.Properties
import weka.core.Instances

/**
 * This class is used to test @link{TPresenter} processors.
 * It generates no container and isn't using any UI system.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait TestPresenter extends TPresenter[Any] {

  val name = getClass.getName
  
  /**
   * just executes syncFun
   * @param parent - can be anything
   * @param syncFun - actual function
   */
  def sync(parent: Any)(syncFun: => Unit) = syncFun

  /**
   * just executes syncFun
   * @param parent - can be anything
   * @param runnable - actual function
   */
  def sync(parent: Any, runnable: Runnable) = runnable.run

  /**
   * does nothing
   */
  def createContainer(parent: Any) = {}

  /**
   * @return "Any"
   */
  def getContainerClass(): String = "Any"

  /**
   * @return null
   */
  def getParent(): Any = { null }

  /**
   * does nothing
   */
  def configure(properties: Properties) = {}

}