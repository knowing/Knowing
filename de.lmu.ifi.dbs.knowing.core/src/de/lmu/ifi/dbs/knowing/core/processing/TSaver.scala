package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.TSaver._
import weka.core.Instances

trait TSaver extends Actor with TSender with TConfigurable {

  private var _mode = WRITE_MODE_NONE
  private var _file = "<no file>"
  private var _url = "<no url>"

  def receive: Receive = customReceive orElse defaultReceive
  
  /**
   * <p>Override for special behaviour</p>
   */
  protected def customReceive: Receive = defaultReceive

  /**
   * <p>Default behaviour</p>
   */
  private def defaultReceive: Receive = {
    case Start | Start() => debug(this, "Saver started")
    case Results(instances) => write(instances)
    case Register(actor, port) => addListener(actor, port)
    case Configure(p) => saverConfiguration(p)
    case Reset => reset
    case msg => warning(this, "<----> " + msg)
  }

  def write(instances: Instances)

  /**
   * Reset dataset, properties and possible connections, inputstreams, etc.
   */
  def reset

  private def saverConfiguration(properties: Properties) {
    _mode = properties.getProperty(WRITE_MODE, WRITE_MODE_BATCH)
    _mode = properties.getProperty(FILE, "<no file>")
    _mode = properties.getProperty(URL, "<no url>")
    configure(properties)
    if (self.getSender.isDefined)
      self reply Ready
  }

  def url: String = _url

  def file: String = _file

  def fetchMode: String = _mode

  def isBatch = _mode equals (WRITE_MODE_BATCH)

  def isIncremental = _mode equals (WRITE_MODE_INCREMENTAL)
}

object TSaver {
  /* ==== Properties to configure TSaver ==== */
  val ABSOLUTE_PATH = "absolute-path"
  val FILE = "file"
  val URL = "url"

  val WRITE_MODE = "mode"
  val WRITE_MODE_NONE = "none"
  val WRITE_MODE_BATCH = "batch"
  val WRITE_MODE_INCREMENTAL = "incremental"

  /** Points to the dpu directory. Ends with a file.seperator */
  val DPU_PATH = "path-to-dpu" // this properties is created by the GraphSupervisor-Caller

  def getFilePath(properties: Properties): String = TLoader.getFilePath(properties)
}