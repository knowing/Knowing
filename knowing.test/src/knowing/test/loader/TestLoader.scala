package knowing.test.loader

import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.factory.TFactory

import java.util.Properties
import weka.core.Instances

import akka.actor.Actor.actorOf
import akka.actor.ActorRef

class TestLoader extends TLoader {

  val name: String = TestLoaderFactory.name
  val id: String = TestLoaderFactory.id

  def getDataSet(): Instances =  {
    println("TestLoader: getDataSet")
    null
  }

  def configure(properties: Properties) = {}

  def reset() = {}

}

class TestLoaderFactory extends TFactory {

  val name: String = TestLoaderFactory.name
  val id: String = TestLoaderFactory.id

  def getInstance(): ActorRef = {
    actorOf[TestLoader]
  }
}

object TestLoaderFactory {

  val name: String = "Test loader"
  val id: String = "Loader id"

}