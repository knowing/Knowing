package de.lmu.ifi.dbs.knowing.test

import org.scalatest.matchers._
import weka.core.{ Instances, Instance, Attribute }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties._
import de.lmu.ifi.dbs.knowing.core.model.IEdge.DEFAULT_PORT

/**
 * 
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait EventMatchers {

  def port(port: String) = new HavePropertyMatcher[Results, String] {
    def apply(result: Results) = {
      val actualPort = result.port.getOrElse(DEFAULT_PORT)
      HavePropertyMatchResult(
        actualPort.equals(port),
        "port",
        port,
        actualPort)
    }
  }

  def relation(relationName: String) = new HavePropertyMatcher[Results, String] {
    def apply(result: Results) = {
      HavePropertyMatchResult(
        result.instances.relationName.equals(relationName),
        "relationName",
        relationName,
        result.instances.relationName)
    }
  }

  def attribute(attributeName: String) = new HavePropertyMatcher[Results, String] {
    def apply(result: Results) = {
      val inst = result.instances

      HavePropertyMatchResult(
        inst.attribute(attributeName) != null,
        "attributes",
        attributeName,
        "Attribute not found")
    }
  }

  /* ========================================== */
  /* ================ BeMatcher =============== */
  /* ========================================== */

  val aResult = new ResultsMatcher
  
  class ResultsMatcher extends BeMatcher[Event] {
    def apply(left: Event) =
      MatchResult(
        left.isInstanceOf[Results],
        left.toString + " is instance of Results.",
        left.toString + " is not an instance of Results.")
  }
  
  

}



object EventMatchers extends EventMatchers