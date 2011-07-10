package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.io.IOException
import java.util.Properties
import weka.core.{ Instances, Instance }

trait TLoader extends TProcessor {

  /**
   * <p>Override for special behaviour</p>
   */
  override protected def customReceive = {
    case Start | Start() =>
      val dataset = getDataSet
      sendEvent(Results(dataset))
      statusChanged(Finished())
    case Reset => reset
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

  /* == Doesn't needed by TLoader == */
  def build(instances: Instances) = {}

  def query(instance: Instance): Instances = ResultsUtil.emptyResult

  def result(results: Instances, query: Instance) = {}

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
        if (path == null || path.isEmpty)
          properties.getProperty(FILE)
        else
          path + properties.getProperty(FILE)
    }
  }
}