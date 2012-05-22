package de.lmu.ifi.dbs.knowing.weka.clusterer

import de.lmu.ifi.dbs.knowing.core.weka.WekaClusterer
import de.lmu.ifi.dbs.knowing.core.weka.WekaClustererFactory
import de.lmu.ifi.dbs.knowing.core.weka.WekaClustererFactory._
import weka.clusterers.SimpleKMeans
import weka.core.DistanceFunction
import java.util.Properties

class WekaSimpleKMeans extends WekaClusterer(new SimpleKMeans) {

	override def configure(properties: Properties) {
		super.configure(properties)
		val kmeans = clusterer.asInstanceOf[SimpleKMeans]
		
		val iterations = properties.getProperty(ITERATIONS, "-1").toInt
		
		val clusters = properties.getProperty(CLUSTERS, "-1").toInt
		
		val seed = properties.getProperty(SEED, "none")
		
		//TODO null check
//		val distance = resolveClass[DistanceFunction](DISTANCE)
	}

}

class WekaSimpleKMeansFactory extends WekaClustererFactory[WekaSimpleKMeans, SimpleKMeans](classOf[WekaSimpleKMeans], classOf[SimpleKMeans])

object WekaSimpleKMeansFactory {
	val FAST_DISTANCE_CALC = "fast_distance_calc"
}