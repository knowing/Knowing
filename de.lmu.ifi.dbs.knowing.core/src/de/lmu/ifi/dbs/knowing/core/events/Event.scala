package de.lmu.ifi.dbs.knowing.core.events

import java.util.Properties
import java.io.{InputStream,OutputStream}
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import weka.core.{Instance,Instances}

trait Event
trait Status extends Event
trait UIEvent extends Event

/* ======================== */
/* == Data Exchange ======= */
/* ======================== */
case class Results(instances: Instances, port: Option[String] = None) extends Event
case class QueryResults(instances: Instances, query: Instance) extends Event
case class QueriesResults(results: List[(Instance, Instances)]) extends Event //query, results
case class Query(query: Instance) extends Event
case class Queries(queries: Instances, id: String = "") extends Event
case class UIFactoryEvent(factory: UIFactory, node: INode) extends Event

/* ======================== */
/* == Status Commands ===== */
/* ======================== */
case class Created extends Status //processor created
case class Waiting extends Status //waiting for first messages
case class Ready extends Status //process already one message and is ready to do more
case class Running extends Status //is currently running
case class Progress(task: String, worked: Int, work: Int = 100) extends Status
case class Finished extends Status //all work is done, not ready for more messages
case class ExceptionEvent(throwable: Throwable, details: String) extends Status
case class Shutdown extends Status // UIfactory can shutdown, all done

/* ======================== */
/* == Runtime Commands ==== */
/* ======================== */
case class Start extends Event
case class Reset extends Event
case class Alive extends Event
case class Configure(properties: Properties) extends Event
case class ConfigureInput(source:String = ResultsUtil.UNKOWN_SOURCE, input: InputStream) extends Event
case class ConfigureOutput(target:String = ResultsUtil.UNKOWN_SOURCE, output: OutputStream) extends Event
case class UpdateUI extends Status with UIEvent

/* ========================= */
/* == Actor Communication == */
/* ========================= */
case class Register(actor: ActorRef, in: Option[String] = None, out: Option[String] = None) extends Event
case class Registered(success: Boolean = true) extends Event