package de.lmu.ifi.dbs.knowing.core.weka

import de.lmu.ifi.dbs.knowing.core.processing.TSaver
import de.lmu.ifi.dbs.knowing.core.processing.TSaver._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory._
import de.lmu.ifi.dbs.knowing.core.weka.WekaArffSaver
import weka.core.Instances
import java.util.Properties
import weka.core.converters.ArffSaver
import java.io.File
import akka.actor.ActorRef
import akka.actor.Actor.actorOf

/**
 * <p>Wraps the weka.core.converters.ArffSaver to save ARFF files</p>
 *
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 04.07.2011
 */
class WekaArffSaver extends TSaver {
  lazy val saver = new ArffSaver

  def write(instances: Instances) {
    println("#### write: ")
    saver.setInstances(instances)
    saver.writeBatch
    reset
  }

  def reset {
    saver.resetOptions
    saver.resetStructure
    saver.resetWriter
  }

  def configure(properties: Properties) {
    println("#### CONFIGURE: " + properties)
    //TODO WekaArffSaver -> RetrievalMode
    if (!file.equals("<no file>")) {
      val outputFile = new File(getFilePath(properties))
      saver.setFile(outputFile)
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
  val id: String = classOf[ArffSaver].getName
}