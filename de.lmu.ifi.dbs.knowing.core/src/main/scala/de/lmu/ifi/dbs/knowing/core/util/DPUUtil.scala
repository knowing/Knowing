package de.lmu.ifi.dbs.knowing.core.util

import java.net.URL
import java.util.Properties
import scala.collection.JavaConversions._
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.model.NodeType
import de.lmu.ifi.dbs.knowing.core.service.IDPUDirectory
import java.io.OutputStream
import java.io.PrintWriter

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
object DPUUtil {

  /**
   * Creates a new IDataProcessingUnit instance. Isn't hooked to any ressource.
   */
  def copy(dpu: IDataProcessingUnit): IDataProcessingUnit = {
    val destination = IDataProcessingUnit.TYPE.instantiate.asInstanceOf[IDataProcessingUnit]
    copy(dpu, destination)
  }

  /**
   * Eclipse Sapphire now offers clone support all models.
   *
   * @param source -> copy values from here
   * @param destination -> paste values in here
   * @return destination
   */
  @deprecated
  def copy(source: IDataProcessingUnit, destination: IDataProcessingUnit): IDataProcessingUnit = {
    destination.copy(source)
    destination
  }

  def getDPU(directory: IDPUDirectory, id: String): IDataProcessingUnit = directory.getDPU(id).getOrElse(null)
  def getDPUPath(directory: IDPUDirectory, id: String): URL = directory.getDPUPath(id).getOrElse(null)

  /**
   * Simple debugging method. Prints DPU to given outputStream
   */
  def print(dpu: IDataProcessingUnit, out: OutputStream = System.out) {
    val writer = new PrintWriter(out)
    if (dpu == null) {
      writer.println("DataProcessingUnit: null")
      writer.flush
      return
    }

    writer.println("### [Data Processing Unit] " + dpu.getName.getContent + " ###")
    writer.println("Description: " + dpu.getDescription.getContent)
    writer.println("Tags: " + dpu.getTags.getContent)
    writer.println("== Configuration: ")
    val conf = dpu.getConfiguration
    writer.println(" History: " + conf.getHistory.getContent)
    writer.println(" Path/Absolute: " + conf.getOutput.getText + " / " + conf.getAbsolute.getContent)
    writer.println(" === Node constraints( " + conf.getNodeConstraints.size + " )")
    conf.getNodeConstraints.foreach { node =>
      writer.println("  Node/Log " + node.getNode.getContent + " / " + node.getLog.getContent)
    }

    writer.println(" === Event constraints( " + conf.getEventConstraints.size + " )")
    conf.getEventConstraints.foreach { e =>
      writer.println("  Node/Log " + e.getType.getContent + " / " + e.getLog.getContent)
    }

    writer.println("== Nodes(" + dpu.getNodes.size + ") ==")
    dpu.getNodes.foreach {
      node =>
        writer.println(" ==== " + node.getId.getText + " ==== ")
        writer.println(" [FactoryId] " + node.getFactoryId.getText)
        writer.println(" [Type] " + node.getType.getText)
        writer.println(" [Properties(" + node.getProperties.size + ")]")
        node.getProperties.foreach {
          p => writer.println("  [x] " + p.getKey.getContent + " -> " + p.getValue.getContent)
        }
    }

    writer.println("== Edges(" + dpu.getEdges.size + ") ==")
    dpu.getEdges.foreach {
      edge =>
        writer.println(" ==== " + edge.getId.getText + " ==== ")
        writer.println(" [Source] " + edge.getSource.getText + ":" + edge.getSourcePort.getText)
        writer.println(" [Target] " + edge.getTarget.getText + ":" + edge.getTargetPort.getText)
    }

    writer.flush
  }

  /* =========================== */
  /* ==== Node util methods ==== */
  /* =========================== */

  /**
   *
   * @param typ - @see NodeType
   */
  def nodesOfType(typ: NodeType, dpu: IDataProcessingUnit): Array[INode] = {
    dpu.getNodes filter (node => node.getType.getContent.equals(typ)) toArray
  }

  /**
   *
   */
  def loaderNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.LOADER, dpu)

  /**
   *
   */
  def saverNodes(dpu: IDataProcessingUnit): Array[INode] = nodesOfType(NodeType.SAVER, dpu)

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
    dpu.getNodes filter (node => node.getType.equals(typ) && node.getFactoryId.equals(factory)) toArray
  }

  /**
   * @param id - node ID
   * @return node or null
   */
  def nodeById(id: String, dpu: IDataProcessingUnit): INode = nodeByIdOption(id, dpu) match {
    case Some(node) => node
    case None => null
  }

  /**
   * @param id - node ID
   * @return Some(node) or None
   */
  def nodeByIdOption(id: String, dpu: IDataProcessingUnit): Option[INode] = {
    dpu.getNodes find (node => node.getId.getContent.equals(id))
  }

  def nodeProperties(node: INode): Properties = node.getProperties.foldLeft(new Properties) {
    (properties, p) =>
      properties.setProperty(p.getKey.getContent, p.getValue.getContent)
      properties
  }

  def setNodeProperties(node: INode, properties: Properties) = {
    val props = node.getProperties
    props.clear
    properties.foreach {
      case (key, value) =>
        val property = props.addNewElement
        property.setKey(key)
        property.setValue(value)
    }
  }

  /* =========================== */
  /* ==== Edge util methods ==== */
  /* =========================== */

}
