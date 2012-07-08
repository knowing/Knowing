package de.lmu.ifi.dbs.knowing.core.service.impl

import org.scalatest.{ BeforeAndAfter, FunSuite }
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import java.net.URL
import java.util.{ HashMap, Map }
import de.lmu.ifi.dbs.knowing.core.service.IModelProvider
import de.lmu.ifi.dbs.knowing.core.model.{ IProperty, INode }
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties

@RunWith(classOf[JUnitRunner])
class ModelStoreTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

  val testKey = "test"
  val testUrl = new URL("http://www.google.com")

  val testKey2 = "test2"
  val testUrl2 = new URL("http://www.twitter.com")

  var modelStore: ModelStore = _
  var provider: IModelProvider = _
  var testNode: INode = _
  var property: IProperty = _

  before {
    modelStore = new ModelStore

    testNode = INode.TYPE.instantiate()
    property = testNode.getProperties.insert
    property.setKey(INodeProperties.DESERIALIZE)
    property.setValue(testKey)

    provider = new IModelProvider {

      private val models = new HashMap[String, URL]()
      models.put(testKey, testUrl)

      def getModels(): Map[String, URL] = models

      def getModel(name: String): URL = models.get(name)

    }
  }

  test("BundleProviders only") {
    modelStore.bundleProviders += (testKey -> testUrl)

    modelStore.bundleProviders should have size 1
    modelStore.bundleProviders should contain key (testKey)
    modelStore.bundleProviders should contain value (testUrl)

    val m1 = modelStore.getModel(testKey)
    m1 should not be None
    m1.get should equal(testUrl)

    val m2 = modelStore.getModel(testNode)
    m2 should not be None
    m2.get should equal(testUrl)

  }

  test("ServiceProviders only") {
    //Check if ModelProvider is correct
    provider.getModel(testKey) should equal(testUrl)

    val models = provider.getModels
    models.size should be(1)
    models.containsKey(testKey) should be(true)

    //Bind ModelProvider and test
    modelStore.bindModelProvider(provider)
    modelStore.serviceProviders should have size (1)

    val m1 = modelStore.getModel(testKey)
    m1 should not be None
    m1.get should equal(testUrl)

    val m2 = modelStore.getModel(testNode)
    m2 should not be None
    m2.get should equal(testUrl)
  }

  test("BundleProviders and ServiceProviders") {
    //ModelProvider provides testKey -> testUrl
    modelStore.bindModelProvider(provider)
    //Bundle provides testKey2 -> testUrl2
    modelStore.bundleProviders += (testKey2 -> testUrl2)

    val m1 = modelStore.getModel(testKey)
    m1 should not be None
    m1.get should equal(testUrl)

    val m2 = modelStore.getModel(testNode)
    m2 should not be None
    m2.get should equal(testUrl)

    val m3 = modelStore.getModel(testKey2)
    m3 should not be None
    m3.get should equal(testUrl2)
    
    m3 should not (equal(m1) or equal(m2))
  }

}

