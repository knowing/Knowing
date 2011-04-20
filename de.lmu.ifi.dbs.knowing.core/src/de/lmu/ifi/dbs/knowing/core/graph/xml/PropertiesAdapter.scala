package de.lmu.ifi.dbs.knowing.core.graph.xml

import java.util.ArrayList
import java.util.Properties
import javax.xml.bind.annotation.adapters.XmlAdapter

import scala.collection.JavaConversions._

class PropertiesAdapter extends XmlAdapter[Array[Property], Properties] {

  def unmarshal(properties: Array[Property]): Properties = {
    val returns = new Properties
    properties foreach ( property => returns.setProperty(property.key, property.value))
    returns
  }

  def marshal(properties: Properties): Array[Property] = {
    val returns = new Array(10)
    val pMap = asMap(properties)
    pMap map { case (key, value) => new Property(key, value) } toArray
  }

}