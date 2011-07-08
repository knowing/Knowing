package de.lmu.ifi.dbs.knowing.core.graph

import scala.annotation.target.field
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import javax.xml.bind.annotation.{ XmlAccessorType, XmlAccessType, XmlAttribute }
import java.util.Properties

import de.lmu.ifi.dbs.knowing.core.graph.xml.PropertiesAdapter


/**
 * <p> A node represents a loader, processor or presenter in a <br>
 * DataProcessingUnit and a individual configuration</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 18.04.2011
 * 
 */
trait Node extends Serializable {
  var id: String
  var factoryId: String
  var nodeType: String
  var properties: Properties
}

case class LoaderNode(var id: String, var factoryId: String, var properties: Properties) extends Node {
  var nodeType = Node.LOADER
}
case class PresenterNode(var id: String, var factoryId: String, var properties: Properties) extends Node {
  var nodeType = Node.PRESENTER
}
case class ProcessorNode(var id: String, var factoryId: String, var properties: Properties) extends Node {
  var nodeType = Node.PROCESSOR
}

case class SaverNode(var id: String, var factoryId: String, var properties: Properties) extends Node {
  var nodeType = Node.SAVER
}

@XmlAccessorType(XmlAccessType.FIELD)
case class PersistentNode(@(XmlAttribute @field) var id: String,
  @(XmlAttribute @field) var factoryId: String,
  @(XmlAttribute @field) var nodeType: String) extends Node {

  @XmlJavaTypeAdapter(classOf[PropertiesAdapter])
  var properties: Properties = new Properties

  def this() = this("", "", "")

  def this(node: Node) = {
    this(node.id, node.factoryId, node.nodeType)
    properties = node.properties
  }
}

object Node {
  val LOADER = "loader"
  val PROCESSOR = "processor"
  val PRESENTER = "presenter"
  val SAVER = "saver"
}
