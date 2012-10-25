package de.lmu.ifi.dbs.knowing.core.test.processor

import akka.actor.ActorRef
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import de.lmu.ifi.dbs.knowing.core.processing.DPUExecutor
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.test._
import de.lmu.ifi.dbs.knowing.test.EventMatchers._
import de.lmu.ifi.dbs.knowing.weka.io._
import java.nio.file.Paths
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.{ ResourceStoreException, UrlResourceStore }
import weka.core.Attribute

@RunWith(classOf[JUnitRunner])
class ArffLoaderTest extends FunSuite with KnowingTestKit with BeforeAndAfter {

  var dpuExecutor: ActorRef = _
  var uiFactory: EmbeddedUIFactory = _

  before {
    //Add processor factories
    val factoryDirectory = new EmbeddedFactoryDirectory()
      .add(new WekaArffLoaderFactory)
      .add(new EmbeddedUIComponentPresenterFactory)

    val modelStore = new EmbeddedModelStore
    val resourceStore = new EmbeddedResourceStore

    //Create UIFactory to access results
    uiFactory = new EmbeddedUIFactory

    //resolves the path relative to the project directory
    val exePath = Paths.get("src", "test", "resources").toUri

    //Load the DPU
    val dpu = loadDPU("test-dpu-arffLoader.dpu", classOf[ArffLoaderTest])

    //Create the dpuExectuor running the test
    dpuExecutor = createDPUExecutor(dpu, uiFactory, exePath, factoryDirectory, modelStore, resourceStore)
  }

  test("Run ARFFLoader") {
    //Start execution
    dpuExecutor ! Start()

    //Wait for results. Future dpuExecutor ? Start() cannot be used as this testclass isn't an actor
    val containers = uiFactory.await()
    val container = containers.head._2
    val instances = container.getInstances()

    //The container should only hold one loaded instances object in its array
    instances should have length 1

    //Check name and size of Instances loaded
    val dataset = instances(0)
    dataset.isEmpty should be(false)
    dataset.size should equal(150)

    val results = new Results(dataset, Some("train"))

    //Check Results object
    results should be(aResult)
    results should have(port("train"))

    //Check Instances are correct
    results should have(name("iris")) //equal to: dataset.relationName should equal("iris")
    results should have(size(150)) //equal to: dataset.size should equal(150)

    //Check attributes are correct
    results should have(attribute(Attribute.NUMERIC, 4))
    results should have(attribute("class"))

    //Check content
    results should have(instance(List(4.7, 3.2, 1.3, 0.2, "Iris-setosa")))

  }

}