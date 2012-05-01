/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.core.util

import de.lmu.ifi.dbs.knowing.core.model.{ IDataProcessingUnit, INode, IEdge, IProperty, IParameter, NodeType }
import scala.collection.JavaConversions._
import java.util.Properties

/**
 * Simple factory to programmatically build dpus.
 * Does not validate DPUs.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-05-01
 */
class DPUBuilder {

	private val internalDPU = IDataProcessingUnit.TYPE.instantiate[IDataProcessingUnit]

	def setName(name: String): DPUBuilder = {
		internalDPU.setName(name)
		this
	}

	def setDescription(descr: String): DPUBuilder = {
		internalDPU.setDescription(descr)
		this
	}

	def addTags(tag: Array[String]): DPUBuilder = addTags(tag: _*)

	def addTags(tag: String*): DPUBuilder = {
		if (tag.isEmpty)
			return this
		val tagString = tag.reduceLeft((tags, t) => tags + "," + t)
		internalDPU.setTags(tagString)
		this
	}

	/**
	 * Overrides nodes with equal id
	 */
	def addNode(node: INode): DPUBuilder = {
		internalDPU.getNodes.addNewElement.copy(node)
		this
	}

	/**
	 * Overrides nodes with equal id
	 */
	def addNode(id: String, factory: Class[_], typ: NodeType): DPUBuilder = {
		val node = internalDPU.getNodes.addNewElement
		node.setId(id)
		node.setFactoryId(factory.getName)
		node.setType(typ)
		this
	}

	/**
	 * Overrides nodes with equal id
	 */
	def addNode(id: String, factory: Class[_], typ: NodeType, properties: Properties): DPUBuilder = {
		val node = internalDPU.getNodes.addNewElement
		node.setId(id)
		node.setFactoryId(factory.getName)
		node.setType(typ)
		for (p <- properties) {
			val property = node.getProperties.addNewElement()
			property.setKey(p._1)
			property.setValue(p._2)
		}
		this
	}

	/**
	 * overrides edges with equal id
	 */
	def addEdge(id: String, source: String, target: String): DPUBuilder = {
		val edge = internalDPU.getEdges.addNewElement
		edge.setId(id)
		edge.setSource(source)
		edge.setTarget(target)
		this
	}

	/**
	 * overrides edges with equal id
	 */
	def addEdge(id: String, source: String, target: String, sourcePort: String, targetPort: String): DPUBuilder = {
		val edge = internalDPU.getEdges.addNewElement
		edge.setId(id)
		edge.setSource(source)
		edge.setSourcePort(sourcePort)
		edge.setTarget(target)
		edge.setTargetPort(targetPort)
		this
	}

	def addParameter(key: String, defaultValue: String): DPUBuilder = {
		val p = internalDPU.getParameters.addNewElement
		p.setKey(key)
		p.setValue(defaultValue)
		this
	}

	def build(): IDataProcessingUnit = {
		val dpu = IDataProcessingUnit.TYPE.instantiate[IDataProcessingUnit]
		dpu.copy(internalDPU)
		dpu
	}

}

object DPUBuilder {

	def apply(): DPUBuilder = new DPUBuilder()

	def apply(name: String) = new DPUBuilder().setName(name)
}
