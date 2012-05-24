/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
package de.lmu.ifi.dbs.knowing.core.weka

import de.lmu.ifi.dbs.knowing.core.processing._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.results.ClassDistributionResultsBuilder
import java.util.Properties
import weka.core.Instances
import weka.clusterers.Clusterer

/**
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 22.05.2012
 */
class WekaClusterer(var clusterer: Clusterer) extends TClusterer with TClassPropertyResolver {

	private var clusterLabels = Array[String]()
	private val name = getClass.getSimpleName

	def buildClusterer(data: Instances) = {
		log.debug("Build internal model for " + name + " ...")
		clusterer.buildClusterer(data)
		createClusterLabels(clusterer.numberOfClusters)
		isBuild = true
		log.debug("... build successfull for " + name)
	}

	def createClusterLabels(numClusters: Int) {
		clusterLabels = { for (i <- 0 until numClusters) yield "Cluster" + i }.toArray
	}

	def query(query: Instances): Instances = {
		log.debug("Query " + query)
		val builder = new ClassDistributionResultsBuilder(clusterLabels.toList)
		for (i <- 0 until query.numInstances) {
			val inst = query.get(i)
			builder + clusterer.distributionForInstance(inst)
		}
		builder.instances
	}

	def configure(properties: Properties) {}
}

/**
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 22.05.2012
 */
class WekaClustererFactory[T <: WekaClusterer, S <: Clusterer](wrapper: Class[T], clazz: Class[S]) extends ProcessorFactory(wrapper) {

	override val name: String = clazz.getSimpleName
	override val id: String = clazz.getName

}

object WekaClustererFactory {
	val DEBUG = INodeProperties.DEBUG
	val DISTANCE = "distance"
	val CLUSTERS = "clusters"
	val SEED = "seed"
	val ITERATIONS = "iterations"
}