package de.lmu.ifi.dbs.knowing.core.events

import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.graph.Node
import akka.actor.ActorRef
import weka.core.Instances
import weka.core.Instance

import java.util.Properties

sealed trait Event

/* ======================== */
/* == Data Exchange ======= */
/* ======================== */
case class Results(instances: Instances) extends Event
case class QueryResults(instances: Instances, query: Instance )extends Event
case class Query(query: Instance) extends Event
case class Queries(queries: Instances) extends Event
case class UIContainer(container: AnyRef) extends Event
case class UIFactoryEvent(factory: UIFactory, node: Node) extends Event

/* ======================== */
/* == Runtime Commands ==== */
/* ======================== */
case class Configure(properties: Properties) extends Event
case class Start extends Event
case class Reset extends Event
case class Ready extends Event
case class Finished extends Event

/* ========================= */
/* == Actor Communication == */
/* ========================= */
case class Register(actor: ActorRef) extends Event
case class Registered(success: Boolean) extends Event