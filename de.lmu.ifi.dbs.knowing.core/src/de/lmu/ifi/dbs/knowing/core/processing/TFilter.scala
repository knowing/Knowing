package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import weka.core.{ Instances, Instance }
import de.lmu.ifi.dbs.knowing.core.events._

/**
 *  <p>Used to filter Instances</p>
 *
 *  @author Nepomuk Seiler
 *  @version 0.1
 *  @since 16.06.2011
 */
trait TFilter extends TProcessor {
  
  override def customReceive = {
    case Results(instances) =>
      val returns = filter(instances)
      sendEvent(Results(returns))
  }

  def filter(instances: Instances): Instances

  def build(instances: Instances) = { /* Do nothing */ }

  //TODO TFilter => Input/Output Format configuration

}