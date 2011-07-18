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
  val EXE_PATH = "execution" // this properties is created by the GraphSupervisor-Caller

  def getFilePath(properties: Properties): String = {
    val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
    absolute match {
      case true => properties.getProperty(FILE)
      case false => getInputURI(properties).getPath
    }
  }

  def getInputURI(properties: Properties): URI = {    
    val exePath = properties.getProperty(EXE_PATH)
    val file = properties.getProperty(FILE)
    val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
    val url = properties.getProperty(URL)
    
    (url, absolute) match {
      case (null, true) | ("", true) => new URI("file", file, null)
      case (null, false) | ("", false) => resolveFile(exePath,file) getOrElse new URI("file",file, null)
      case (_, true) => new URI(properties.getProperty(URL))
      case (_, false) => resolveFile(exePath, url) getOrElse new URI(url)
      case _ => new URI("")
    }
  }

  def resolveFile(exePath: String, filename: String): Option[URI] = {
    var sep = System.getProperty("file.separator")
    if(sep.equals("\\")){
      sep = "/"; //resolve methods doesn't like a backslash...
    }
    exePath match {
      case null | "" => None
      case _ =>                        
        //Some(exeURI.resolve("." + sep + filename))
        //val exeURI = new URI("file", null,exePath.replace("file:",""),null)
        val file = new File(exePath.replace("file:","").replace("%5C","/"))
        val path = file.getPath.replace(file.getName,"")        
        val newSome = Some(new URI("file:" + path.replace("\\","/")+filename))
        newSome
    }
  }
}