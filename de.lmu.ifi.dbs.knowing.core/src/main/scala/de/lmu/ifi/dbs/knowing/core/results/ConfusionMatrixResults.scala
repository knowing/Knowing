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
package de.lmu.ifi.dbs.knowing.core.results

import java.util.{ ArrayList, Arrays, Collections, List => JList, Properties, Map => JMap }
import weka.core.{ Attribute, Instances, DenseInstance }
import scala.collection.JavaConversions._

object ConfusionMatrixResults extends ResultsType {

	val name = "confusionMatrix"

	def newInstances(): Instances = throw new UnsupportedOperationException("Cannot create ConfusionMatrix without classes")

	/**
	 * Columns: One column for every label
	 * Rows: One row for every label
	 *
	 * index of row/column -> classes(index)
	 *
	 * @param names - class label names
	 * @return Confusionmatrix already filled up with correct Instance objects.
	 */
	def newInstances(classes: List[String]) = {
		val attributes = new ArrayList[Attribute]
		classes foreach (name => attributes.add(new Attribute(name)))
		val dataset = new Instances(name, attributes, classes.length)
		for (i <- 0 until classes.length)
			dataset.add(i, new DenseInstance(1.0, Array.fill(classes.length) { 0.0 }))
		dataset
	}

	def apply(classes: List[String]) = newInstances(classes)

	/**
	 * <p>Columns: One column for every label</p>
	 * <li>Rows: One row for every label</li>
	 *
	 * <p>index of row/column -> classes(index)</p>
	 *
	 * @param names - class label names
	 * @return Confusionmatrix already filled up with correct Instance objects.
	 */
	def newInstances(classes: JList[String]): Instances = newInstances(classes.toList)
}