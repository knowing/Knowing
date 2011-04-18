package de.lmu.ifi.dbs.knowing.core.events

import weka.core.Instances
import weka.core.Instance

import java.util.Properties

sealed trait Event 

case class Results(instances: Instances) extends Event
case class ModelResults(results: Instances) extends Event
case class Query(query: Instance) extends Event

case class Configure(properties:Properties) extends Event
case class Start(properties: Properties) extends Event
case class Reset extends Event
case class Ready extends Event
case class Finished extends Event

case class Register extends Event

