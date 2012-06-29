package de.lmu.ifi.dbs.knowing.core.test.filter

import de.lmu.ifi.dbs.knowing.core.processing.TFilter
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.results.EmptyResults
import weka.core.{ Instances, Instance }
import java.util.Properties
import akka.actor.ActorRef
import akka.actor.Actor


class SourceSplitFilter extends TFilter {

  /**
   * Just prints out the map and returns the head of the map or emptyResult
   */
  override def filter(instances: Instances): Instances = {
    val map = ResultsUtil.splitInstanceBySource(instances)
    println(map)
    val ret = map.headOption getOrElse (("empty",EmptyResults()))
    ret._2
  }

  def configure(properties: Properties) {

  }

  /**
   * Cannot split a single Instance
   */
  def query(query: Instance): Instances = throw new UnsupportedOperationException

  def result(result: Instances, query: Instance) = null
}

class SourceSplitFilterFactory extends ProcessorFactory(classOf[SourceSplitFilter])