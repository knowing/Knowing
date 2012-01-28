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

  /* ========================================== */
  /* ========== HaveProperty Matcher ========== */
  /* ========================================== */

  /* ===================== */
  /* ====== Results ====== */
  /* ===================== */

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

  def name(relationName: String) = new HavePropertyMatcher[ResultsEvent, String] {

    def apply(result: ResultsEvent) = {
      val names = result match {
        case Results(inst, port) => List(inst.relationName)
        case QueryResults(inst, query) => List(inst.relationName)
        case QueriesResults(results) => Nil //TODO implement name have matcher
      }
      val results = names.find(_.equals(relationName))
      HavePropertyMatchResult(
        results.isDefined,
        "relationName",
        relationName,
        "Found: " + results)
    }

  }

  def attribute(attributeName: String) = new HavePropertyMatcher[ResultsEvent, String] {
    def apply(result: ResultsEvent) = {
      val inst = result match {
        case Results(inst, port) => List(inst)
        case QueryResults(inst, query) => List(inst)
        case QueriesResults(results) => Nil //TODO implement attribute have matcher
      }

      HavePropertyMatchResult(
        inst.exists(_.attribute(attributeName) != null),
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