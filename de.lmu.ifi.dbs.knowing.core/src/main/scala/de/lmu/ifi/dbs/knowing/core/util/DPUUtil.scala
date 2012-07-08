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
package de.lmu.ifi.dbs.knowing.core.util

import java.net.URL
import java.util.Properties
import java.io.{ PrintWriter, OutputStream }
import scala.collection.JavaConversions._
import de.lmu.ifi.dbs.knowing.core.model.{ INode, IEdge, IParameter, IProperty, NodeType, IDataProcessingUnit }
import de.lmu.ifi.dbs.knowing.core.service.IDPUDirectory
import de.lmu.ifi.dbs.knowing.core.exceptions.KnowingException
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.UrlResourceStore
import com.typesafe.config.Config

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
object DPUUtil {

	/**
	 * @param url - URL to dpu
	 * @return a deep copy of the url stored. Not linked to any resource
	 */
	def deserialize(url: URL): IDataProcessingUnit = {
		val store = new XmlResourceStore(new UrlResourceStore(url))
		val resource = new RootXmlResource(store)
		val orgDPU: IDataProcessingUnit = IDataProcessingUnit.TYPE.instantiate(resource)
		val dpu: IDataProcessingUnit = IDataProcessingUnit.TYPE.instantiate()
		dpu.copy(orgDPU)
		store.dispose
		dpu
	}

	/**
	 * Creates a new IDataProcessingUnit instance. Isn't hooked to any resource.
	 * @param dpu - original dpu
	 * @return
	 */
	def copy(dpu: IDataProcessingUnit): IDataProcessingUnit = {
		val destination = IDataProcessingUnit.TYPE.instantiate.asInstanceOf[IDataProcessingUnit]
		destination.copy(dpu)
		destination
	}

	def getDPU(directory: IDPUDirectory, id: String): IDataProcessingUnit = directory.getDPU(id).getOrElse(null)
	def getDPUPath(directory: IDPUDirectory, id: String): URL = directory.getDPUPath(id).getOrElse(null)

	@throws(classOf[KnowingException])
	def applyProperties(dpu: IDataProcessingUnit, properties: Properties): IDataProcessingUnit = {
		if (dpu.getParameters.isEmpty && (properties == null || properties.isEmpty))
			return dpu
		if (dpu.getNodes.isEmpty && properties.isEmpty)
			return dpu
			
		//Parameters must be defined to use them
		if (dpu.getParameters.isEmpty && !properties.isEmpty)
			throw new KnowingException("No parameters defined in dpu.")
		if (dpu.getNodes.isEmpty && !properties.isEmpty)
			throw new KnowingException("No nodes defined in dpu to be configured with " + properties)

		val returns = copy(dpu)
		val parameters = returns.getParameters

		//Merged node properties: Map[String, IProperty] == Key => (Key, Value)
		val dpuKeyProperties = returns.getNodes
			.flatMap(_.getProperties.toList)
			.map(p => (p.getKey.getContent -> p))
			.toMap

		//Merged node properties: Map[String, IProperty] == Value => (Key, Value)
		val dpuValueProperties = returns.getNodes
			.flatMap(_.getProperties.toList)
			.filter { p =>
				val v = p.getValue.getContent
				(v != null) && v.startsWith("${") && v.endsWith("}")
			}
			.map { p =>
				val v = p.getValue.getContent
				(v.substring(2, v.length - 1) -> p)
			}
			.toMap

		// 1. find value which could be replaced
		// 2. find key which identical name (inferring parameter)
		// 3. Use parameter value as fallback
		parameters foreach { parameter =>
			val key = parameter.getKey.getContent
			val property = dpuValueProperties.contains(key) match {
				case true => dpuValueProperties(key)
				case false => dpuKeyProperties.get(key).getOrElse {
					throw new KnowingException("Parameter [" + key + "] unused.")
				}
			}
			applyProperty(property, parameter, properties)
		}
		returns
	}

	private def applyProperty(property: IProperty, parameter: IParameter, properties: Properties) {
		properties.containsKey(parameter.getKey.getContent) match {
			case true => property.setValue(properties.getProperty(parameter.getKey.getContent))
			case false =>
				val value = Option(parameter.getValue.getContent).getOrElse {
					throw new KnowingException("Parameter [" + parameter.getKey.getContent + "] has no default value.")
				}
				property.setValue(value)
		}
	}

	/* =========================== */
	/* ==== Node util methods ==== */
	/* =========================== */

	/**
	 *
	 * @param typ - @see NodeType
	 */
	def nodesOfType(typ: NodeType, dpu: IDataProcessingUnit): Array[INode] = {
		dpu.getNodes filter (node => node.getType.getContent.equals(typ)) toArray
	}

	/**
	 *
	 */
	def loaderNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.LOADER, dpu)

	/**
	 *
	 */
	def saverNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.SAVER, dpu)

	/**
	 *
	 */
	def presenterNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.PRESENTER, dpu)

	/**
	 *
	 */
	def processorNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.PROCESSOR, dpu)

	/**
	 *
	 */
	def node(typ: String, factory: String, dpu: IDataProcessingUnit): Array[INode] = {
		dpu.getNodes filter (node => node.getType.equals(typ) && node.getFactoryId.equals(factory)) toArray
	}

	/**
	 * @param id - node ID
	 * @return node or null
	 */
	def nodeById(id: String, dpu: IDataProcessingUnit): INode = nodeByIdOption(id, dpu) match {
		case Some(node) => node
		case None => null
	}

	/**
	 * @param id - node ID
	 * @return Some(node) or None
	 */
	def nodeByIdOption(id: String, dpu: IDataProcessingUnit): Option[INode] = {
		dpu.getNodes find (_.getId.getContent.equals(id))
	}

	def nodeProperties(node: INode): Properties = node.getProperties.foldLeft(new Properties) {
		(properties, p) =>
			if (p.getValue.getContent != null)
				properties.setProperty(p.getKey.getContent, p.getValue.getContent)
			else //Inferring property key as paramter value
				properties.setProperty(p.getKey.getContent, "${" + p.getKey.getContent + "}")
			properties
	}

	def setNodeProperties(node: INode, properties: Properties) = {
		val props = node.getProperties
		props.clear
		properties.foreach {
			case (key, value) =>
				val property = props.insert
				property.setKey(key)
				property.setValue(value)
		}
	}

	/* =========================== */
	/* ==== Edge util methods ==== */
	/* =========================== */

	def edgeById(id: String, dpu: IDataProcessingUnit): IEdge = edgeByIdOption(id, dpu) match {
		case Some(edge) => edge
		case None => null
	}

	def edgeByIdOption(id: String, dpu: IDataProcessingUnit): Option[IEdge] = {
		dpu.getEdges find (_.getId.getContent.equals(id))
	}

	/* ============================== */
	/* ==== Logging util methods ==== */
	/* ============================== */

	/**
	 * Simple debugging method. Prints DPU to given outputStream
	 */
	def print(dpu: IDataProcessingUnit, out: OutputStream = System.out) {
		val writer = new PrintWriter(out)
		if (dpu == null) {
			writer.println("DataProcessingUnit: null")
			writer.flush
			return
		}

		writer.println("### [Data Processing Unit] " + dpu.getName.getContent + " ###")
		writer.println("Description: " + dpu.getDescription.getContent)
		writer.println("Tags: " + dpu.getTags.getContent)
		writer.println("== Configuration: ")
		val conf = dpu.getConfiguration
		writer.println(" History: " + conf.getHistory.getContent)
		writer.println(" Path/Absolute: " + conf.getOutput.getText + " / " + conf.getAbsolute.getContent)

		writer.println(" === Parameters(" + dpu.getParameters.size + ") == ")
		dpu.getParameters.foreach { p =>
			writer.println("  " + p.getKey.getContent + " => " + p.getValue.getContent)
		}

		writer.println(" === Node constraints( " + conf.getNodeConstraints.size + " )")
		conf.getNodeConstraints.foreach { node =>
			writer.println("  Node/Log " + node.getNode.getContent + " / " + node.getLog.getContent)
		}

		writer.println(" === Event constraints( " + conf.getEventConstraints.size + " )")
		conf.getEventConstraints.foreach { e =>
			writer.println("  Node/Log " + e.getType.getContent + " / " + e.getLog.getContent)
		}

		writer.println("== Nodes(" + dpu.getNodes.size + ") ==")
		dpu.getNodes.foreach {
			node =>
				writer.println(" ==== " + node.getId.getText + " ==== ")
				writer.println(" [FactoryId] " + node.getFactoryId.getText)
				writer.println(" [Type] " + node.getType.getText)
				writer.println(" [Properties(" + node.getProperties.size + ")]")
				node.getProperties.foreach {
					p => writer.println("  [x] " + p.getKey.getContent + " -> " + p.getValue.getContent)
				}
		}

		writer.println("== Edges(" + dpu.getEdges.size + ") ==")
		dpu.getEdges.foreach {
			edge =>
				writer.println(" ==== " + edge.getId.getText + " ==== ")
				writer.println(" [Source] " + edge.getSource.getText + ":" + edge.getSourcePort.getText)
				writer.println(" [Target] " + edge.getTarget.getText + ":" + edge.getTargetPort.getText)
		}

		writer.flush
	}
}
