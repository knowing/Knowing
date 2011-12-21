package de.lmu.ifi.dbs.knowing.core.common.io

import java.util.Properties
import java.io.ObjectOutputStream

import akka.actor.ScalaActorRef
import weka.core.Instances
import weka.core.converters.SerializedInstancesSaver
import de.lmu.ifi.dbs.knowing.core.processing.TSaver
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory

class InstancesSaver extends TSaver {

  def write(instances: Instances) = outputs foreach {
    case (file, out) =>
      val saver = new SerializedInstancesSaver
      saver.setDestination(out)
      //Should be configured, default: incremental
      saver.setInstances(instances)
      saver.writeBatch
      saver.resetWriter
  }

  def reset() {}

  def configure(properties: Properties) {}

}

class InstancesSaverFactory extends ProcessorFactory(classOf[InstancesSaver])