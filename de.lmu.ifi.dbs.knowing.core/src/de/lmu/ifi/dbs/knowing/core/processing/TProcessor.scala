package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.Actor
import weka.core.Instances
import weka.core.Instance

import de.lmu.ifi.dbs.knowing.core.events._

trait TProcessor extends Actor {

  def receive = {
    case Results(instances) => build(instances)
    case Query(q) => query(q)
    case Start(p) => log.info("Running with properties: " + p)
    case msg => log.info("Unkown message: " + msg)
  }
  
  def build(instances: Instances)
	  
  def query(query: Instance)
}