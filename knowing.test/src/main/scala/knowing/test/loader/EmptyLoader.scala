package knowing.test.loader

import java.util.Properties
import weka.core.{ Instances, Instance }
import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil

class EmptyLoader extends TLoader {

  def getDataSet:Instances  = ResultsUtil.emptyResult
  
  def configure(properties: Properties) = {

  }

}

class EmptyLoaderFactory extends ProcessorFactory(classOf[EmptyLoader])