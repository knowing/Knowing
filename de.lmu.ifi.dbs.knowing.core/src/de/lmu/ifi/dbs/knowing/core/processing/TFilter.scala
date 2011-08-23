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
  
  //Filter is always build. Set this to false if filter has to be trained
  isBuild = true
  
  override def customReceive = {
    case Results(instances) =>
      statusChanged(Running())
      val returns = filter(instances)
      isBuild = true
      sendEvent(Results(returns))
      statusChanged(Ready())
  }

  def filter(instances: Instances): Instances

  def build(instances: Instances) = { /* Do nothing */ }

  //TODO TFilter => Input/Output Format configuration

}