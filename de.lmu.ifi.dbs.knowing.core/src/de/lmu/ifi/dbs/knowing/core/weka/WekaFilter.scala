package de.lmu.ifi.dbs.knowing.core.weka

import java.util.Properties

import de.lmu.ifi.dbs.knowing.core.processing.TFilter

import weka.core.{ Instance, Instances }
import weka.filters.Filter

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 18.06.2011
 *
 */
class WekaFilter(protected val filter: Filter) extends TFilter {

  /**
   * <p>Code mainly from weka.filters.Filter</p>
   */
  def filter(instances: Instances): Instances = {
    filter.setInputFormat(new Instances(instances, 0))
    val enum = instances.enumerateInstances
    while (enum.hasMoreElements) {
      filter.input(enum.nextElement.asInstanceOf[Instance])
    }
    filter.batchFinished
    val returns = filter.getOutputFormat
    var processed = filter.output
    while (processed != null) {
      returns.add(processed)
      processed = filter.output
    }
    returns
  }

  def query(query: Instance): Instances = {
	filter.input(query)
	val returns = filter.getOutputFormat
	returns.add(filter.output)
    returns
  }

  def result(result: Instances, query: Instance) = {}

  def configure(properties: Properties) = {}

}