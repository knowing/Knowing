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
import akka.actor.Actor.actorOf
import akka.actor.ActorRef
import akka.util.duration._
import scala.collection.mutable.ArrayBuffer

import de.lmu.ifi.dbs.knowing.core.events._

@RunWith(classOf[JUnitRunner])
class IPiePresenterTest extends FunSuite with ShouldMatchers with BeforeAndAfter with TestKit {

  val classes = List("A", "B", "C")
  val content = ArrayBuffer[(String, Double)]()

  var presenter: ActorRef = _
  var testInstances: Instances = _

  before {
    presenter = actorOf[TestPieChartPresenter].start
    testInstances = IPieChartPresenter.newInstances(classes)
  }

  after {
    presenter.stop
  }

  test("Check Presenter") {
    for (i <- 0 to 5) {
      val clazz = i % 3
      val value = i + 100 * Math.random
      val inst = new DenseInstance(1, Array(clazz, value))
      testInstances.add(inst)
      content += ((testInstances.classAttribute.value(clazz), value))
    }

    testInstances.size should equal(6)

    presenter ! Results(testInstances)
    
    for(msg <- content) {
      expectMsg(msg._1)
      expectMsg(msg._2)
    }
    expectMsg("update")
  }

}