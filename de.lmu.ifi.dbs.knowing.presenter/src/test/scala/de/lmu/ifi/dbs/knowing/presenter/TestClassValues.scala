package de.lmu.ifi.dbs.knowing.presenter

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import weka.core.{ Attribute, Instances, DenseInstance }
import weka.core.Attribute.{ NUMERIC, NOMINAL, DATE, RELATIONAL }
import java.util.Date
import akka.testkit.TestKit
import akka.actor.{ActorSystem,ActorRef, Props}
import akka.util.duration._
import scala.collection.mutable.ArrayBuffer
import de.lmu.ifi.dbs.knowing.core.events._
import weka.core.Utils

@RunWith(classOf[JUnitRunner])
class TestClassValues  extends FunSuite with ShouldMatchers with BeforeAndAfter {
  
  test("MissingClassValue as String") {
    val instances = ITimeIntervalClassPresenter(List("A", "B", "C"))
    
    val values = Array(100000.0, 200000.0, Utils.missingValue())
    val inst = new DenseInstance(1.0, values)
    
    instances.add(inst)
    
    instances.size should be(1)
    
    val inst1 = instances.get(0)
    
    //val labelIndex = inst1.value(instances.classAttribute)
    //labelIndex should equal(Utils.missingValue())
    
    val missing = inst1.isMissing(instances.classAttribute)
    missing should be (true)

  }

}