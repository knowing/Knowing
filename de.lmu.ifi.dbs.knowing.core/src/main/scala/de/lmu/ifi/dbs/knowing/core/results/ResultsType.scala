package de.lmu.ifi.dbs.knowing.core.results

import weka.core.{ Instances, Attribute }
import java.util.ArrayList

/**
 * A class implementing this trait provides a Instances format.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait ResultsType {

	/** Relation name. */
	val name: String

	/**
	 *
	 */
	def newInstances(): Instances

	/**
	 * delegating to newInstances()
	 */
	def apply(): Instances = newInstances

}

/**
 *
 */
object ResultsType {

	/* ========================= */
	/* === Default Attributes == */
	/* ========================= */

	val ATTRIBUTE_DUMMY = "dummy"
	val ATTRIBUTE_CLASS = "class"
	val ATTRIBUTE_CLASS_PREFIX = "class"
	val ATTRIBUTE_CLASS_DISTRIBUTION = "class_distribution"
	val ATTRIBUTE_PROBABILITY = "probability"
	val ATTRIBUTE_TIMESTAMP = "timestamp"
	val ATTRIBUTE_VALUE = "y"
	val ATTRIBUTE_VALUE_PREFIX = "y"
	val ATTRIBUTE_FROM = "from"
	val ATTRIBUTE_TO = "to"
	val ATTRIBUTE_SOURCE = "source"

	val META_ATTRIBUTE_NAME = "name"

	val DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss:SSS"

}