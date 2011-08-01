package knowing.test.processor

import java.io.InputStreamReader
import java.io.LineNumberReader
import java.io.PrintWriter
import java.util.Date
import java.util.Properties
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.processing.TSerializable
import de.lmu.ifi.dbs.knowing.core.events._
import weka.core.Instance
import weka.core.Instances
import akka.actor.ActorRef
import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import java.io.IOException
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory


class SerializableProcessor extends TProcessor with TSerializable {

  private var randomNumber = 0.0
  private var randomString = "empty"

  override def start {
    try {
      val in = getInputStream
      in match {
        case None => //nothing
        case Some(i) =>
          debug(this, "Trying to open InputStream")
          val reader = new LineNumberReader(new InputStreamReader(i))
          val line = reader.readLine
          if (line != null) {
            randomNumber = line.toDouble
            randomString = reader.readLine
          }
          reader.close
      }
    } catch {
      case e: IOException => //error(this, e.getStackTraceString)
    }
    debug(this, "Start Serializable Processor with: " + randomNumber + " / " + randomString)
    statusChanged(Finished())
  }

  override def postStop() {
    randomNumber = Math.random
    randomString = new Date toString
    val out = getOutputStream
    out match {
      case None => //nothing
      case Some(o) =>
        debug(this, "Trying to open OutputStream")
        val writer = new PrintWriter(o)
        writer.println(randomNumber toString)
        writer.println(randomString)
        writer.flush
        writer.close
    }
    debug(this, "Stop Serializable Processor with: " + randomNumber + " / " + randomString)
  }

  def build(instances: Instances) {}

  def query(query: Instance): Instances = { null }

  def result(result: Instances, query: Instance) {}

  def configure(properties: Properties) {}

}

class SerializableProcessorFactory extends ProcessorFactory(classOf[SerializableProcessor]) 