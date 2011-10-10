package de.lmu.ifi.dbs.knowing.core.util

import java.net.URL
import java.util.Properties
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.propertiesAsScalaMap
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.model.NodeType
import de.lmu.ifi.dbs.knowing.core.service.IDPUDirectory
import java.io.OutputStream
import java.io.PrintWriter

/**
 * 
 */
object DPUUtil {

  /**
   * Creates a new IDataProcessingUnit instance. Isn't hook to any ressource.
   */
  def copy(dpu: IDataProcessingUnit): IDataProcessingUnit = {
    val destination = IDataProcessingUnit.TYPE.instantiate.asInstanceOf[IDataProcessingUnit]
    copy(dpu, destination)
  }

  /**
   *
   * @param source -> copy values from here
   * @param destination -> paste values in here
   * @return destination
   */
  def copy(source: IDataProcessingUnit, destination: IDataProcessingUnit): IDataProcessingUnit = {
    destination.setName(source.getName.getContent)
    destination.setDescription(source.getDescription.getContent)
    destination.setTags(source.getTags.getContent)

    val destConf = destination.getConfiguration
    val srcConf = source.getConfiguration
    destConf.setHistory(srcConf.getHistory.getContent)
    destConf.setAbsolute(srcConf.getAbsolute.getContent)
    destConf.setOutput(srcConf.getOutput.getContent)
    srcConf.getconstraints.foreach { c =>
      val newConstr = destConf.getconstraints.addNewElement
      newConstr.setType(c.getType.getContent)
      newConstr.setLog(c.getLog.getContent)
    }
    
    source.getNodes.foreach { node =>
      val nodeNew = destination.getNodes.addNewElement
      nodeNew.setId(node.getId.getContent)
      nodeNew.setFactoryId(node.getFactoryId.getText)
      nodeNew.setType(node.getType.getText)
      node.getProperties.foreach { p =>
      	val newProp = nodeNew.getProperties.addNewElement
      	newProp.setKey(p.getKey.getContent)
      	newProp.setValue(p.getValue.getContent)
      }
    }
    source.getEdges.foreach {
      edge =>
        val edgeNew = destination.getEdges.addNewElement
        edgeNew.setId(edge.getId.getContent)
        edgeNew.setSource(edge.getSource.getContent)
        edgeNew.setSourcePort(edge.getSourcePort.getContent)
        edgeNew.setTarget(edge.getTarget.getContent)
        edgeNew.setTargetPort(edge.getTargetPort.getContent)
    }
    destination
  }

  def getDPU(directory: IDPUDirectory, id: String): IDataProcessingUnit = directory.getDPU(id).getOrElse(null)
  def getDPUPath(directory: IDPUDirectory, id: String): URL = directory.getDPUPath(id).getOrElse(null)

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