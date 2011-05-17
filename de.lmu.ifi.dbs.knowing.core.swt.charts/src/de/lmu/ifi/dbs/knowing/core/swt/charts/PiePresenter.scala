package de.lmu.ifi.dbs.knowing.core.swt.charts

import scala.collection.mutable.HashMap

import org.jfree.util.Rotation
import org.jfree.chart.plot.{ Plot, PiePlot3D }
import org.jfree.chart.{ ChartFactory, JFreeChart }
import org.jfree.data.general.{ Dataset, PieDataset, DefaultPieDataset }
import java.awt.{ Color, Font }
import java.util.Properties

import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import weka.core.{ Instances, Instance, Attribute }

import de.lmu.ifi.dbs.knowing.core.swt.charts.PiePresenter._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.{ guessClassIndex }
import de.lmu.ifi.dbs.knowing.core.factory.TFactory

/**
 * <p>
 * The PiePresenter can handle three types of instances:
 * <li>nominal class attribute only</li>
 * <li>nominal class attribute + probability</li>
 * <li>nominal class attribute (top probability) + probability per class label</li>
 * </p>
 * <p>
 * There should only be one nominal attribute, because if the dataset sets no<br>
 * class attribute, the PiePresenter will guess, which means he choose the first
 * nominal<br>
 * attribute he can find and treat (but not set!) this as the class attribute
 * </p>
 *
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 30.03.2011
 *
 */
class PiePresenter extends AbstractChartPresenter("Pie Presenter") {

  /** label -> probability || label -> count */
  val internal_model = new HashMap[String, Double]

  var model: Instances = _

  var num_instances = 0

  //TODO PiePresenter -> mode property
  var mode = -1

  /* ========================== */
  /* == Dataset manipulation == */
  /* ========================== */

  /**
   * Just sums the count of class labels
   *
   * @param dataset
   */
  private def updateModelClassOnly(dataset: Instances) = {
    if (mode != -1 || MODE_LABEL_ONLY != mode) {
      initModel(dataset)
      mode = MODE_LABEL_ONLY
    }

    val instances = dataset.enumerateInstances
    val attribute = dataset.attribute(0)
    while (instances.hasMoreElements) {
      val instance = instances.nextElement.asInstanceOf[Instance]
      num_instances += 1
      val index = instance.value(0).asInstanceOf[Int]
      val label = attribute.value(index)
      val count = internal_model.get(label)
      count match {
        case Some(x) => internal_model.put(label, x + 1)
        case None => internal_model.put(label, 0)
      }

    }

  }

  /**
   * <p>This method assumes that either
   * <li>each label appears only one time</li>
   * <li>
   * @param dataset
   * @param classIndex
   */
  private def updateModelClassProbability(dataset: Instances, classIndex: Int) {
    var tmpClassIndex = classIndex
    if (mode != -1 || MODE_LABEL_PROBABILITY != mode) {
      tmpClassIndex = initModel(dataset)
      mode = MODE_LABEL_PROBABILITY
    }

    val valIndex = findValueIndex(dataset)
    val instances = dataset.enumerateInstances()
    while (instances.hasMoreElements) {
      val instance = instances.nextElement.asInstanceOf[Instance]
      val label = instance.stringValue(classIndex)
      val value = instance.value(valIndex)
      val oldValue = internal_model.get(label)
      // assumes that probabilities always sum up to 100%
      val newValue = value + (oldValue getOrElse (0.0))
      internal_model.put(label, newValue)
    }
  }

  private def updateDataset {
    val pieset = dataset.asInstanceOf[DefaultPieDataset]
    pieset clear ()
    internal_model foreach { case (key, propability) => pieset.setValue(key, propability) }
  }

  /**
   *
   * @param dataset
   * @return the first numeric index found, if non exists a negative value
   */
  private def findValueIndex(dataset: Instances): Int = {
    val attributes = dataset.enumerateAttributes()
    while (attributes.hasMoreElements) {
      val attribute = attributes.nextElement.asInstanceOf[Attribute]
      if (attribute.isNumeric())
        return attribute.index()
    }
    return -1
  }

  /**
   * <p>
   * Sets the distribution for all lables to zero<br>
   * to take effect call {@link #updateDataset()} and {@link #updateChart()}
   * </p>
   *
   * @param classAttribute - must be nominal
   */
  private def initModel(dataset: Instances): Int = {
    internal_model clear
    val classIndex = guessClassIndex(dataset)
    val classAttribute = dataset.attribute(classIndex)
    if (!classAttribute.isNominal())
      return classIndex

    val labels = classAttribute.enumerateValues()
    while (labels.hasMoreElements) {
      internal_model.put(labels.nextElement.asInstanceOf[String], 0.0)
    }
    num_instances = 0
    return classIndex
  }

  def buildContent(instances: Instances) = {
    var classIndex = -1
    if (internal_model.isEmpty)
      classIndex = initModel(instances)

    if (instances.numAttributes == 1)
      updateModelClassOnly(instances)
    else if (instances.numAttributes == 2)
      updateModelClassProbability(instances, classIndex)
    else {}
    //TODO PiePresenter -> Implement label_distribution input format

    updateDataset
    updateChart
    redraw
  }
  
  def configure(properties:Properties) = {}

  def getModel(labels: Array[String]): Instances = { null }

  /* ========================== */
  /* ===== Chart creation ===== */
  /* ========================== */

  protected def createChart(dataset: Dataset): JFreeChart = ChartFactory.createPieChart3D("Pie Chart", dataset.asInstanceOf[PieDataset], true, false, false)

  protected def createDataset(): Dataset = new DefaultPieDataset

  override protected def configurePlot(plot: Plot) = {
    val plot3D = plot.asInstanceOf[PiePlot3D]
    plot3D.setLabelFont(new Font("SansSerif", Font.PLAIN, 12))
    plot3D.setNoDataMessage("No data available")
    plot3D.setCircular(false)
    plot3D.setLabelGap(0.02)

    plot3D.setStartAngle(270)
    plot3D.setDirection(Rotation.ANTICLOCKWISE)
    plot3D.setForegroundAlpha(0.60f)
    plot3D.setBackgroundPaint(Color.white)
  }

}

object PiePresenter {
  val name = "Pie Presenter"
  
  val MODE_LABEL_ONLY = 0
  val MODE_LABEL_PROBABILITY = 1
  val MODE_LABEL_DISTRIBUTION = 2
}

class PiePresenterFactory extends TFactory {

  val name: String = PiePresenter.name
  val id: String = classOf[PiePresenter].getName

  def getInstance(): ActorRef = actorOf[PiePresenter]

  def createDefaultProperties: Properties = new Properties

  def createPropertyValues: Map[String, Array[Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}