package de.lmu.ifi.dbs.knowing.core.graph

import scala.collection.immutable.HashMap
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit

class GraphValidator(nodes: Array[_<: Node], edges: Array[Edge]) {

  val errors = new HashMap[String, String]

  private val targetNodes = (edges: Array[Edge]) => edges map (e => e.sourceId)
  private val sourceNodes = (edges: Array[Edge]) => edges map (e => e.targetId)

  def this(dpu: DataProcessingUnit) = this(dpu.nodes, dpu.edges)

  def validate: Boolean = {
    true
  }

  def hasCircle: Boolean = {
    hasCircle(edges, edges.length);
  }

  def hasCircle(edges: Array[Edge], size: Int): Boolean = {

    val targetIds = sourceNodes(edges) distinct
    val sourceIds = targetNodes(edges) distinct

    val targetIdExists = (id: String) => targetIds exists (targetId => targetId.equals(id))
    val sourceIdExists = (id: String) => sourceIds exists (sourceId => sourceId.equals(id))

    val nonRootEdges = edges filter (e => targetIdExists(e.sourceId))
    val nonLeafEdges = edges filter (e => sourceIdExists(e.targetId))

    val edgesLeft = nonRootEdges intersect nonLeafEdges distinct;
    val length = edgesLeft.length
    size match {
      case 0 => false
      case x => if (x == length) true
       else hasCircle(edgesLeft, length)

    }
  }
}

object GraphValidator {

  def validate(dpu: DataProcessingUnit): Boolean = true

  def validate(nodes: Array[Node], edges: Array[Edge]): Boolean = true
}