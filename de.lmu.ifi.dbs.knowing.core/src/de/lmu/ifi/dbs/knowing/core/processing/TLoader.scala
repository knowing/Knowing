package de.lmu.ifi.dbs.knowing.core.processing

import java.io.IOException
import java.util.Properties
import weka.core.Instances
import de.lmu.ifi.dbs.knowing.core.events._
import akka.actor.Actor
import akka.event.EventHandler

trait TLoader extends Actor with TSender with TConfigurable {

  def receive = {
    case Register(actor) => addListener(actor)
    case Configure(p) => 
      configure(p)
      self reply Ready
    case Start =>
      val dataset = getDataSet
      sendEvent(new Results(dataset))
    case Reset => reset
    case msg => EventHandler.warning(this,"<----> " + msg)
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
   * Reset dataset, properties and possible connections, inputstreams, etc.
   */
  def reset

}

object TLoader {
  val FILE = "file"
  val URL = "url"
}