package de.lmu.ifi.dbs.knowing.core.graph

import scala.annotation.target.field
import javax.xml.bind.annotation.{ XmlAccessorType, XmlAccessType, XmlAttribute }

@XmlAccessorType(XmlAccessType.FIELD)
class Edge(@(XmlAttribute @field) var id: String,
  @(XmlAttribute @field) var sourceId: String,
  @(XmlAttribute @field) var targetId: String,
  @(XmlAttribute @field) var weight: Int) extends Serializable {
  
  def this(id:String, sourceId:String, targetId:String) = this(id, sourceId, targetId, 1)
  
  def this() = this("","","",0)
  
  override def toString() = id + "[" + sourceId + " -> " + targetId + "]"

}