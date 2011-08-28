package de.lmu.ifi.dbs.knowing.core.japi

import java.util.Properties
import akka.actor.ScalaActorRef
import weka.core.{Instances, Instance}
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.processing.TSender._
import de.lmu.ifi.dbs.knowing.core.events.Event

/**
 * <p>This wrapper class provides a wrapper for processors developed in Java.
 * It just delegates every method calls to the scala API.</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 04.07.2011
 */
abstract class JProcessor extends TProcessor {
  
  val processor: IProcessor
  
  def build(instances: Instances) = processor.build(instances)

  def query(query: Instance): Instances = processor.query(query, self)

  def result(result: Instances, query: Instance) = processor.result(result, query)

  override def messageException(message: Any) = processor.messageException(message)
  
  def configure(properties: Properties) = processor.configure(properties)

  override def sendEvent(event:Event, out:String ) {
    out match {
      case null => super.sendEvent(event)
      case _ => super.sendEvent(event, Some(out))
    }
  }
}