package knowing.test

import knowing.test.loader.TestLoaderFactory
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleActivator

class Activator extends BundleActivator {

  def start(context: BundleContext) = {  
    Activator.context = context
    context registerService(classOf[TFactory].getName, new TestLoaderFactory(), null )
  }

  def stop(context: BundleContext) = {  
    Activator.context = null
  }

}

object Activator {
  
  private var context: BundleContext = null

  def getContext(): BundleContext = context
}