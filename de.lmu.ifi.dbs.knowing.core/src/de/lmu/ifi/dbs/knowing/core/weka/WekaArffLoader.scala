package de.lmu.ifi.dbs.knowing.core.weka

import java.net.URL
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory._
import de.lmu.ifi.dbs.knowing.core.processing.TLoader

import akka.actor.ActorRef
import akka.actor.Actor.actorOf

import java.io.{ FileInputStream, File }
import java.util.Properties

import weka.core.converters.ArffLoader
import weka.core.Instances

import WekaArffLoader._

/**
 * <p>Wrapping the standard WEKA ARFF Loader</p>
 * 
 * @autor Nepomuk Seiler
 * @version 0.7
 * @since 2011-04-xx
 */
class WekaArffLoader extends TLoader {

  lazy val loader = new ArffLoader()

  def getDataSet(): Instances = loader.getDataSet

  def configure(properties: Properties) = {
    val uri = TLoader.getInputURI(properties)
    loader.setSource(uri.toURL)
  }

  def reset = loader.reset

}

object WekaArffLoader {
  val PROP_ABSOLUTE_PATH = TLoader.ABSOLUTE_PATH
  val PROP_FILE = TLoader.FILE
  val PROP_URL = TLoader.URL

}

class WekaArffLoaderFactory extends TFactory {

  val name: String = WekaArffLoaderFactory.name
  val id: String = WekaArffLoaderFactory.id

  def getInstance: ActorRef = actorOf[WekaArffLoader]

  def createDefaultProperties: Properties = {
    val returns = new Properties
    returns setProperty (PROP_FILE, System.getProperty("user.home"))
    returns setProperty (PROP_URL, "file://" + System.getProperty("user.home"))
    returns setProperty (PROP_ABSOLUTE_PATH, "false")
    returns
  }

  def createPropertyValues: Map[String, Array[_ <: Any]] = {
    Map(PROP_FILE -> Array(new File(System.getProperty("user.home"))),
      PROP_URL -> Array(new URL("file", "", System.getProperty("user.home"))),
      PROP_ABSOLUTE_PATH -> boolean_property)
  }

  def createPropertyDescription: Map[String, String] = {
    Map(PROP_FILE -> "ARFF file destination",
      PROP_URL -> "ARFF file URL",
      PROP_ABSOLUTE_PATH -> "Search file in absolute or relative path")
  }
}

object WekaArffLoaderFactory {
  val name: String = "ARFF Loader"
  val id: String = classOf[ArffLoader].getName
}