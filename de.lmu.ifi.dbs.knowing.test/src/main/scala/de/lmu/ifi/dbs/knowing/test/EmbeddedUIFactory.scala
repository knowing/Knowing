package de.lmu.ifi.dbs.knowing.test

import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.factory.{ ProcessorFactory, UIFactory }
import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import weka.core.Instances
import scala.collection.mutable.{ HashMap, ArrayBuffer }
import java.util.Properties
import java.util.concurrent.SynchronousQueue

/**
 * @author Nepomuk Seiler
 * @version 0.2
 */
class EmbeddedUIFactory extends UIFactory[IEmbeddedUIComponent] {

  val containers = HashMap[String, IEmbeddedUIComponent]()
  val rendevouz = new SynchronousQueue[HashMap[String, IEmbeddedUIComponent]]

  private var supervisor: ActorRef = _

  /**
   * Waits until the process finishes and return the
   * Map with all presenters wrapped inside a IEmbeddedUIComponent
   * 
   * @return map with [Node.id -> IEmbeddedUIComponent]
   */
  def await(): HashMap[String, IEmbeddedUIComponent] = rendevouz.take

  def createContainer(node: INode): IEmbeddedUIComponent = {
    val container = new EmbeddedUIComponent
    containers += (node.getId.getContent -> container)
    container
  }

  def update(actor: ActorRef, status: Status) = {
    status match {
      case ExceptionEvent(e, details) => e.printStackTrace()
      case Shutdown() => rendevouz.put(containers)
      case _ => //println("Update " + status + " from " + actor.actorClassName)
    }
  }

  def setSupervisor(supervisor: ActorRef) = this.supervisor = supervisor

  def getId(): String = classOf[EmbeddedUIFactory].getName

}

/**
 * Used to test the datamining process without an UI System.
 * EmbeddedUIComponents simple store the instances.
 *
 * @author Nepomuk Seiler
 * @version 0.2
 */
trait IEmbeddedUIComponent {

  /**
   * @param inst - Instances which should be displayed
   */
  def addInstances(inst: Instances)

  /**
   * Retrieve all stored Instances
   */
  def getInstances(): Array[Instances]
}

/**
 * @author Nepomuk Seiler
 * @version 0.2
 */
class EmbeddedUIComponent extends IEmbeddedUIComponent {

  private val instancesBuffer = ArrayBuffer[Instances]()

  def addInstances(inst: Instances) = instancesBuffer += inst

  def getInstances(): Array[Instances] = instancesBuffer.toArray
}

class EmbeddedUIComponentPresenter extends TPresenter[IEmbeddedUIComponent] {

  val name = "Embedded UI Component Presenter"

  protected var parent: IEmbeddedUIComponent = _

  def sync(parent: IEmbeddedUIComponent)(syncFun: => Unit) = syncFun
  
  def sync(parent: IEmbeddedUIComponent, runnable: Runnable) = runnable.run()

  /**
   * Creates the initial UI-Container presenting the data
   *
   * @param parent
   * @return
   */
  def createContainer(parent: IEmbeddedUIComponent) = this.parent = parent

  /**
   * @param instances
   */
  def buildPresentation(instances: Instances) = parent.addInstances(instances)

  /**
   * <p>This method is used for determining if the<br>
   * UI system is able to handle this presenter.</p>
   *
   * @return the UI-Container class
   */
  def getContainerClass(): String = classOf[IEmbeddedUIComponent].getName
  
  def getParent(): IEmbeddedUIComponent = parent

  def configure(properties: Properties) = {}
}

class EmbeddedUIComponentPresenterFactory extends ProcessorFactory(classOf[EmbeddedUIComponentPresenter])