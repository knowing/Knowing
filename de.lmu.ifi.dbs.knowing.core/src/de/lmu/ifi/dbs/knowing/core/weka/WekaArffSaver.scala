package de.lmu.ifi.dbs.knowing.core.weka

import de.lmu.ifi.dbs.knowing.core.processing.{ TSaver, TStreamResolver }
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
 *
 *  <p>Currently the WekaArffSaver implementation
 * doesn't use the ArffSaver from Weka internaly as
 * it doesn't scale well. Instead a custom, always
 * incremental, write-implementation is used.<p>
 *
 * @version 0.3
 * @since 04.07.2011
 * @author Nepomuk Seiler
 */
class WekaArffSaver extends TSaver {

  def write(instances: Instances) {
    //write header
    statusChanged(Progress("Write header for " + instances.relationName, 0, instances.size))
    val header = new Instances(instances, 0)

    var count = 1
    outputs foreach {
      case (file, output) =>
        val writer = new PrintWriter(output)
        writer.print(header.toString)
        //Write instances incremental
        debug(this, "Write Instances[" + instances.numInstances + "] for " + file)

        val attributes = for (i <- 0 until header.numAttributes) yield header.attribute(i)

        val enumInst = instances.enumerateInstances
        var instNum = 1
        var worked = 1
        while (enumInst.hasMoreElements) {
          //Normalize to 0-100 and send only on change.
          worked = (instNum * 100) / instances.size match {
            case 0 => 0
            case w if w == worked => w
            case w if w > worked =>
              statusChanged(Progress("Write..", w, 100))
              w
          }

          val inst = enumInst.nextElement.asInstanceOf[Instance]
          val sb = attributes.foldLeft(new StringBuffer)((sb, attr) => sb.append(inst.value(attr) + ","))

          sb.deleteCharAt(sb.length - 1) //remove last ','
          writer.println(sb.toString)
          instNum += 1
        }
        writer.flush
        writer.close

        debug(this, "Write Instances finished [" + count + "/" + outputs.size + "]")
        count += 1
    }

    reset 
  }

  def reset = configure(properties)

  def configure(properties: Properties) {
    //TODO WekaArffSaver -> RetrievalMode
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
      ABSOLUTE_PATH -> BOOLEAN_PROPERTY)
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