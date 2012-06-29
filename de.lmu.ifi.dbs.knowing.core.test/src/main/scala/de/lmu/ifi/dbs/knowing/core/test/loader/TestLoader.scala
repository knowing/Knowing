package de.lmu.ifi.dbs.knowing.core.test.loader

import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import java.util.Properties
import weka.core.Instances
import akka.actor.ActorRef

class TestLoader extends TLoader {

  def getDataSet(): Instances =  {
    println("TestLoader: getDataSet")
    null
  }

  override def configure(properties: Properties) = {}

  def reset() = {}

}

class TestLoaderFactory extends ProcessorFactory(classOf[TestLoader])