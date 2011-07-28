package knowing.test.filter

import de.lmu.ifi.dbs.knowing.core.processing.TFilter
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import weka.core.{ Instances, Instance }
import java.util.Properties
import akka.actor.ActorRef
import akka.actor.Actor

class SourceSplitFilter extends TFilter {

  /**
   * Just prints out the map and returns the head of the map or emptyResult
   */
  def filter(instances: Instances): Instances = {
    val map = ResultsUtil.splitInstanceBySource(instances)
    println(map)
    val ret = map.headOption getOrElse (("empty",ResultsUtil.emptyResult))
    ret._2
  }

  def configure(properties: Properties) {

  }

  def query(query: Instance): Instances = null

  def result(result: Instances, query: Instance) = null
}

class SourceSplitFilterFactory extends TFactory {

  val name = classOf[SourceSplitFilter].getSimpleName
  val id = classOf[SourceSplitFilter].getName

  def getInstance(): ActorRef = Actor.actorOf[SourceSplitFilter]

  def createDefaultProperties: Properties = new Properties

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()

}