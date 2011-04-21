package de.lmu.ifi.dbs.knowing.core.graph.xml

import DataProcessingUnit._

import de.lmu.ifi.dbs.knowing.core.graph._
import scala.annotation.target.field
import javax.xml.bind.annotation._

@XmlRootElement(name = "DataProcessingUnit")
@XmlAccessorType(XmlAccessType.FIELD)
class DataProcessingUnit(@XmlAttributeField var name: String,
  @XmlElementField var description: String,
  @XmlElementField var tags: String) {

  @XmlElementWrapper(name = "nodes")
  @XmlElement(name = "node")
  var nodes: Array[PersistentNode] = Array()

  @XmlElementWrapper(name = "edges")
  @XmlElement(name = "edge")
  var edges: Array[Edge] = Array()

  def this() = this("", "", "")

  def this(name: String) = this(name, "", "")

  def addTag(tag: String) = tags += "," + tag

  def addNode(node: Node) = {
    var list = nodes.toList
    list = new PersistentNode(node) :: list
    nodes = list.toArray
  }

}

object DataProcessingUnit {
  type XmlAttributeField = XmlAttribute @field
  type XmlElementField = XmlElement @field
}