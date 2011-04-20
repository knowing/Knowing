package de.lmu.ifi.dbs.knowing.core.test

import de.lmu.ifi.dbs.knowing.core.processing.TProcessor

import weka.core.Instances
import weka.core.Instance

class TestProcessor extends TProcessor {

	def build(instances: Instances) = log.info("Instances: " + instances)
	
	def query(q: Instance) = log.info("Query: " + q)
	
	def getClassLabels = new Array(0)
}