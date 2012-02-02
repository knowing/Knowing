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
import de.lmu.ifi.dbs.knowing.test.EventMatchers._
import de.lmu.ifi.dbs.knowing.test.EmbeddedPresenters._
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.validation._
import java.nio.file.Paths
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.{ ResourceStoreException, UrlResourceStore }
import weka.core.Attribute

@RunWith(classOf[JUnitRunner])
class NaiveBayesTest extends FunSuite with KnowingTestKit with BeforeAndAfter {

  var dpuExecutor1: ActorRef = _
  var dpuExecutor2: ActorRef = _
  var factoryDirectory: EmbeddedFactoryDirectory = _

  var dpu: IDataProcessingUnit = _

  before {
    //Add processor factories
    factoryDirectory = new EmbeddedFactoryDirectory
    factoryDirectory
      .add(new WekaArffLoaderFactory)
      .add(new NaiveBayesFactory)
      .add(new ConfusionMatrixFactory)
      .add(new XCrossValidatorFactory(Some(factoryDirectory)))
      .add(TablePresenterFactory)
      .add(EmbeddedUIComponentFactory)

    //Load the DPU
    //TODO this must work!
    //    dpu = loadDPU("dpu-arff-xcrossvalidation.dpu")
    val dpuURL = Paths.get("KNOWING-INF", "dpu", "dpu-arff-xcrossvalidation.dpu").toUri.toURL
    dpu = loadDPU(dpuURL)
  }

  test("Compare ConfusionMatrices") {
    //resolves the path relative to the project directory. Just a dummy
    val exePath = Paths.get("src").toUri
    val modelStore = new EmbeddedModelStore

    val iris01 = Paths.get("src", "test", "resources", "iris01.arff").toUri.toURL
    val iris02 = Paths.get("src", "test", "resources", "iris02.arff").toUri.toURL

    val resourceStore1 = new EmbeddedResourceStore()
      .put("iris.arff", iris01)

    val resourceStore2 = new EmbeddedResourceStore()
      .put("iris.arff", iris02)

    //Create UIFactory to access results
    val uiFactory1 = new EmbeddedUIFactory
    val uiFactory2 = new EmbeddedUIFactory

    //Create the dpuExectuor running the test
    dpuExecutor1 = createDPUExecutor(dpu, uiFactory1, exePath, factoryDirectory, modelStore, resourceStore1).start
    dpuExecutor2 = createDPUExecutor(dpu, uiFactory2, exePath, factoryDirectory, modelStore, resourceStore2).start

    dpuExecutor1 ! Start()
    dpuExecutor2 ! Start()

    val containers1 = uiFactory1.await
    val containers2 = uiFactory2.await

    val matrix1 = containers1("RawMatrixPresenter").getInstances()(0)
    val matrix2 = containers2("RawMatrixPresenter").getInstances()(0)

    println(matrix1)
    println(matrix2)

  }

}