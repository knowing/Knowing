package de.lmu.ifi.dbs.knowing.core.service.impl

import org.scalatest.{ BeforeAndAfter, FunSuite }
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import java.net.URL

@RunWith(classOf[JUnitRunner])
class ModelStoreTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

  var modelStore: ModelStore = _

  before {
    modelStore = new ModelStore
  }

  test("BundleProviders only") {
    modelStore.bundleProviders += ("test" -> new URL("http://www.google.de"))
    val model = modelStore.getModel("test")
    
    model should not equal(None)
    val url = model.get
    url should equal(new URL("http://www.google.de"))
  }

  test("ServiceProviders only") {

  }

  test("BundleProviders and ServiceProviders") {

  }

}