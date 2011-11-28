package de.lmu.ifi.dbs.knowing.core.weka

import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory._
import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.processing.TLoader._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import java.io.{ FileInputStream, File }
import java.util.Properties
import java.net.{ URI, URL }
import weka.core.converters.ArffLoader
import weka.core.{ Instances, Instance, Attribute }
import WekaArffLoader._

import scala.collection.JavaConversions.asList

/**
 * <p>Wrapping the standard WEKA ARFF Loader</p>
 *
 * @autor Nepomuk Seiler
 * @version 0.7
 * @since 2011-04-xx
 */
class WekaArffLoader extends TLoader {

  var source: Boolean = false

  private var single = true

  def getDataSet(): Instances = {
    val filenames = asList(inputs map (_._1) toList)
    var count = 0
    val datasets = inputs.par map {
      case (src, in) =>
        val loader = new ArffLoader
        loader.setSource(in)
        (src -> loader)
    } map {
      case (src, loader) =>
        statusChanged(new Progress("Loading", count, inputs.size + 1))
        count += 1
        (src, loader.getDataSet)
    } toList;
    datasets.size match {
      case 0 => ResultsUtil.emptyResult // Nothing generated
      case 1 => datasets.head._2 // Just one input
      case _ => // hell yeah, more than one input
        val head = datasets.head._2
        val header = new Instances(head, 0)
        source match {
          case true =>
            val filter: (String, Instances) => Instances = { (file, inst) =>
              inst.insertAttributeAt(new Attribute(SOURCE_ATTRIBUTE, filenames), inst.numAttributes)
              for (i <- 0 until inst.numInstances) inst.get(i).setValue(inst.attribute(SOURCE_ATTRIBUTE), file)
              inst
            }
            header.insertAttributeAt(new Attribute(TLoader.SOURCE_ATTRIBUTE, filenames), head.numAttributes)
            statusChanged(new Progress("Merge Instances", inputs.size, inputs.size + 1))
            self ! Reset()
            ResultsUtil.appendInstancesTupel(header, datasets, filter)
          case false =>
            self ! Reset()
            ResultsUtil.appendInstances(header, datasets map (_._2))
        }

    }

  }

  override def configure(properties: Properties) = {
    source = properties.getProperty(SOURCE_ATTRIBUTE, "false") toBoolean;
    if(!resolved) {
      inputs = resolveInputs(properties)
      resolved = true
    }
  }

  //Doesn't reset ArffReader -> cannot be gc
  //loaders foreach (_._2.reset) 
  def reset = configure(properties)

  /**
   * Forward if there were multiple loaders
   */
  override def build(inst: Instances) = sendEvent(new Results(inst))

  private def extractFilename(uri: URI): String = {
    val sep = System.getProperty("file.separator")
    val path = uri.getPath
    val index = path.lastIndexOf(sep)
    path.substring(index + 1, path.length)
  }

}

object WekaArffLoader {
  val PROP_ABSOLUTE_PATH = TLoader.ABSOLUTE_PATH
  val PROP_FILE = TLoader.FILE
  val PROP_URL = TLoader.URL
  val PROP_DIR = TLoader.DIR
}

class WekaArffLoaderFactory extends ProcessorFactory(classOf[WekaArffLoader]) {

  override val name: String = WekaArffLoaderFactory.name
  override val id: String = WekaArffLoaderFactory.id

  override def createDefaultProperties: Properties = {
    val returns = new Properties
    returns setProperty (PROP_FILE, System.getProperty("user.home"))
    returns setProperty (PROP_URL, "file://" + System.getProperty("user.home"))
    returns setProperty (PROP_ABSOLUTE_PATH, "false")
    returns
  }

  override def createPropertyValues: Map[String, Array[_ <: Any]] = {
    Map(PROP_FILE -> Array(new File(System.getProperty("user.home"))),
      PROP_URL -> Array(new URL("file", "", System.getProperty("user.home"))),
      PROP_ABSOLUTE_PATH -> BOOLEAN_PROPERTY)
  }

  override def createPropertyDescription: Map[String, String] = {
    Map(PROP_FILE -> "ARFF file destination",
      PROP_URL -> "ARFF file URL",
      PROP_ABSOLUTE_PATH -> "Search file in absolute or relative path")
  }
}

object WekaArffLoaderFactory {
  val name: String = "ARFF Loader"
  val id: String = classOf[ArffLoader].getName
}