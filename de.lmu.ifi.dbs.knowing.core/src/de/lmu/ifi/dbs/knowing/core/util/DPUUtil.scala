package de.lmu.ifi.dbs.knowing.core.util

import de.lmu.ifi.dbs.knowing.core.model._
import scala.collection.JavaConversions._
import java.util.Properties

object DPUUtil {

  /* =========================== */
  /* ==== Node util methods ==== */
  /* =========================== */

  /**
   *
   */
  def nodesOfType(typ: String, dpu: IDataProcessingUnit): Array[INode] = {
    dpu.getNodes.toList filter (node => node.getType.equals(typ)) toArray
  }

  /**
   *
   * @param typ - @see NodeType
   */
  def nodesOfType(typ: NodeType, dpu: IDataProcessingUnit): Array[INode] = nodesOfType(typ.name.toLowerCase, dpu)

  /**
   *
   */
  def loaderNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.LOADER, dpu)

  /**
   *
   */
  def presenterNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.PRESENTER, dpu)

  /**
   *
   */
  def processorNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.PROCESSOR, dpu)

  /**
   *
   */
  def node(typ: String, factory: String, dpu: IDataProcessingUnit): Array[INode] = {
    dpu.getNodes.toList filter (node => node.getType.equals(typ) && node.getFactoryId.equals(factory)) toArray
  }

  def nodeProperties(node: INode): Properties = {
    val props = new Properties
    node.getProperties.foldLeft(props) { (properties, p) =>
      properties.setProperty(p.getKey.getContent, p.getValue.getContent)
      properties
    }
  }

  /* =========================== */
  /* ==== Edge util methods ==== */
  /* =========================== */

}