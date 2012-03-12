package de.lmu.ifi.dbs.knowing.core.common.io

import java.util.Properties
import java.io.ObjectInputStream
import weka.core.Instances
import weka.core.converters.SerializedInstancesLoader
import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.results.EmptyResults


class InstancesLoader extends TLoader {

  def getDataSet(): Instances = {
    val inst = inputs map {
      case (file, in) => 
        val loader = new SerializedInstancesLoader
        loader.setSource(in)
        loader.getDataSet
    }
    inst.size match {
      case 0 => EmptyResults()
      case 1 => inst.head
      case _ =>
        val header = new Instances(inst.head,0)
        ResultsUtil.appendInstances(header, inst.toList)
    }
  }
  
  def configure(properties: Properties): Unit = {}

}

class InstancesLoaderFactory extends ProcessorFactory(classOf[InstancesLoader]) {
  override val id = classOf[weka.core.converters.SerializedInstancesLoader].getName
}