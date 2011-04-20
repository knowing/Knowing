package de.lmu.ifi.dbs.knowing.core.graph.xml

import de.lmu.ifi.dbs.knowing.core.graph._
import scala.annotation.target.field
import javax.xml.bind.annotation._
import DataProcessingUnit._

@XmlRootElement(name = "DataProcessingUnit")
@XmlAccessorType(XmlAccessType.FIELD)
class DataProcessingUnit(@XmlAttributeField var name: String,
    @XmlElementField var description: String,
    @XmlElementField var tags: String,
    @XmlElementWrapperField var nodes: Array[Node],
    @XmlElementWrapperField var edges: Array[Edge]) {

	def this() = this("","","", new Array(0), new Array(0))
	
	def addTag(tag:String) = tags += "," + tag
  
}

object DataProcessingUnit {
  type XmlAttributeField = XmlAttribute @field
  type XmlElementField = XmlElement @field
  type XmlElementWrapperField = XmlElementWrapper @field
}