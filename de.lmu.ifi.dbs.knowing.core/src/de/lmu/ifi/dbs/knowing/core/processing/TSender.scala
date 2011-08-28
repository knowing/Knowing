package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.{ Actor, ActorRef }
import akka.event.EventHandler.{ debug, info, warning, error }
import com.eaio.uuid.UUID
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import de.lmu.ifi.dbs.knowing.core.events._
import TSender._
import weka.core.Instances

/**
 * <p>This actor is able to send Events to registered actors</p>
 */
trait TSender { this: Actor =>

  /**
   * OUTPUT_PORT -> INPUT_PORT > Map[UUID -> Actor]
   *
   */
  val listeners = Map.empty[String, Map[UUID, (ActorRef, Set[String])]]
  //Add default ports
  listeners += (DEFAULT_PORT -> Map())

  /* ================================= */
  /* == Register listeners to ports == */
  /* ================================= */

  def register(listener: ActorRef, output: Option[String], input: Option[String]) {
    val uuid = listener.getUuid
    (output, input) match {
      //Default input/output == no ports
      case (None, None) =>
        listeners(DEFAULT_PORT).get(uuid) match {
          //Listener is registered, just add new port
          case Some(entry) => entry._2 += DEFAULT_PORT
          //Listener isn't registered yet
          case None => listeners(DEFAULT_PORT) += (uuid -> (listener, Set(DEFAULT_PORT)))
        }

      //Only output port defined
      case (Some(out), None) =>
        listeners.get(out) match {
          case Some(outputs) =>
            outputs.get(uuid) match {
              case Some(entry) => entry._2 += DEFAULT_PORT
              case None => outputs += (uuid -> (listener, Set(DEFAULT_PORT)))
            }

          case None =>
            val entry = Map.empty[UUID, (ActorRef, Set[String])]
            entry += (uuid -> (listener, Set(DEFAULT_PORT)))
            listeners += (out -> entry)
        }

      //Only input port defined
      case (None, Some(in)) => listeners(DEFAULT_PORT).get(uuid) match {
        case Some(entry) => entry._2 += DEFAULT_PORT
        case None => listeners(DEFAULT_PORT) += (uuid -> (listener, Set(in)))
      }

      //Both ports defined
      case (Some(out), Some(in)) => listeners.get(out) match {
        case Some(output) => output.get(uuid) match {
          //Output exists, input exists
          case Some(entry) => entry._2 += DEFAULT_PORT
          //Output exists, input has to be created
          case None => output += (uuid -> (listener, Set(in)))
        }
        //Create output and input entry
        case None =>
          val entry = Map(uuid -> (listener, Set(in)))
          listeners += (out -> entry)
      }
    }
  }

  /* ================================ */
  /* === Send events to listeners === */
  /* ================================ */

  protected def sendEvent(event: Event, output: Option[String] = None) {
    sendEvent(event, output getOrElse DEFAULT_PORT)
  }

  /**
   * @param event - Event to send
   * @param output - the output the event comes from - can be null (send to DEFAULT_PORT)
   */
  protected def sendEvent(event: Event, output: String) {
    (event, output) match {
      //Handle Results explicite
      case (Results(results, _), null) => sendResults(results, Some(DEFAULT_PORT))
      case (Results(results, _), out) => sendResults(results, Some(out))
      
      //Send to DEFAULT_PORT
      case (_, null) => listeners.get(DEFAULT_PORT) match {
        case Some(e) => e foreach { case (_, (listener, _)) => sendToActor(listener, event) }
        case None => warning(this, "Event " + event + " could not be send")
      }
      //Send to specified port
      case (_, out) => listeners.get(out) match {
        case Some(e) => e foreach { case (_, (listener, _)) => sendToActor(listener, event) }
        case None => warning(this, "Event " + event + " could not be send")
      }
    }
  }

  protected def sendResults(results: Instances, output: Option[String] = None) {
    //Make instances immutable
    val Immutable = classOf[ImmutableInstances] //to pattern match on
    val immutableInstances = results.getClass match {
      case Immutable => results
      case _ => new ImmutableInstances(results)
    }

    // [1] Take all listeners of specified output port
    // [2] Go through all listeners and send Results with specified input 
    val out = output getOrElse DEFAULT_PORT
    listeners.get(out) match {
      case None => warning(this, "Event could not be send to port " + out)
      //Send Results to all inputs for each listener
      case Some(outs) => outs foreach {
        case (_, (listener, inputs)) =>
          inputs foreach {
            case DEFAULT_PORT => sendToActor(listener, new Results(immutableInstances, None))
            case in => sendToActor(listener, new Results(immutableInstances, Some(in)))
          }
      }
    }
  }

  protected def sendToActor(actor: ActorRef, event: Event) {
    if (actor.isRunning)
      actor ! event
  }
}

object TSender {
  val DEFAULT_PORT = "default"
}