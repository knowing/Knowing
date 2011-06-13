package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import weka.core.{ Instances, Attribute }
import akka.actor.{ Actor, ActorRef }
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.graph.Node
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import scala.collection.mutable.{ Set => MSet }
import scala.collection.JavaConversions._

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 18.04.2011
 */
trait TPresenter[T] extends Actor with TSender with TConfigurable {

  val name: String

  def receive: Receive = customReceive orElse defaultReceive

  /**
   * <p>Override for special behaviour</p>
   */
  protected def customReceive: Receive = defaultReceive

  /**
   * <p>Default behaviour</p>
   */
  private def defaultReceive: Receive = {
    case UIFactoryEvent(factory, node) =>
      val parent = factory createContainer (node)
      createContainer(parent.asInstanceOf[T])
      if(self.getSender.isDefined)
    	  self reply Ready
    case Configure(properties) =>
      configure(properties)
      self reply Ready
    case Results(instances) =>
      buildPresentation(instances)
      sendEvent(new UpdateUI)
    case QueryResults(instances, _) =>
      buildPresentation(instances)
      sendEvent(new UpdateUI)
    case Register(actor) => addListener(actor)
    case Query => self reply getContainerClass
    case Start => debug(this, ("Running " + self.getActorClassName))
    case msg => warning(this, "<----> " + msg)
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

  /**
   *  <p>Checks the dataset for class attribute in this order
   *  <li> {@link Instances#classIndex()} -> if >= 0 returns index</li>
   *  <li> returns index of the attribute named "class" if exists</li>
   *  <li> returns index of the first nominal attribute</li>
   *  </p>
   *
   * @param dataset
   * @return class attribute index or -1
   */
  def guessAndSetClassLabel(dataset: Instances): Int = {
    val index = dataset.classIndex
    index match {
      case -1 =>
        val cIndex = guessClassLabel(dataset)
        dataset.setClassIndex(cIndex)
        cIndex
      case x => x
    }
  }

  private def guessClassLabel(dataset: Instances): Int = {
    val classAttribute = dataset.attribute("class")
    if (classAttribute != null)
      return classAttribute.index

    //Maybe this is not the feastes way to do
    val attributes = dataset.enumerateAttributes().toList
    val nominal = attributes filter (a => a.asInstanceOf[Attribute].isNominal)
    nominal.headOption match {
      case Some(x) => x.asInstanceOf[Int]
      case None => -1

    }
  }

}