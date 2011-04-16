package de.lmu.ifi.dbs.knowing.core.graph

import java.util.Properties

sealed trait Node

case class LoaderNode(id:String, factoryId:String, properties:Properties) extends Node
case class PresenterNode(id:String, factoryId:String, properties:Properties) extends Node
case class ProcessorNode(id:String, factoryId:String, properties:Properties) extends Node