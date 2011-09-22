package de.lmu.ifi.dbs.knowing.core.processing

import akka.event.EventHandler.{ debug, info, warning, error }
import java.util.Properties
import weka.core.{ Instance, Instances }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.IProcessorPorts.{ TRAIN, TEST }
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil

/**
 *  @author Nepomuk Seiler
 *  @version 0.1
 *  @since 16.06.2011
 */
trait TClassifier extends TProcessor {

  override def build = {
    case (instances, Some(TEST)) =>
      guessAndSetClassLabel(instances)
      isBuild match {
        case false => queriesQueue += ((self.sender, Queries(instances, instances.relationName)))
        case true =>
          processStoredQueries
          val results = queries(instances)
          results.headOption match {
            case None => //nothing
            case Some(h) =>
              val header = new Instances(h._1.dataset, instances.size)
              val result = ResultsUtil.appendClassDistribution(header, results.toMap)
              sendResults(result)
          }
      }

    case (instances, Some(TRAIN)) => build(instances)
    case (instances, None) => build(instances)
    case (instances, Some(port)) => error(this, "Incompatible target port: " + port)
  }

  /**
   * <p>This method build the internal model which is used<br>
   * to answer queries.</p>
   *
   * <p>The build process should be implemented in an own<br>
   * thread, so other processors could build up their models<br>
   * too.</p>
   *
   * <p>Calling this method more than once should generate a<br>
   * new model based on the old one, instead of building a<br>
   * new model. For reseting the model use {@link #resetModel()}</p>
   *
   * @param the dataset
   */
  def build(instances: Instances)

  /**
   * <p>The presenter connected to this {@link IResultProcessor} calls this<br>
   * method to generate his initial presentation model. After that the<br>
   * presenter starts querying the processor.</p>
   *
   * @return - class labels
   */
  def getClassLabels(): Array[String]

}
