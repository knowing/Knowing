package de.lmu.ifi.dbs.knowing.presenter

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterAll
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import weka.core.{ Attribute, Instances }
import weka.core.Attribute.{ NUMERIC, NOMINAL, DATE, RELATIONAL }
import java.util.Date
import akka.testkit.TestKit

/**
 * Take a look at to finish implementation.
 * 
 * @see http://akka.io/docs/akka/1.2/scala/testkit-example.html
 * @see http://akka.io/api/akka/1.2/akka/testkit/TestKit.html
 */
@RunWith(classOf[JUnitRunner])
class ITimeIntervalClassPresenterTest extends FunSuite with ShouldMatchers with BeforeAndAfterAll with TestKit {

  var presenter: ITimeIntervalClassPresenter[_] = _
  var testInstances: Instances = _

  override def beforeAll(configMap: Map[String, Any]) {
  }

  override def afterAll() {
    
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