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
import de.lmu.ifi.dbs.knowing.test.EmbeddedPresenters._
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.validation._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil._
import de.lmu.ifi.dbs.knowing.core.results.ClassDistributionResults._
import de.lmu.ifi.dbs.knowing.weka.io._
import de.lmu.ifi.dbs.knowing.weka.classifier.WekaNaiveBayesFactory
import java.nio.file.Paths
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.{ ResourceStoreException, UrlResourceStore }
import weka.core.{ Instances, Attribute }
import scala.collection.JavaConversions._

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
      .add(new WekaNaiveBayesFactory)
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
    dpuExecutor1 = createDPUExecutor(dpu, uiFactory1, exePath, factoryDirectory, modelStore, resourceStore1)
    dpuExecutor2 = createDPUExecutor(dpu, uiFactory2, exePath, factoryDirectory, modelStore, resourceStore2)

    dpuExecutor1 ! Start()
    dpuExecutor2 ! Start()

    //Wait for process is finished
    val containers1 = uiFactory1.await
    val containers2 = uiFactory2.await

    //Receive RawMatrix Instances
    val dist1 = containers1("RawMatrixPresenter").getInstances()(0)
    val dist2 = containers2("RawMatrixPresenter").getInstances()(0)

    val distAttrs = findClassDistributionAttributesAsMap(dist1)

    //Split by class
    val classes1 = splitInstanceByAttribute(dist1, "class", false)
    val classes2 = splitInstanceByAttribute(dist2, "class", false)

    // doesn't work :(
    //    val funAvg = (clazz:String, instances: Instances) => {
    //        val distAttr = distAttrs(clazz)
    //        val avg = instances.foldLeft(0.0)((avg,inst) => avg + inst.value(distAttr))
    //        (clazz -> avg)
    //    } 

    //Average classification of dataset 1
    val avg1 = classes1 map {
      case (clazz, instances) =>
        val distAttr = distAttrs(clazz)
        val sum = instances.foldLeft(0.0)((avg, inst) => avg + inst.value(distAttr))
        val avg = sum / instances.numInstances
        (clazz -> avg)
    }

    //Average classification of dataset 2
    val avg2 = classes2 map {
      case (clazz, instances) =>
        val distAttr = distAttrs(clazz)
        val sum = instances.foldLeft(0.0)((avg, inst) => avg + inst.value(distAttr))
        val avg = sum / instances.numInstances
        (clazz -> avg)
    }

    println(avg1)
    println(avg2)
  }

}