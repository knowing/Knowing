package de.lmu.ifi.dbs.knowing.core.common

import de.lmu.ifi.dbs.knowing.core.processing.TFilter
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import java.util.Properties
import weka.core.{ Attribute, Instances}
import scala.collection.mutable.ListBuffer
import java.util.ArrayList
import weka.core.Utils

/**
 * Sets class value to missing for instances which don't reach
 * the given threshold.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
class UncertainClassDumper extends TFilter {

  import UncertainClassDumper._

  private var threshold = THRESHOLD_DEFAULT

  def filter(instances: Instances): Instances = {
    guessAndSetClassLabel(instances)
    // Find attributes
    val attrClasses = new ListBuffer[Attribute]

    for (i <- 0 until instances.numAttributes) {
      val attr = instances.attribute(i)
      if (attr.name.startsWith("class") && attr.name.length > 5)
        attrClasses += attr
    }
    
    val returns = new Instances(instances)
    val classAttr = instances.classAttribute
        
    // Dump instances
    for (i <- 0 until instances.numInstances) {
      val inst = returns.get(i)
      
      // Set class missing only if threshold is exceeded 
      var thresholdFlag = true
      for (j <- 0 until attrClasses.size) {
        if(inst.value(attrClasses(j)) > threshold) {
          thresholdFlag = false;
        }
      }
      if(thresholdFlag) inst.setClassValue(Utils.missingValue())
    }

    returns
  }

  def configure(properties: Properties) = {
    threshold = properties.getProperty(THRESHOLD, THRESHOLD_DEFAULT.toString).toDouble
  }

}

object UncertainClassDumper {

  val THRESHOLD = "threshold"
  val THRESHOLD_DEFAULT = 0.80
}

class UncertainClassDumperFactory extends ProcessorFactory(classOf[UncertainClassDumper])