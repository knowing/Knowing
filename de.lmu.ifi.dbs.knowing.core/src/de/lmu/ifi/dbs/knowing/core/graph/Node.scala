package de.lmu.ifi.dbs.knowing.core.graph

import scala.annotation.target.field
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import javax.xml.bind.annotation.{ XmlAccessorType, XmlAccessType, XmlAttribute }
import java.util.Properties

import de.lmu.ifi.dbs.knowing.core.graph.xml.PropertiesAdapter

sealed trait Node {
  var id: String
  var factoryId: String
}

case class LoaderNode(var id: String, var factoryId: String, properties: Properties) extends Node
case class PresenterNode(var id: String, var factoryId: String, properties: Properties) extends Node
case class ProcessorNode(var id: String, var factoryId: String, properties: Properties) extends Node

@XmlAccessorType(XmlAccessType.FIELD)
case class PersistentNode(@(XmlAttribute @field) var id: String, 
    @(XmlAttribute @field) var factoryId: String,
    @(XmlAttribute @field) var nodeType: String) extends Node {
  
  @XmlJavaTypeAdapter(classOf[PropertiesAdapter]) 
  var properties: Properties = _
  
  def this() = this("", "", "")
}
    
