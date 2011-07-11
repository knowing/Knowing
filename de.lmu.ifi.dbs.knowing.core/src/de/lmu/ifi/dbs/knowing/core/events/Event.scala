package de.lmu.ifi.dbs.knowing.core.events

import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.graph.Node
import akka.actor.ActorRef
import weka.core.Instances
import weka.core.Instance

import java.util.Properties

trait Event
trait Status extends Event

/* ======================== */
/* == Data Exchange ======= */
/* ======================== */
case class Results(instances: Instances) extends Event
case class QueryResults(instances: Instances, query: Instance )extends Event
case class Query(query: Instance) extends Event
case class Queries(queries: Instances) extends Event
case class UIFactoryEvent(factory: UIFactory, node: Node) extends Event

/* ======================== */
/* == Status Commands ===== */
/* ======================== */
case class Created extends Status	//processor created
case class Waiting extends Status	//waiting for first messages
case class Ready extends Status		//process already one message and is ready to do more
case class Running extends Status	//is currently running
case class Progress(task:String, worked:Int, work:Int) extends Status
case class Finished extends Status	//all work is done, not ready for more messages

/* ======================== */
/* == Runtime Commands ==== */
/* ======================== */
case class Start extends Event
case class Reset extends Event
case class Alive extends Event
case class Configure(properties: Properties) extends Event
case class UpdateUI extends Status

/* ========================= */
/* == Actor Communication == */
/* ========================= */
case class Register(actor: ActorRef, port:Option[String]) extends Event
case class Registered(success: Boolean) extends Event