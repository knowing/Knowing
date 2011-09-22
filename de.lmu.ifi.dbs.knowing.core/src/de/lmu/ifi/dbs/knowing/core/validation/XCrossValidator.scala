package de.lmu.ifi.dbs.knowing.core.validation

import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.{ TFactory, ProcessorFactory }
import de.lmu.ifi.dbs.knowing.core.util.{ OSGIUtil, ResultsUtil }
import de.lmu.ifi.dbs.knowing.core.events._
import java.util.Properties
import weka.core.{ Instance, Instances }
import com.eaio.uuid.UUID
import weka.core.Attribute

class XCrossValidator(var factory: TFactory, var folds: Int, var validator_properties: Properties) extends TProcessor {

  protected var resultHeader: Instances = _
  protected var results: List[Instances] = Nil
  protected var relAttribute = -1
  protected var classLabels: Array[String] = Array()
  protected var currentFold: Int = 0

  private var first_run = true
  private var sortAttribute = ""

  def this() = this(null, 10, new Properties)

  override def customReceive = {
    case status: Status => //statusChanged(status) handle it!
  }

  def build(instances: Instances) {
    //Init classlabels
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        warning(this, "No classLabel found in " + instances.relationName)
      case x => classLabels = classLables(instances.attribute(x))
    }
    //Create crossValidator actors for each fold
    val crossValidators = initCrossValidators(folds)
    debug(this, "Fold-Actors created!")
    statusChanged(Progress("validation", 0, folds))
    startCrossValidation(crossValidators, instances)

    debug(this, "Fold-Actors configured and training started")
  }

  def result(result: Instances, query: Instance) {
    //First run - init header and relationalAttribute
    if (resultHeader == null) {
      resultHeader = new Instances(result, result.size * folds)
      val attr = result.enumerateAttributes
      while (attr.hasMoreElements) {
        val a = attr.nextElement.asInstanceOf[Attribute]
        relAttribute = a.`type` match {
          case Attribute.RELATIONAL => a.index
          case _ => relAttribute
        }
      }
    }

    results = result :: results
    addRelationalInstances(result)
    currentFold += 1
    if (currentFold == folds) {
      debug(this, "Last Fold " + currentFold + " results arrived")
      debug(this, "Copy relational Attribute from index: " + relAttribute)
      sendEvent(Results(mergeResults))
      currentFold = 0
    } else {
      statusChanged(Progress("validation", 1, folds))
      debug(this, "Fold " + currentFold + " results arrived")
    }
	
  }

  protected def initCrossValidators(folds: Int) = for (i <- 0 until folds; val actor = factory.getInstance) yield actor

  protected def startCrossValidation(crossValidators: IndexedSeq[ActorRef], instances: Instances) {
    for (j <- 0 until folds) {
      self startLink crossValidators(j) //Start actors and link yourself as supervisor
      crossValidators(j) ! Register(self, None) //Register so results/status events are send to us
      crossValidators(j) ! Configure(configureProperties(validator_properties, j)) //Configure actor
      crossValidators(j) ! Results(instances.trainCV(folds, j)) //Send the train set
      crossValidators(j) ! Queries(instances.testCV(folds, j)) //Query the trained crossValidator instance
    }
  }

  /**
   * Supports only one relational attribute
   */
  protected def addRelationalInstances(result: Instances) {
    //Add relations to header relational-attribute
    relAttribute match {
      case -1 => //no relational attribute found
      case _ =>
        val insts = result.enumerateInstances        
        while (insts.hasMoreElements) {
          val inst = insts.nextElement.asInstanceOf[Instance]
          inst.relationalValue(relAttribute) match {
            case null => //Do nothing
            case relation => inst.setValue(relAttribute, resultHeader.attribute(relAttribute).addRelation(relation));            
          }          
        }
    }
  }

  /**
   * Merge two matrices. Skips zero-values
   */
  protected def mergeResults: Instances = {
    //Reverse results for ordering with relational attribute
    val returns = ResultsUtil.appendInstances(resultHeader, results.reverse)
    sortAttribute match {
      case null | "" => returns
      case name: String =>
        returns.attribute(name) match {
          case null => warning(this, "Attribute " + name + " not available to sort by")
          case a => returns.sort(a)
        }
        returns
      case _ => returns
    }
  }

  def configure(properties: Properties) = {
    //Retrieve CrossValidator factory
    val factory = OSGIUtil.getFactoryService(CrossValidatorFactory.id)
    factory match {
      case Some(f) => this.factory = f
      case None => throw new Exception("No Factory with " + CrossValidatorFactory.id + " found!")
    }
    //Set properties for this XCrossValidator
    val strFolds = properties.getProperty(CrossValidatorFactory.FOLDS, "10")
    folds = strFolds.toInt
    validator_properties = properties
    sortAttribute = properties.getProperty(XCrossValidatorFactory.SORT_ATTRIBUTE, "")
  }

  /**
   * Creates properties for each CrossValidator-fold
   */
  protected def configureProperties(properties: Properties, fold: Int): Properties = {
    val returns = new Properties
    returns.putAll(properties)
    returns.setProperty(CrossValidatorFactory.FOLD, fold.toString)
    returns.setProperty(CrossValidatorFactory.STANDALONE, "false")
    returns
  }

  def query(query: Instance): Instances = { null }

  def getClassLabels(): Array[String] = classLabels

}

class XCrossValidatorFactory extends ProcessorFactory(classOf[XCrossValidator]) {

  override def createDefaultProperties: Properties = {
    val props = new Properties();
    props.setProperty(CrossValidatorFactory.CLASSIFIER, "")
    props.setProperty(CrossValidatorFactory.FOLDS, "10")
    props.setProperty(XCrossValidatorFactory.SORT_ATTRIBUTE, "")
    props
  }

}

object XCrossValidatorFactory {

  val name: String = "XCrossValidator"
  val id: String = classOf[XCrossValidator].getName

  val SORT_ATTRIBUTE = "sortAttribute"
}
