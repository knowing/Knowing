package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.io.IOException
import java.util.Properties
import weka.core.{ Instances, Instance }
import java.net.URL
import java.net.URI
import java.io.File
import java.io.FilenameFilter

trait TLoader extends TProcessor {

  /**
   * <p>Override for special behaviour</p>
   */
  override protected def customReceive = {
    case Reset => reset
  }

  override def start {
    val dataset = getDataSet
    sendEvent(Results(dataset))
    statusChanged(Finished())
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

  /**
   * [scheme:][//authority][path][?query][#fragment]
   * default scheme: file
   */
  val URL = "url"
  val DIR = "dir"
  val FILE_EXTENSIONS = "extensions"
  /**
   * This attribute is added, when using dir-option, so
   * each source can be identified.
   *
   * property values: true | false
   */
  val SOURCE_ATTRIBUTE = ResultsUtil.ATTRIBUTE_SOURCE

  /** Options: single | multiple **/
  val OUTPUT = "output"
  val OUTPUT_SINGLE = "single"
  val OUTPUT_MULTIPLE = "multiple"

  /** Points to the dpu directory. Ends with a file.seperator */
  val EXE_PATH = "execution" // this properties is created by the GraphSupervisor-Caller

  def getFilePath(properties: Properties): String = {
    val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
    absolute match {
      case true => properties.getProperty(FILE)
      case false => getInputURI(properties).getPath
    }
  }

  def getInputURI(properties: Properties): URI = {
    val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
    val exePath = properties.getProperty(EXE_PATH)
    val file = properties.getProperty(FILE)
    val url = properties.getProperty(URL)
    (url, absolute) match {
      case (null, true) | ("", true) => new URI("file", file, null)
      case (null, false) | ("", false) => resolveFile(exePath, file) getOrElse new URI("file", file, null)
      case (_, true) => new URI(url)
      case (_, false) => resolveFile(exePath, url) getOrElse new URI(url)
      case _ => new URI("")
    }
  }

  def getInputURIs(properties: Properties): Array[URI] = {
    val dirPath = properties.getProperty(DIR)
    if (dirPath == null || dirPath.isEmpty)
      return Array(getInputURI(properties))

    val exePath = properties.getProperty(EXE_PATH)
    val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
    val extensions = properties.getProperty(FILE_EXTENSIONS, "").split(',')
    val dir = absolute match {
      case true => new File(dirPath)
      case false =>
        val sep = "/"; //System.getProperty("file.separator")
        // If exe path is path to dpu, remove the dpu-filename
        val lastIndex = exePath.lastIndexOf(sep)
        //Get the directory path
        val exeFile = new URI(exePath.substring(0, lastIndex)).toURL.getFile
        // `.` means the current directory, so this should be removed
        val dirPathNew = dirPath.replace(".", "")
        // Java assumes ./path/ is a file and ./path is a directory
        if (dirPathNew isEmpty) new File(exeFile)
        else new File(exeFile + sep + dirPathNew)
    }
    val files = dir.listFiles(new FilenameFilter {
      def accept(dir: File, name: String): Boolean = extensions filter (ext => name.endsWith(ext)) nonEmpty
    })
    files match {
      case null => Array()
      case _ => files map (_.toURI)
    }
  }

  def resolveFile(exePath: String, filename: String): Option[URI] = {
    exePath match {
      case null | "" => None
      case _ => Some(new URI(exePath).resolve("./" + filename))
    }
  }
}