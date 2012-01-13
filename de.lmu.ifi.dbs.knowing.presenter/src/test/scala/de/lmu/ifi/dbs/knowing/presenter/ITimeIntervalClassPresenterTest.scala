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

import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil._
import de.lmu.ifi.dbs.knowing.core.events._

/**
 * Take a look at to finish implementation.
 *
 * @see http://akka.io/docs/akka/1.2/scala/testkit-example.html
 * @see http://akka.io/api/akka/1.2/akka/testkit/TestKit.html
 */
@RunWith(classOf[JUnitRunner])
class ITimeIntervalClassPresenterTest extends FunSuite with ShouldMatchers with BeforeAndAfter with TestKit {

  var presenter: ActorRef = _
  var testInstances: Instances = _

  before {
    presenter = actorOf[TestTimeIntervalClassPresenter].start
    testInstances = timeIntervalResult(List("A", "B", "C"))

  }

  after {
    presenter.stop
  }

  test("Check Presenter") {
    for (i <- 0 to 5) {
      val clazz = i % 3
      val from = i * 1000
      val to = from + 100
      val inst = new DenseInstance(1, Array(clazz, from, to))
      testInstances.add(inst)
    }

    testInstances.size should equal(6)

    presenter ! Results(testInstances)


    expectMsg {
      case msg: Array[String] => Array()
    }
  }

}

    /*presenter = new ITimeIntervalClassPresenter[Any] {

      *//**
       * Add classes to the TimeIntervalChart
       *//*
      def buildCategories(classes: Array[String]) {
        
      }

      *//**
       *
       *//*
      def addInterval(clazz: String, from: Date, to: Date) {
        
      }
    }*/