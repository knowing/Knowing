package de.lmu.ifi.dbs.knowing.test

import org.scalatest._
import org.scalatest.matchers._
import weka.core.{ Instances, Instance, Attribute }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties._
import de.lmu.ifi.dbs.knowing.core.model.IEdge.DEFAULT_PORT
import java.util.Date
import java.util.Arrays

/**
 *
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait EventMatchers extends ShouldMatchers {

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

  /**
   * The list can contain the following types
   *
   * <li> Int, Long, Double, Float for numeric attributes<li>
   * <li> String for nominal attributes <li>
   * <li> Date for date attributes <li>
   *
   * @param content - List of type Any
   */
  def instance(content: List[Any]) = new HavePropertyMatcher[Results, String] {
    def apply(result: Results): HavePropertyMatchResult[String] = {
      val instances = result.instances
      if (instances.numAttributes != content.size) {
        return HavePropertyMatchResult(
          false,
          "instance",
          listToString(content),
          "[Header not equal. Wrong number of attributes]")
      }
      //this could perform better `content.view.zipWithIndex`
      val contentArray = content.zipWithIndex.map {
        case (x: Double, _) => x
        case (x: Float, _) => x.toDouble
        case (x: Long, _) => x.toDouble
        case (x: Int, _) => x.toDouble
        case (nom: String, index) =>
          val attr = instances.attribute(index)
          if (attr.`type` == Attribute.NUMERIC || attr.`type` == Attribute.DATE) {
            throw new Exception("String " + nom + " doesn't fit to attribute[ " + attributeToString(attr.`type`) + "]")
          }
          attr.indexOfValue(nom).toDouble
        case (date: Date, _) => date.getTime.toDouble
        case (c, index) => throw new Exception("No matching attribute for content type [" + c + "] at index [" + index + "]")
      }.toArray

      
      for (i <- 0 until instances.numInstances) {
        val inst = instances.get(i)
        val actual = inst.toDoubleArray
        if (Arrays.equals(actual, contentArray)) {
          return HavePropertyMatchResult(
            true,
            "instance",
            listToString(content),
            "")
        }
      }
      HavePropertyMatchResult(
        false,
        "instance",
        listToString(content),
        "<not available>")
    }
  }

  /* ===================== */
  /* === ResultsEvent ==== */
  /* ===================== */

  def name(relationName: String) = new HavePropertyMatcher[ResultsEvent, String] {

    def apply(result: ResultsEvent) = {
      val names = instancesList(result) map (_.relationName)
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
      val inst = instancesList(result)
      HavePropertyMatchResult(
        inst.exists(_.attribute(attributeName) != null),
        "attributes",
        attributeName,
        "Attribute not found")
    }
  }

  /**
   * Check if attribute with given type exists or if
   * a specific number exists. Default is existence.
   *
   * @param attributeType - weka.core.Attribute.{NUMERIC, NOMINAL, STRING, DATE, RELATIONAL}
   * @param numAttr - 0 if exists, >0 specific number
   */
  def attribute(attributeType: Int, numAttr: Int = 0) = new HavePropertyMatcher[ResultsEvent, String] {
    def apply(result: ResultsEvent) = {
      val inst = instancesList(result)
      numAttr match {
        case 0 =>
          val exists = inst.exists { inst =>
            //contains all indices of attributes with type attributeType
            val ret = for (i <- 0 until inst.numAttributes if (inst.attribute(i).`type`.equals(attributeType))) yield i
            !ret.isEmpty
          }
          HavePropertyMatchResult(
            exists,
            "attributeType",
            attributeToString(attributeType),
            "Attribute type not found")
        case x if x > 0 =>
          val occurences = inst.map { inst =>
            //contains all indices of attributes with type attributeType
            for (i <- 0 until inst.numAttributes if (inst.attribute(i).`type`.equals(attributeType))) yield i
          }

          //Split occurrences in (ok, too many/not enough attributes)
          occurences.partition(seq => seq.length == x) match {
            //okay
            case (_, Nil) =>
              HavePropertyMatchResult(
                true,
                "attributeType[" + attributeToString(attributeType) + "]",
                numAttr.toString,
                "Correct amount of attributes")

            //too many or not enough attributes of type attributeType
            case (Nil, attrIndices) =>
              HavePropertyMatchResult(
                false,
                "attributeType[" + attributeToString(attributeType) + "]",
                numAttr.toString,
                attrIndices.head.length.toString)

          }

        //Negative amount of attributes
        case _ => throw new Exception("Invalid amount of attributes (negative)")
      }
    }
  }

  private def instancesList(result: ResultsEvent) = result match {
    case Results(inst, port) => List(inst)
    case QueryResults(inst, query) => List(inst)
    case QueriesResults(results) => results.values.toList
  }

  private def attributeToString(attrType: Int) = attrType match {
    case Attribute.NUMERIC => "numeric"
    case Attribute.NOMINAL => "nominal"
    case Attribute.STRING => "string"
    case Attribute.DATE => "date"
    case Attribute.RELATIONAL => "relational"
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

  /* ========================================== */
  /* =========== Custom Matchers ============== */
  /* ========================================== */

  private def listToString(list: List[Any]) = list.toString.substring(5, list.toString.size - 1)

}

object EventMatchers extends EventMatchers