package de.lmu.ifi.dbs.knowing.core.weka

import java.util.Properties
import de.lmu.ifi.dbs.knowing.core.processing.TFilter
import weka.core.{ Instance, Instances }
import weka.filters.Filter
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 18.06.2011
 *
 */
class WekaFilter(protected val filter: Filter) extends TFilter {

  /**
   * <p>Code mainly from weka.filters.Filter</p>
   */
  override def filter(instances: Instances): Instances = {
    val header = new Instances(instances, 0)
    guessAndSetClassLabel(header)
    filter.setInputFormat(header)
    Filter.useFilter(instances, filter)
  }

  def query(query: Instance): Instances = {
    filter.input(query)
	filter.batchFinished

    val returns = new Instances(filter.getOutputFormat, 1)
    returns.add(filter.output)
    returns
  }
  
  //TODO override queries

  def result(result: Instances, query: Instance) = {}

  def configure(properties: Properties) = {}

}

/* =========================== */
/* ==== Processor Factory ==== */
/* =========================== */

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
class WekaFilterFactory[T <: WekaFilter, S <: Filter](wrapper: Class[T], clazz: Class[S]) extends TFactory {

  val name: String = clazz.getSimpleName
  val id: String = clazz.getName

  def getInstance(): ActorRef = actorOf(wrapper)

  /* ======================= */
  /* ==== Configuration ==== */
  /* ======================= */

  def createDefaultProperties: Properties = new Properties

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()

}

object WekaFilterFactory {
  val DEBUG = "debug"
}