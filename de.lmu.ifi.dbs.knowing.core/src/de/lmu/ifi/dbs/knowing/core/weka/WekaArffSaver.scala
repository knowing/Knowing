package de.lmu.ifi.dbs.knowing.core.weka

import de.lmu.ifi.dbs.knowing.core.processing.TSaver
import de.lmu.ifi.dbs.knowing.core.processing.TSaver._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory._
import de.lmu.ifi.dbs.knowing.core.events._
import weka.core.{ Instances, Instance, Attribute }
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import java.io.{ PrintWriter, OutputStream, FileOutputStream, File }
import java.util.Properties
import scala.collection.JavaConversions._

/**
 * <p>Wraps the weka.core.converters.ArffSaver to save ARFF files</p>
 *
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 04.07.2011
 */
class WekaArffSaver extends TSaver {

  private var out: OutputStream = _

  def write(instances: Instances) {
    debug(this, "Write Instances")
    //write header
    statusChanged(Progress("Write header",0, instances.size))
    val header = new Instances(instances, 0)
    val writer = new PrintWriter(out)
    writer.println(header.toString)

    //Write instances incremental
    val attributes = header.enumerateAttributes.toList.asInstanceOf[List[Attribute]]
    val enumInst = instances.enumerateInstances
    var instNum = 1
    while (enumInst.hasMoreElements) {
      statusChanged(Progress("Write..",instNum, instances.size))
      val inst = enumInst.nextElement.asInstanceOf[Instance]
      val sb = attributes.foldLeft(new StringBuffer)((sb, attr) => sb.append(inst.value(attr)+","))
      sb.deleteCharAt(sb.length-1)
      writer.println(sb.toString)
      instNum += 1
    }
    writer.flush
    writer.close

    reset
    //TODO WekaArffSaver -> must be configured again, after write
  }

  def reset {
  }

  def configure(properties: Properties) {
    //TODO WekaArffSaver -> RetrievalMode
    if (!file.equals("<no file>")) {
      val outputFile = new File(getFilePath(properties))
      if(!outputFile.exists) outputFile.createNewFile
      out = new FileOutputStream(outputFile)
    } else if (!url.equals("<no url>")) {
      //TODO WekaArffSaver -> URL output
    }
  }
}

class WekaArffSaverFactory extends TFactory {

  val name: String = WekaArffSaverFactory.name
  val id: String = WekaArffSaverFactory.id

  def getInstance: ActorRef = actorOf[WekaArffSaver]

  def createDefaultProperties: Properties = {
    val returns = new Properties
    returns setProperty (FILE, System.getProperty("user.home"))
    returns setProperty (URL, "file://" + System.getProperty("user.home"))
    returns setProperty (ABSOLUTE_PATH, "false")
    returns
  }

  def createPropertyValues: Map[String, Array[_ <: Any]] = {
    Map(FILE -> Array(new File(System.getProperty("user.home"))),
      URL -> Array(new java.net.URL("file", "", System.getProperty("user.home"))),
      ABSOLUTE_PATH -> boolean_property)
  }

  def createPropertyDescription: Map[String, String] = {
    Map(FILE -> "ARFF file destination",
      URL -> "ARFF file URL",
      ABSOLUTE_PATH -> "Search file in absolute or relative path")
  }
}

object WekaArffSaverFactory {
  val name: String = "ARFF Loader"
  val id: String = classOf[weka.core.converters.ArffSaver].getName
}