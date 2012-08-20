package de.lmu.ifi.dbs.knowing.core.processing

import org.scalatest.{ BeforeAndAfter, FunSuite }
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import java.util.ArrayList
import weka.core.{ Attribute, Instances, ProtectedProperties }
import java.util.Properties

@RunWith(classOf[JUnitRunner])
class ImmutableInstancesTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

  test("Preserve Attribute Meta-Data") {
    val attributes = new ArrayList[Attribute]
    val props = new Properties()
    props.setProperty("name", "my-name")
    val attribute = new Attribute("y0", new ProtectedProperties(props))
    attributes.add(attribute)
    
    val instances = new Instances("test", attributes, 0)
    val immutable = new ImmutableInstances(instances)
    
    instances.attribute("y0").getMetadata().getProperty("name") should be equals( immutable.attribute("y0").getMetadata().getProperty("name"))
    
    instances.attribute("y0") should be equals(immutable.attribute("y0"))
  }
}