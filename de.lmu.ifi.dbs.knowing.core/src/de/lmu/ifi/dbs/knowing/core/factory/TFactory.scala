package de.lmu.ifi.dbs.knowing.core.factory

import java.util.Properties
import akka.actor.ActorRef

trait TFactory {

  val name: String
  val id: String

  def getInstance(): ActorRef
}