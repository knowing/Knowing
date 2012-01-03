package de.lmu.ifi.dbs.knowing.core.util

import org.scalatest.{ BeforeAndAfter, FunSuite }
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.eclipse.sapphire.modeling.xml.{ XmlResourceStore, RootXmlResource }
import org.eclipse.sapphire.modeling.UrlResourceStore
import de.lmu.ifi.dbs.knowing.core.model._
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import java.net.{ URL, URI }
import java.nio.file.Paths
import DPUUtil._


@RunWith(classOf[JUnitRunner])
class DPUUtilTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

  var dpu: IDataProcessingUnit = _

  before {
    val url = Paths.get("src", "test", "resources", "dpu-arff-xcrossvalidation.dpu").toUri.toURL
    val store = new XmlResourceStore(new UrlResourceStore(url))
    val resource = new RootXmlResource(store)
    val orgDPU: IDataProcessingUnit = IDataProcessingUnit.TYPE.instantiate(resource)
    dpu = IDataProcessingUnit.TYPE.instantiate()
    dpu.copy(orgDPU)
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
    empty should be (null)
    
    val loader = nodeById("ARFF",dpu)
    loader should not be (null)
    loader.getType.getContent should equal(NodeType.LOADER)
  }

  test("get nodeProperties") {
    val validator = nodeById("validator",dpu)
    validator should not be (null)
    
    val properties = DPUUtil.nodeProperties(validator)
    properties.size should equal(4)
    
    val propertyNames = properties.stringPropertyNames
    propertyNames.contains("classifier") should be (true)
    propertyNames.contains("folds") should be (true)
    propertyNames.contains("kernel-estimator") should be (true)
    propertyNames.contains("supervised-discretization") should be (true)
    propertyNames.contains("not there") should not be (true)
  }
  
}