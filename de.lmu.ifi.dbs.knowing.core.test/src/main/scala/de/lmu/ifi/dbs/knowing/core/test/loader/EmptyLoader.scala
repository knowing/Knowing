package de.lmu.ifi.dbs.knowing.core.test.loader

import java.util.Properties
import weka.core.{ Instances, Instance }
import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.results.EmptyResults

class EmptyLoader extends TLoader {

  def getDataSet:Instances  = EmptyResults()
  
  def configure(properties: Properties) = {

  }

}

class EmptyLoaderFactory extends ProcessorFactory(classOf[EmptyLoader])