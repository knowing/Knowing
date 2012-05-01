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
package de.lmu.ifi.dbs.knowing.debug.presenter

import de.lmu.ifi.dbs.knowing.core.model.{ IDataProcessingUnit, NodeType }
import de.lmu.ifi.dbs.knowing.core.util.{ DPUBuilder, DPUUtil }
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import weka.core.converters.ArffLoader
import java.util.Properties

/**
 * Creates a DPU to present the results of the execution process.
 * [ArffLoader -> Original Presenter]
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-05-1
 */
object PresentationDPUBuilder {

	def create(dpu: IDataProcessingUnit): IDataProcessingUnit = {
		val presenterNodes = DPUUtil.presenterNodes(dpu)
		presenterNodes.foldLeft(DPUBuilder(dpu.getName.getContent)) {
			(b, n) =>
				val loaderId = n.getId.getContent + ".loader"
				val properties = new Properties
				properties.setProperty(INodeProperties.FILE, n.getId.getContent + ".arff")
				b.addNode(loaderId, classOf[ArffLoader], NodeType.LOADER, properties)
					.addNode(n)
					.addEdge(loaderId + "2" + n.getId.getContent, loaderId, n.getId.getContent)
		}.build()
	}
}