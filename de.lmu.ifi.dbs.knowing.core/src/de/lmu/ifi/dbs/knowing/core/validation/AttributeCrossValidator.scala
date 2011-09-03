package de.lmu.ifi.dbs.knowing.core.validation

import java.util.Properties
import akka.actor.ActorRef
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.events._
import weka.core.Instances

class AttributeCrossValidator extends XCrossValidator {

  import AttributeCrossValidator._
  private var splitAttr = ResultsUtil.ATTRIBUTE_SOURCE

  override def build(instances: Instances) {
    //Init classlabels
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        warning(this, "No classLabel found in " + instances.relationName)
      case x => classLabels = classLables(instances.attribute(x))
    }
    confusionMatrixHeader = ResultsUtil.confusionMatrix(getClassLabels.toList)
    val instMaps = ResultsUtil.splitInstanceByAttribute(instances, splitAttr)
    //Map test-data -> train-data 
    val instMap = for (e <- instMaps) yield instMaps.partition(e2 => e._1.equals(e2._1))
    folds = instMap.size
    debug(this, "Fold-Actors created!")
    statusChanged(Progress("validation", 0, folds))
    val crossValidators = initCrossValidators(folds)
    var i = 0
    instMap foreach {
      case (test, train) =>
        //Debug purpose
        val sb = new StringBuffer
        sb.append("test[")
        sb.append(test.head._1)
        sb.append("] -> train ")
        train foreach (elem => sb.append(elem._1 + ","))
        debug(this, sb.toString)
        
        val testData = test.head._2
        val trainData = ResultsUtil.appendInstances(new Instances(testData, 0), train map (_._2) toList)
        guessAndSetClassLabel(testData)
        guessAndSetClassLabel(trainData)
        //Logic
        self startLink crossValidators(i)
        crossValidators(i) ! Register(self, None)
        crossValidators(i) ! Configure(configureProperties(validator_properties, i))
        crossValidators(i) ! Results(trainData)
        crossValidators(i) ! Queries(testData)
        i += 1
    }

    debug(this, "Fold-Actors configured and training started")
  }

  override def configure(properties: Properties) {
    splitAttr = properties.getProperty(ATTRIBUTE, ResultsUtil.ATTRIBUTE_SOURCE)
    super.configure(properties)
  }

}

object AttributeCrossValidator {
  val ATTRIBUTE = "attribute"
}

class AttributeCrossValidatorFactory extends ProcessorFactory(classOf[AttributeCrossValidator])