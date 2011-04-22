package de.lmu.ifi.dbs.knowing.core.processing

import weka.core.Instances
import akka.actor.Actor

import de.lmu.ifi.dbs.knowing.core.graph.Node
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 18.04.2011
 */
trait TPresenter[T] extends Actor {

  val name: String

  def receive = {
    case UIFactoryEvent(factory, node) => 
      val parent = factory createContainer (node)
      createContainer(parent.asInstanceOf[T])
      self reply Ready
    case UIContainer(parent: T) => 
      log debug("UIContainer " + parent)
      createContainer(parent)
      self ! Ready
    case Results(instances) => buildPresentation(instances)
    case Query => self reply getContainerClass
    case Configure(_) => self reply Ready
    case Start => log debug("Running " + self.getActorClassName)
    case msg => log error("<----> " + msg)
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
   * <p>This method is used by the {@link IResultProcessor}s to add<br>
   * their content. The model can differ depending on the presenter configuration.</p>
   *
   * <p>It's very recommended to use {@link Queries} or {@link Results} methods to generate the<br>
   * initial model as they guarantee specific naming schemes and a wide range<br>
   * of general purpose datasets.</p>
   *
   * @param labels - the class labels
   * @return the internal model
   */
  def getModel(labels: Array[String]): Instances

  /**
   * <p>This method is used for determining if the<br>
   * UI system is able to handle this presenter.</p>
   *
   * @return the UI-Container class
   */
  def getContainerClass(): String

}