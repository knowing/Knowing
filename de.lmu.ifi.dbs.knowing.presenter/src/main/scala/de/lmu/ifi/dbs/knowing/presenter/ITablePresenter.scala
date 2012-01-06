package de.lmu.ifi.dbs.knowing.presenter

import java.util.Properties
import java.text.{ DateFormat, SimpleDateFormat, DecimalFormat }
import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.appendInstances
import weka.core.{ Attribute, Instances }
import weka.core.Attribute.{ NUMERIC, NOMINAL, DATE, RELATIONAL }

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @see for Java usage: http://www.codecommit.com/blog/java/interop-between-java-and-scala
 */
trait ITablePresenter[T] extends TPresenter[T] {
  import IFormatConstants._
  import ITablePresenter._

  protected var dateFormat = DateFormat.getDateTimeInstance
  protected var decimalFormat = new DecimalFormat

  protected var header: Instances = _
  protected var attributes: Array[Attribute] = _

  protected var content: Instances = _

  protected var rowsPerPage = 100
  protected var page = 0

  /**
   * @param attributes - contain header information
   */
  def buildTableHeader(attributes: Array[Attribute])

  /**
   * @param content - string values containing column content
   */
  def addRow(content: Array[String])

  /**
   * Init header and add rows
   */
  def buildPresentation(instances: Instances) {
    //Create header or add content to existing model
    isBuild match {
      case false =>
        header = new Instances(instances, 0)
        content = instances
        attributes = { for (i <- 0 until instances.numAttributes) yield instances.attribute(i) }.toArray
        buildTableHeader(attributes)
      case true =>
        appendInstances(content, instances)
    }

    for (i <- 0 until Math.min(rowsPerPage,instances.numInstances)) {
      val inst = instances.get(i)
      val row = attributes map {
        case a if a.`type`.equals(NUMERIC) => decimalFormat.format(inst.value(a))
        case a if a.`type`.equals(NOMINAL) => a.value(inst.value(a).toInt)
        case a if a.`type`.equals(DATE) => dateFormat.format(inst.value(a)) //ignores ARFF date format
        case a if a.`type`.equals(RELATIONAL) => "relational attribute"
      }
      addRow(row)
    }
  }

  /**
   * Initializes the decimal and date formatter.
   * Could be overridden by clients
   */
  def configure(properties: Properties) = {
    val datePattern = properties.getProperty(DATE_PATTERN, DATE_PATTERN_DEFAULT)
    dateFormat = new SimpleDateFormat(datePattern)
    val decimalPattern = properties.getProperty(DECIMAL_PATTERN, DECIMAL_PATTERN_DEFAULT)
    decimalFormat = new DecimalFormat(decimalPattern)

    rowsPerPage = properties.getProperty(ROWS_PER_PAGE, "100").toInt
  }

}

object ITablePresenter {

  val ROWS_PER_PAGE = "rows"
}