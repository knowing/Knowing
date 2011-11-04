package de.lmu.ifi.dbs.knowing.core.processing


import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.TSaver._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.util.Properties
import weka.core.{Instances, Instance}

trait TSaver extends TProcessor {

  protected var _mode = WRITE_MODE_NONE
  protected var _file = "<no file>"
  protected var _url = "<no url>"

  /**
   * <p>Override for special behaviour</p>
   */
  override protected def customReceive = {
    case Configure(p) => saverConfiguration(p)
    case Reset => reset
  }

  def write(instances: Instances)

  /**
   * Reset dataset, properties and possible connections, inputstreams, etc.
   */
  def reset

  private def saverConfiguration(properties: Properties) {
    _mode = properties.getProperty(WRITE_MODE, WRITE_MODE_BATCH)
    _file = properties.getProperty(FILE, "<no file>")
    _url = properties.getProperty(URL, "<no url>")
    configure(properties)
    statusChanged(Waiting())
  }

  /**
   * <p>Just delegating to write(instances)</p>
   */
  def build(instances: Instances) = write(instances)

  /* === Getter Methods === */
  def url: String = _url

  def file: String = _file

  def fetchMode: String = _mode

  def isBatch = _mode equals (WRITE_MODE_BATCH)

  def isIncremental = _mode equals (WRITE_MODE_INCREMENTAL)

  /* == Doesn't needed by TSaver == */
  def query(instance: Instance): Instances = ResultsUtil.emptyResult

  def result(results: Instances, query: Instance) = {}
}

object TSaver {
  /* ==== Properties to configure TSaver ==== */
  val ABSOLUTE_PATH = INodeProperties.ABSOLUTE_PATH
  val FILE = INodeProperties.FILE
  val URL = INodeProperties.URL

  val WRITE_MODE = INodeProperties.WRITE_MODE
  val WRITE_MODE_NONE = INodeProperties.WRITE_MODE_NONE
  val WRITE_MODE_BATCH = INodeProperties.WRITE_MODE_BATCH
  val WRITE_MODE_INCREMENTAL = INodeProperties.WRITE_MODE_INCREMENTAL

  def getFilePath(properties: Properties): String = TLoader.getFilePath(properties)
}