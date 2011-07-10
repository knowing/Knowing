package de.lmu.ifi.dbs.knowing.core.graph

import scala.annotation.target.field
import javax.xml.bind.annotation.{ XmlAccessorType, XmlAccessType, XmlAttribute }

@XmlAccessorType(XmlAccessType.FIELD)
class Edge(@(XmlAttribute @field) var id: String,
  @(XmlAttribute @field) var sourceId: String,
  @(XmlAttribute @field) var targetId: String,
  @(XmlAttribute @field) var weight: Int) extends Serializable {

  def this(id: String, sourceId: String, targetId: String) = this(id, sourceId, targetId, 1)

  def this() = this("", "", "", 0)

  def getPlainSourceId = sourceId.split(':')(0)
  def getPlainTargetId = targetId.split(':')(0)

  def setPlainSourceId(id: String) {
    hasSourcePort match {
      case true => sourceId = id + ":" + getSourcePort
      case false => sourceId = id
    }
  }

  def setPlainTargetId(id: String) {
    hasTargetPort match {
      case true => targetId = id + ":" + getTargetPort
      case false => targetId = id
    }
  }

  def getSourcePort: String = {
    if (hasSourcePort) return sourceId.split(':')(1)
    return ""
  }

  def getTargetPort: String = {
    if (hasTargetPort) return targetId.split(':')(1)
    return ""
  }

  def hasSourcePort: Boolean = sourceId contains (':')
  def hasTargetPort: Boolean = targetId contains (':')

  def setSourcePort(port: String) {
    port match {
      case null | "" => sourceId = getPlainSourceId
      case s: String => sourceId = getPlainSourceId + ":" + port
    }
  }
  def setTargetPort(port: String) {
    port match {
      case null | "" => targetId = getPlainTargetId
      case s: String => targetId = getPlainTargetId + ":" + port
    }
  }

  override def toString() = id + "[" + sourceId + " -> " + targetId + "]"

}