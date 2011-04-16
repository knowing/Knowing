package de.lmu.ifi.dbs.knowing.core.util

import de.lmu.ifi.dbs.knowing.core.factory.TLoaderFactory
import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.internal.Activator

import org.osgi.framework.{ InvalidSyntaxException, ServiceReference }

import java.util.Properties

object Util {

  def getLoaderService(id: String, properties: Properties): Option[TLoader] = {
    val context = Activator.getContext()
    try {
      val references = context.getServiceReferences(classOf[TLoaderFactory].getName, null)
      if (references != null) {
        //Get some type safety in here for shorter code
        val services = references map (r => context.getService(r))
        val loader = services filter (s => s.asInstanceOf[TLoaderFactory].id.equals(id))
        //should be: loader.headOption
        Some(loader.apply(0).asInstanceOf[TLoader])
      } else {
        None
      }
    } catch {
      case inv: InvalidSyntaxException =>
        inv printStackTrace;
        None
      case e: Exception =>
        e printStackTrace;
        None
    }
  }

}

class Util {

}