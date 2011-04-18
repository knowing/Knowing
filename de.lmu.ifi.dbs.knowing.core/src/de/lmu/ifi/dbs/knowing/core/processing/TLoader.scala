package de.lmu.ifi.dbs.knowing.core.processing

import java.io.IOException
import weka.core.Instances
import java.util.Properties
import de.lmu.ifi.dbs.knowing.core.events._
import akka.actor.Actor

trait TLoader extends Actor with TSender {

  def receive = {
    case Start(p) =>
      log debug("Loader Started")
      configure(p)
      val dataset = getDataSet
      sendEvent(new Results(dataset))
    case Configure(p) => configure(p)
    case Reset => reset
    case Register => addListener(self)
    case msg => log.info("Unkown message: " + msg)
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