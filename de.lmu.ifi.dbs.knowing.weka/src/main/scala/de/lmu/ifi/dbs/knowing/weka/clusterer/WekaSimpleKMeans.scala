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
		if(iterations != -1)
			kmeans.setMaxIterations(iterations)
		
		val clusters = properties.getProperty(CLUSTERS, "-1").toInt
		if(clusters != -1)
			kmeans.setNumClusters(clusters)
		
		val seed = properties.getProperty(SEED, "none")
		if(!seed.eq("none"))
			kmeans.setSeed(seed.toInt)
		
		val distance = resolveClass[DistanceFunction](DISTANCE)
		distance.foreach(kmeans.setDistanceFunction(_))
	}

}

class WekaSimpleKMeansFactory extends WekaClustererFactory[WekaSimpleKMeans, SimpleKMeans](classOf[WekaSimpleKMeans], classOf[SimpleKMeans])

object WekaSimpleKMeansFactory {
	val FAST_DISTANCE_CALC = "fast_distance_calc"
}