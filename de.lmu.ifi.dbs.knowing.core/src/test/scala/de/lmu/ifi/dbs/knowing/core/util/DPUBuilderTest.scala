package de.lmu.ifi.dbs.knowing.core.util

import org.scalatest.{ BeforeAndAfter, FunSuite }
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import weka.core.converters.{ArffSaver,ArffLoader}
import de.lmu.ifi.dbs.knowing.core.model.NodeType

@RunWith(classOf[JUnitRunner])
class DPUBuilderTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

	
	test("Loader->Saver DPU") {
		val dpu = DPUBuilder("LoaderSaverDPU")
		.addNode("ARFFLoader", classOf[ArffLoader], NodeType.LOADER)
		.addNode("ARFFSaver", classOf[ArffSaver], NodeType.SAVER)
		.addEdge("loader2Saver", "ARFFLoader", "ARFFSaver")
		.build()
		
		DPUUtil.print(dpu, System.out)
		
		val validation = DPUValidation.compiletime(dpu)
		validation.hasErrors should be (false)
		
		val loader = DPUUtil.nodeById("ARFFLoader", dpu)
		loader should not be(null)
		loader.getId.getContent should be equals("ARFFLoader")
		loader.getFactoryId.getContent should be equals(classOf[ArffLoader].getName)
		loader.getType should be equals(NodeType.LOADER)
		 
		val saver =  DPUUtil.nodeById("ARFFSaver", dpu)
		saver should not be(null)
		
		val edge = DPUUtil.edgeById("loader2Saver", dpu)
		edge should not be(null)
		
	}
}