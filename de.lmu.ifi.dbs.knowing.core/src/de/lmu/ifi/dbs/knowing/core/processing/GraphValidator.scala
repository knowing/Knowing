package de.lmu.ifi.dbs.knowing.core.processing

import scala.collection.immutable.HashMap
import scala.collection.JavaConversions._
import de.lmu.ifi.dbs.knowing.core.model._

/**
 * This class will provide validation methods to detect:
 * [1] circle paths inside the graph, which can result in infinite loops
 * [2] wrong configuration parameters
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 */
class GraphValidator(nodes: List[INode], edges: List[IEdge]) {

  private val errors = new HashMap[String, String]

  private val targetNodes = (edges: List[IEdge]) => edges map (e => e.getSource.getText)
  private val sourceNodes = (edges: List[IEdge]) => edges map (e => e.getTarget.getText)

  def this(dpu: IDataProcessingUnit) = this(dpu.getNodes.toList, dpu.getEdges.toList)

  def validate: Boolean = {
    true
  }

  def hasCircle: Boolean = {
    hasCircle(edges, edges.length);
  }

  def hasCircle(edges: List[IEdge], size: Int): Boolean = {

    val targetIds = sourceNodes(edges) distinct
    val sourceIds = targetNodes(edges) distinct

    val targetIdExists = (id: String) => targetIds exists (targetId => targetId.equals(id))
    val sourceIdExists = (id: String) => sourceIds exists (sourceId => sourceId.equals(id))

    val nonRootEdges = edges filter (e => targetIdExists(e.getSource.getText))
    val nonLeafEdges = edges filter (e => sourceIdExists(e.getTarget.getText))

    val edgesLeft = nonRootEdges intersect nonLeafEdges distinct;
    val length = edgesLeft.length
    size match {
      case 0 => false
      case x => if (x == length) true
       else hasCircle(edgesLeft, length)

    }
  }
  
  def getErrors:Array[String] = errors map {case (k,v) => k + ": " + v} toArray
}

object GraphValidator {

  def validate(dpu: IDataProcessingUnit): Boolean = true

  def validate(nodes: List[INode], edges: List[IEdge]): Boolean = true
}