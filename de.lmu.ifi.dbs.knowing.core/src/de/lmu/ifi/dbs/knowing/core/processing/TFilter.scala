package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import weka.core.{ Instances, Instance }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.{appendInstances, emptyResult }

/**
 *  <p>Used to filter Instances</p>
 *
 *  @author Nepomuk Seiler
 *  @version 0.3
 *  @since 16.06.2011
 */
trait TFilter extends TProcessor {
  
  //Filter is always build. Set this to false if filter has to be trained
  isBuild = true
  
  override def customReceive = {
    case Results(instances) =>
      statusChanged(Running())
      val returns = filter(instances)
      isBuild = true
      //Filter sends results to listeners
      sendEvent(Results(returns))
      statusChanged(Ready())
  }

  /**
   * Default implementation uses the query method. Override
   * this method for performance issues or if the input is need as a whole.
   */
  def filter(instances: Instances): Instances = {
    val results = queries(instances)
    mergeResults(results)
  }
  
  /**
   * Delegates to filter method
   */
  def build(instances: Instances) = filter(instances)
  
  protected def mergeResults(results: List[(Instances, Instance)]): Instances = {
    val instances = results map(_._1)
    instances.headOption match {
      case Some(head) => 
        val header = new Instances(head, 0)
        appendInstances(header, instances)
      case None => emptyResult
    }
  }
  
  //TODO TFilter => Input/Output Format configuration

}