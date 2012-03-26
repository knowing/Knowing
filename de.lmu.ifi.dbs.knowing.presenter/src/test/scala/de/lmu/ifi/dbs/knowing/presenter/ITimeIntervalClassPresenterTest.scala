/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
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
import de.lmu.ifi.dbs.knowing.core.events._
import scala.collection.mutable.ArrayBuffer

/**
 * Take a look at to finish implementation.
 *
 * @see http://akka.io/docs/akka/1.2/scala/testkit-example.html
 * @see http://akka.io/api/akka/1.2/akka/testkit/TestKit.html
 */
@RunWith(classOf[JUnitRunner])
class ITimeIntervalClassPresenterTest extends FunSuite with ShouldMatchers with BeforeAndAfter with TestKit {

  val classes = List("A", "B", "C")
  val content = ArrayBuffer[(String, Int,Int)]()
  
  var presenter: ActorRef = _
  var testInstances: Instances = _

  before {
    presenter = actorOf[TestTimeIntervalClassPresenter].start
    testInstances = ITimeIntervalClassPresenter.newInstances(classes)
  }

  after {
    presenter.stop
  }

  test("Check Presenter") {
    for (i <- 0 to 5) {
      val clazz = i % 3
      val from = i * 100000
      val to = from + 10000
      val inst = new DenseInstance(1, Array(from, to, clazz))
      testInstances.add(inst)
      content += ((testInstances.classAttribute.value(clazz), from, to))
    }

    testInstances.size should equal(6)

    presenter ! Results(testInstances)
    
    expectMsg(classes)
    for(msg <- content) {
      expectMsg(msg)
    }

  }

}