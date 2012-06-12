package de.lmu.ifi.dbs.knowing.core.util

import org.scalatest.{ BeforeAndAfter, FunSuite }
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.eclipse.sapphire.modeling.xml.{ XmlResourceStore, RootXmlResource }
import org.eclipse.sapphire.modeling.UrlResourceStore
import de.lmu.ifi.dbs.knowing.core.model._
import de.lmu.ifi.dbs.knowing.core.exceptions.KnowingException
import akka.actor.{ ActorSystem, ActorRef }
import java.net.{ URL, URI }
import java.nio.file.Paths
import java.util.Properties
import DPUUtil._


@RunWith(classOf[JUnitRunner])
class DPUUtilTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

	var dpu: IDataProcessingUnit = _

	before {
		val url = Paths.get("src", "test", "resources", "dpu-arff-xcrossvalidation.dpu").toUri.toURL
		dpu = deserialize(url)
	}

	test("DPU loaded correctly") {
		dpu.getName.getContent should equal("Arff-10fold-cross-validation")
		dpu.getNodes.size should equal(5)
		dpu.getEdges.size should equal(4)
	}

	test("Nodes of type Loader") {
		val loader = loaderNodes(dpu)
		loader should have length 1
	}

	test("Nodes of type Saver") {
		val saver = saverNodes(dpu)
		saver should have length 0
	}

	test("Nodes of type Presenter") {
		val presenter = presenterNodes(dpu)
		presenter should have length 2
	}

	test("find node by id") {
		val empty = nodeById("", dpu)
		empty should be(null)

		val loader = nodeById("ARFF", dpu)
		loader should not be (null)
		loader.getType.getContent should equal(NodeType.LOADER)
	}

	test("get nodeProperties") {
		val validator = nodeById("validator", dpu)
		validator should not be (null)

		val properties = nodeProperties(validator)
		properties.size should equal(4)

		val propertyNames = properties.stringPropertyNames
		propertyNames.contains("classifier") should be(true)
		propertyNames.contains("folds") should be(true)
		propertyNames.contains("kernel-estimator") should be(true)
		propertyNames.contains("supervised-discretization") should be(true)
		propertyNames.contains("not there") should not be (true)
	}

	test("Apply properties - Complete Properties") {
		val url = Paths.get("src", "test", "resources", "parameter-test.dpu").toUri.toURL
		val testDPU = deserialize(url)

		val properties = new Properties()
		properties.put("debug", "true") // n1
		properties.put("input-file", "/home/user/testfile") // n2
		properties.put("distance-custom", "12") // n3

		val newDPU = applyProperties(testDPU, properties);

		val n1 = nodeById("n1", newDPU)
		n1 should not be (null)
		nodeProperties(n1) should have size 1
		nodeProperties(n1).getProperty("debug") should equal("true")

		val n2 = nodeById("n2", newDPU)
		n2 should not be (null)
		nodeProperties(n2) should have size 1
		nodeProperties(n2).getProperty("input-file") should equal("/home/user/testfile")

		val n3 = nodeById("n3", newDPU)
		n3 should not be (null)
		nodeProperties(n3) should have size 1
		nodeProperties(n3).getProperty("distance") should equal("12")

		val n4 = nodeById("n4", newDPU)
		n4 should not be (null)
		nodeProperties(n4) should have size 1
		nodeProperties(n4).getProperty("distance") should equal("24")
	}

	
	test("Apply properties - Input File only") {
		val url = Paths.get("src", "test", "resources", "parameter-test.dpu").toUri.toURL
		val testDPU = deserialize(url)

		val properties = new Properties()
		properties.put("input-file", "/home/user/testfile") // n2
		
		val newDPU = applyProperties(testDPU, properties);

		val n1 = nodeById("n1", newDPU)
		n1 should not be (null)
		nodeProperties(n1) should have size 1
		nodeProperties(n1).getProperty("debug") should equal("true")

		val n2 = nodeById("n2", newDPU)
		n2 should not be (null)
		nodeProperties(n2) should have size 1
		nodeProperties(n2).getProperty("input-file") should equal("/home/user/testfile")

		val n3 = nodeById("n3", newDPU)
		n3 should not be (null)
		nodeProperties(n3) should have size 1
		nodeProperties(n3).getProperty("distance") should equal("36")

		val n4 = nodeById("n4", newDPU)
		n4 should not be (null)
		nodeProperties(n4) should have size 1
		nodeProperties(n4).getProperty("distance") should equal("24")
	}

	test("Apply properties - Missing input-file ") {
		val url = Paths.get("src", "test", "resources", "parameter-test.dpu").toUri.toURL
		val testDPU = deserialize(url)

		val properties = new Properties()
		val thrown = evaluating(applyProperties(testDPU, properties)) should produce [KnowingException]
		thrown.getMessage should equal ("Parameter [input-file] has no default value.")

	}
	

}