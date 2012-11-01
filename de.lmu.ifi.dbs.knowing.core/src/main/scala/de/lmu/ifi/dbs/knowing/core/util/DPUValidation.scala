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
package de.lmu.ifi.dbs.knowing.core.util

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.internal.Activator
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import DPUUtil._
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-09
 */
object DPUValidation {

  def compiletime(dpu: IDataProcessingUnit): Validation = new CompiletimeValidation(dpu)

  def runtime(dpu: IDataProcessingUnit): Validation = new RuntimeValidation(dpu)
}

sealed trait Validation {

  def hasErrors(): Boolean = getErrors.nonEmpty

  def getErrors(): List[String]

  def hasWarnings(): Boolean = getWarnings.nonEmpty

  def getWarnings(): List[String]
}

class CompiletimeValidation(val dpu: IDataProcessingUnit) extends Validation {

  private val errors = ListBuffer[String]()
  private val warnings = ListBuffer[String]()

  //Start validation
  //TODO ticket #39 first
  //1. Unused parameters == warning

  //2. IO/Presenter nodes == warning
  if (loaderNodes(dpu).isEmpty)
    warnings += "No loaders found!"

  val saver = saverNodes(dpu)
  val presenter = presenterNodes(dpu)
  if (saver.isEmpty && presenter.isEmpty)
    warnings += "No savers or presenters found!"

  //3. Using Presenter API == warning
  presenter.foreach { node =>
    if (!node.getFactoryId.getText.startsWith("de.lmu.ifi.dbs.knowing.presenter"))
      warnings += "Node " + node.getId.getContent + " doesn't use the Presenter API"
  }

  //4. Unique Edge/Node ids == error
  val nodeIds = dpu.getNodes.map(_.getId.getContent)
  if (nodeIds.length != nodeIds.distinct.length)
    errors += "Ambigious node ids"

  val edgeIds = dpu.getEdges.map(_.getId.getContent)
  if (edgeIds.length != edgeIds.distinct.length)
    errors += "Ambigious edge ids"

  //5. Cycles == error
  //TODO implement cycle detection DPU

  def getErrors(): List[String] = errors.toList

  def getWarnings(): List[String] = warnings.toList
}

class RuntimeValidation(val dpu: IDataProcessingUnit, withCompiletime: Boolean = true) extends Validation {

  private val errors = ListBuffer[String]()
  private val warnings = ListBuffer[String]()

  //Start validation

  //0. Compiltime Validation
  if (withCompiletime) {
    val compiletime = DPUValidation.compiletime(dpu)
    errors ++= compiletime.getErrors
    warnings ++= compiletime.getWarnings
  }

  //1. Check if factories exist
  dpu.getNodes.filter { node =>
    val filter = "(" + TFactory.FACTORY_ID + "=" + node.getFactoryId.getText + ")"
    val references = Activator.getContext.getServiceReferences(classOf[TFactory], filter)
    references.isEmpty
  }.foreach { node =>
    errors += "No factory  for node " + node.getId.getText + " with processor " + node.getFactoryId.getText
  }

  //TODO ticket #39 first 
  //2. Parameters are fully set

  def getErrors(): List[String] = errors.toList

  def getWarnings(): List[String] = warnings.toList

}