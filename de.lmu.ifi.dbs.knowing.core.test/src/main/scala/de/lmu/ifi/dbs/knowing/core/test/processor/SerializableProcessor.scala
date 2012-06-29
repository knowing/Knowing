package de.lmu.ifi.dbs.knowing.core.test.processor

import java.io.InputStreamReader
import java.io.LineNumberReader
import java.io.PrintWriter
import java.util.Date
import java.util.Properties
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.processing.TSerializable
import de.lmu.ifi.dbs.knowing.core.events._
import weka.core.Instance
import weka.core.Instances
import akka.actor.{Actor, ActorRef }
import java.io.IOException


class SerializableProcessor extends TProcessor with TSerializable {

  //These values get stored in our serializable model
  private var randomNumber = 0.0
  private var randomString = "empty"

  override def start {
    try {
      val in = inputStream
      in match {
        case None => log.warning("No InputStream defined")
        case Some(i) =>
          log.debug("Trying to open InputStream")
          val reader = new LineNumberReader(new InputStreamReader(i))
          val line = reader.readLine
          if (line != null) {
            randomNumber = line.toDouble
            randomString = reader.readLine
          }
          reader.close
      }
    } catch {
      case e: IOException => log.warning( e.getMessage)
    }
    log.debug("Start Serializable Processor with: " + randomNumber + " / " + randomString)
    //Normally you'll send here no statusChanged, this is just for test purpose so the process terminates
    statusChanged(Finished())
  }

  override def postStop() {
    randomNumber = Math.random
    randomString = new Date toString
    val out = outputStream
    out match {
      case None => log.warning("No OutputStream defined")
      case Some(o) =>
        log.debug("Trying to open OutputStream")
        val writer = new PrintWriter(o)
        writer.println(randomNumber toString)
        writer.println(randomString)
        writer.flush
        writer.close
    }
    log.debug("Stop Serializable Processor and save model: " + randomNumber + " / " + randomString)
  }

  def process(instances: Instances) = { case _ => }

  def query(query: Instances): Instances = throw new UnsupportedOperationException

  def configure(properties: Properties) {}

}

class SerializableProcessorFactory extends ProcessorFactory(classOf[SerializableProcessor]) 