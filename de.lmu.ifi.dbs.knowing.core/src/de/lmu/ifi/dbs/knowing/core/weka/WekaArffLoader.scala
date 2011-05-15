package de.lmu.ifi.dbs.knowing.core.weka

import java.net.URL
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.processing.TLoader

import akka.actor.ActorRef
import akka.actor.Actor.actorOf

import java.io.{ FileInputStream, File }
import java.util.Properties

import weka.core.converters.ArffLoader
import weka.core.Instances

import WekaArffLoader._

class WekaArffLoader extends TLoader {

  lazy val loader = new ArffLoader()

  def getDataSet(): Instances = loader.getDataSet

  def configure(properties: Properties) = {
    val path = properties.getProperty(WekaArffLoader.PROP_FILE)
    val fin = new FileInputStream(path)
    loader.setSource(fin)
  }

  def reset() = {}

}

object WekaArffLoader {

  val PROP_FILE = "file"
  val PROP_URL = "url"

}

class WekaArffLoaderFactory extends TFactory {

  val name: String = WekaArffLoaderFactory.name
  val id: String = WekaArffLoaderFactory.id

  def getInstance: ActorRef =  actorOf[WekaArffLoader]

  def createDefaultProperties: Properties = {
    val returns = new Properties
    returns setProperty (PROP_FILE, System.getProperty("user.home"))
    returns setProperty (PROP_URL, "file://" + System.getProperty("user.home"))
    returns
  }

  def createPropertyValues: Map[String, Array[Any]] = {
    Map(PROP_FILE -> Array(new File(System.getProperty("user.home"))),
      PROP_URL -> Array(new URL("file", "", System.getProperty("user.home"))))
  }

  def createPropertyDescription: Map[String, String] = {
    Map(PROP_FILE -> "ARFF file destination",
      PROP_URL -> "ARFF file URL")
  }
}

object WekaArffLoaderFactory {
  val name: String = "ARFF Loader"
  val id: String = classOf[ArffLoader].getName
}