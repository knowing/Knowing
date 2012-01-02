package knowing.test.processor

import akka.actor.Actor.actorOf
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
import de.lmu.ifi.dbs.knowing.core.weka._
import java.nio.file.Paths
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.{ ResourceStoreException, UrlResourceStore }
import weka.core.Attribute

@RunWith(classOf[JUnitRunner])
class ArffLoaderTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

  var dpuExecutor: ActorRef = _
  var uiFactory: EmbeddedUIFactory = _

  before {
    //Add processor factories
    val factoryDirectory = new EmbeddedFactoryDirectory()
      .add(new WekaArffLoaderFactory)
      .add(new EmbeddedUIComponentPresenterFactory)

    //Create UIFactory to access results
    uiFactory = new EmbeddedUIFactory

    //resolves the path relative to the project directory
    val exePath = Paths.get("src", "test", "resources").toUri
    
    //Load the DPU
    val dpuURL = getClass().getResource("test-dpu-arffLoader.dpu")
    val store = new XmlResourceStore(new UrlResourceStore(dpuURL))
    val resource = new RootXmlResource(store)
    val dpu: IDataProcessingUnit = IDataProcessingUnit.TYPE.instantiate(resource)

    //Create the dpuExectuor running the test
    dpuExecutor = actorOf(new DPUExecutor(dpu, uiFactory, exePath, factoryDirectory)).start
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
    dataset.isEmpty should be (false)
    dataset.size should equal(150)
    
    dataset.relationName should equal("iris")
    
    //Check attributes are correct
    val attributes = for(i <- 0 until dataset.numAttributes) yield dataset.attribute(i)
    attributes should have length 5
    
    for(i <- 0 until 4) attributes(i).`type` should equal(Attribute.NUMERIC)
    attributes(4).name should equal("class")
    attributes(4).`type` should equal(Attribute.NOMINAL)
  }

}