package de.lmu.ifi.dbs.knowing.core.processing

import java.io.IOException
import java.util.Properties
import weka.core.Instances
import de.lmu.ifi.dbs.knowing.core.events._
import akka.actor.Actor
import akka.event.EventHandler

trait TLoader extends Actor with TSender with TConfigurable {

  def receive: Receive = customReceive orElse defaultReceive

  /**
   * <p>Override for special behaviour</p>
   */
  protected def customReceive: Receive = defaultReceive

  /**
   * <p>Default behaviour</p>
   */
  private def defaultReceive: Receive = {
    case Register(actor) => addListener(actor)
    case Configure(p) =>
      configure(p)
      if (self.getSender.isDefined)
        self reply Ready
    case Start | Start() =>
      val dataset = getDataSet
      sendEvent(new Results(dataset))
    case Reset => reset
    case msg => EventHandler.warning(this, "<----> " + msg)
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
  /* ==== Properties to configure TLoader ==== */
  val ABSOLUTE_PATH = "absolute-path"
  val FILE = "file"
  val URL = "url"

  /** Points to the dpu directory. Ends with a file.seperator */
  val DPU_PATH = "path-to-dpu" // this properties is created by the GraphSupervisor-Caller

  def getFilePath(properties: Properties): String = {
    val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
    absolute match {
      case true => properties.getProperty(FILE)
      case false => 
        val path = properties.getProperty(DPU_PATH)
        if(path == null || path.isEmpty)
        	properties.getProperty(FILE)
        else
        	path + properties.getProperty(FILE)
    }
  }
}