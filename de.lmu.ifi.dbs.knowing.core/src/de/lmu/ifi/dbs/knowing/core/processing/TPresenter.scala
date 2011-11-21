package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.{ Actor, ActorRef }
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import scala.collection.mutable.{ Set => MSet }
import scala.collection.JavaConversions._
import weka.core.{ Instances, Instance, Attribute }

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 18.04.2011
 */
trait TPresenter[T] extends TProcessor {

  /** Name of this component */
  val name: String

  override final protected def customReceive: Receive = presenterReceive orElse defaultReceive
  
  protected def presenterReceive: Receive = defaultReceive

  /**
   * Handles UI specific events
   */
  private def defaultReceive: Receive = {
    case UIFactoryEvent(factory, node) =>
      statusChanged(Running())
      val parent = factory createContainer (node)
      createContainer(parent.asInstanceOf[T])
      if (self.getSender.isDefined) self reply Ready()
      statusChanged(Ready())
    case QueryResults(instances, _) => buildPresentation(instances)
  }

  /**
   * 
   */
  def build(instances: Instances) {
    statusChanged(Running())
    buildPresentation(instances)
    statusChanged(UpdateUI())
    statusChanged(Ready())
  }

  /**
   * Creates the initial UI-Container presenting the data
   *
   * @param parent
   * @return
   */
  def createContainer(parent: T)

  /**
   * @param instances
   */
  def buildPresentation(instances: Instances)

  /**
   * <p>This method is used for determining if the<br>
   * UI system is able to handle this presenter.</p>
   *
   * @return the UI-Container class
   */
  def getContainerClass(): String

  /* == Doesn't needed by TPresenter == */
  def query(instance: Instance): Instances = ResultsUtil.emptyResult

  def result(results: Instances, query: Instance) = {}

}