package de.lmu.ifi.dbs.knowing.core.util

import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.internal.Activator

import org.osgi.framework.{ InvalidSyntaxException, ServiceReference }

import java.util.Properties

object Util {

  def getFactoryService(id: String): Option[TFactory] = {
    val context = Activator.getContext()
    try {
      val references = context.getServiceReferences(classOf[TFactory].getName, null)
      if (references != null) {
        //Get some type safety in here for shorter code
        val services = references map (r => context.getService(r))
        val loaders = services filter (s => s.asInstanceOf[TFactory].id.equals(id))
        val loader = loaders.headOption
        loader match {
          case Some(loader) => Some(loader.asInstanceOf[TFactory])
          case None => None
        }
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