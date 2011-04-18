package de.lmu.ifi.dbs.knowing.core.weka

import akka.actor.ActorRef
import akka.actor.Actor.actorOf

import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.processing.TLoader

import java.io.FileInputStream
import java.util.Properties

import weka.core.converters.ArffLoader
import weka.core.Instances

class WekaArffLoader extends TLoader {

  lazy val loader = new ArffLoader()

  def getDataSet(): Instances = loader.getDataSet

  def configure(properties: Properties) = {
    val path = properties.getProperty(WekaArffLoader.PROP_FILE)
    val fin = new FileInputStream(path)
    loader.setSource(fin)
    log debug ("Set file to " + path)
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

  def getInstance(): ActorRef = {
    actorOf[WekaArffLoader]
  }
}

object WekaArffLoaderFactory {
  val name: String = "ARFF Loader"
  val id: String = classOf[ArffLoader].getName
}