package de.lmu.ifi.dbs.knowing.core.processing

import java.io.IOException
import java.util.Properties
import weka.core.Instances
import de.lmu.ifi.dbs.knowing.core.events._
import akka.actor.Actor

trait TLoader extends Actor with TSender {

  def receive = {
    case Register(actor) => addListener(actor)
    case Configure(p) => 
      configure(p)
      self reply Ready
    case Start =>
      log debug ("Loader " + getClass().getSimpleName + " started...")
      val dataset = getDataSet
      sendEvent(new Results(dataset))
    case Reset => reset
    case msg => log error("<----> " + msg)
  }

  /**
   * <p>This method sends the created dataset to all registered listener</p>
   *
   * @return
   * @throws IOException
   * @see {@link Loader}
   */
  @throws(classOf[IOException])
  def getDataSet(): Instances

  /**
   * Configure this loader. URL, password, file-extension
   * @param properties
   */
  def configure(properties: Properties)

  /**
   * Reset dataset, properties and possible connections, inputstreams, etc.
   */
  def reset

}