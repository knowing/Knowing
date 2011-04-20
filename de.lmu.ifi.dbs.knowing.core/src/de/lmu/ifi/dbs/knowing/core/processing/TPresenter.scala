package de.lmu.ifi.dbs.knowing.core.processing

import weka.core.Instances
import akka.actor.Actor

import de.lmu.ifi.dbs.knowing.core.events._

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 18.04.2011
 */
trait TPresenter[T] extends Actor {
  
  val name:String
  
  def receive = {
    case UIContainer(parent:T) => self reply createContainer(parent)
    case Results(instances) => buildPresentation(instances)
    case Query => self reply getContainerClass
    case _ => log error ("unkown message")
  }

  /**
   * Creates the initial UI-Container presenting the data
   *
   * @param parent
   * @return
   */
  def createContainer(parent: T): AnyRef
  
  /**
   * @param instances
   */
  def buildPresentation(instances:Instances)
  
  def getContainerClass():String 

}