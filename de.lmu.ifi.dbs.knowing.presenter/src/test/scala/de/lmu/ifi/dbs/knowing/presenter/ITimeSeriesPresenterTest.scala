package de.lmu.ifi.dbs.knowing.presenter

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import akka.testkit.TestKit
import akka.actor.{ ActorSystem, ActorRef, Props }
import weka.core.{ Attribute, Instances, DenseInstance }
import de.lmu.ifi.dbs.knowing.core.results.TimeSeriesResults
import de.lmu.ifi.dbs.knowing.core.events._
import scala.collection.mutable.ArrayBuffer
import java.util.Date

@RunWith(classOf[JUnitRunner])
class ITimeSeriesPresenterTest extends TestKit(ActorSystem()) with FunSuite with ShouldMatchers with BeforeAndAfter {

	val content = ArrayBuffer[(Double, List[Double])]()

	var presenter: ActorRef = _
	var testInstances: Instances = _
	var series: Map[String, Attribute] = _

	before {
		presenter = system.actorOf(Props(new TestTimeSeriesPresenter))
		testInstances = ITimeSeriesPresenter.newInstances(List("x", "y", "z"))
		series = TimeSeriesResults.findValueAttributesAsMap(testInstances)
	}

	after {
		system.stop(presenter)
	}

	test("Check Presenter") {
		for (i <- 0 to 5) {
			val date = i * 100000
			val x = i + (100 * Math.random)
			val y = i + (230 * Math.random)
			val z = -700 * (i + Math.random)
			val inst = new DenseInstance(1, Array(date, x, y, z))
			testInstances.add(inst)
			content += ((date, List(x, y, z)))
		}

		testInstances.size should equal(6)

		presenter ! Results(testInstances)

		expectMsg(series.values.toList)
		for (msg <- content) {
			expectMsg(new Date(msg._1.toLong))
			for (v <- msg._2) {
				expectMsg(v)
			}
		}
		expectMsg("update")
	}

}