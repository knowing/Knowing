package de.lmu.ifi.dbs.knowing.core.graph.xml

import scala.annotation.target.field
import javax.xml.bind.annotation.{ XmlAccessorType, XmlAccessType, XmlAttribute }

@XmlAccessorType(XmlAccessType.FIELD)
class Property(@(XmlAttribute @field) var key: String,
  @(XmlAttribute @field) var value: String) {

  def this() = this("", "")

  override def toString(): String = key + " => " + value

}