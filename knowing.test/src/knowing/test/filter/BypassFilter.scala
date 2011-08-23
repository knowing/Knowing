package knowing.test.filter

import akka.event.EventHandler.{debug, info, warning, error}
import de.lmu.ifi.dbs.knowing.core.processing.TFilter
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import weka.core.{ Instances, Instance }
import java.util.Properties
import akka.actor.ActorRef
import akka.actor.Actor

/**
 * Simple bypass filter.
 * 
 * @author Nepomuk Seiler
 * @since 2011-08-22
 * @version 1.0
 */
class BypassFilter extends TFilter {

  def filter(instances: Instances): Instances =  instances
  
  def configure(properties: Properties) {}

  def query(query: Instance): Instances = {
    val ret = new Instances(query.dataset, 0)
    ret.add(query)
    ret
  }

  def result(result: Instances, query: Instance) { }
}

class BypassFilterFactory extends ProcessorFactory(classOf[BypassFilter]) 
