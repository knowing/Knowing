/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.core.util

import org.osgi.framework.{ InvalidSyntaxException, ServiceRegistration, BundleContext }
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.factory.TFactory._
import de.lmu.ifi.dbs.knowing.core.processing._
import de.lmu.ifi.dbs.knowing.core.internal.Activator
import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import java.net.URL
import java.util.{ Dictionary, Hashtable }
import OSGIUtil._

/**
 * <p>Util for (de)register DataMining-Factory-Services</p>
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 03.07.2011
 */
class OSGIUtil(context: BundleContext) {

	private var registrations: List[ServiceRegistration[_]] = Nil

	def registerLoader(factory: TFactory) = registrations = context.registerService(FACTORY_CLASS, factory, createServiceProperties(factory)) :: registrations

	def registerSaver(factory: TFactory) = registrations = context.registerService(FACTORY_CLASS, factory, createServiceProperties(factory)) :: registrations

	def registerProcessor(factory: TFactory) = registrations = context.registerService(FACTORY_CLASS, factory, createServiceProperties(factory)) :: registrations

	def registerPresenter(factory: TFactory) = registrations = context.registerService(FACTORY_CLASS, factory, createServiceProperties(factory)) :: registrations

	/* ================================== */
	/* == 							 == */
	/* ================================== */

	//TODO OSGIUtil -> register only with real class, not custom ID

	def registerLoader(factory: TFactory, clazz: String) {
		registrations = context.registerService(FACTORY_CLASS, factory, createServiceProperties(factory)) :: registrations
		//    registrations = context.registerService(LOADER_CLASS, factory, null) :: registrations
		//    registrations = context.registerService(clazz, factory, null) :: registrations
	}

	def registerSaver(factory: TFactory, clazz: String) {
		registrations = context.registerService(FACTORY_CLASS, factory, createServiceProperties(factory)) :: registrations
		//    registrations = context.registerService(LOADER_CLASS, factory, null) :: registrations
		//    registrations = context.registerService(clazz, factory, null) :: registrations
	}

	def registerProcessor(factory: TFactory, clazz: String) {
		registrations = context.registerService(FACTORY_CLASS, factory, createServiceProperties(factory)) :: registrations
		//    registrations = context.registerService(PROCESSOR_CLASS, factory, null) :: registrations
		//    registrations = context.registerService(clazz, factory, null) :: registrations
	}

	def registerPresenter(factory: TFactory, clazz: String) {
		registrations = context.registerService(FACTORY_CLASS, factory, createServiceProperties(factory)) :: registrations
		//    registrations = context.registerService(PRESENTER_CLASS, factory, null) :: registrations
		//    registrations = context.registerService(clazz, factory, null) :: registrations
	}

	def registerLoader(factory: TFactory, clazz: Class[_ <: TLoader]): Unit = registerLoader(factory, clazz.getName)

	def registerSaver(factory: TFactory, clazz: Class[_ <: TSaver]): Unit = registerSaver(factory, clazz.getName)

	def registerProcessor(factory: TFactory, clazz: Class[_ <: TProcessor]): Unit = registerProcessor(factory, clazz.getName)

	def registerPresenter(factory: TFactory, clazz: Class[_ <: TPresenter[_]]): Unit = registerPresenter(factory, clazz.getName)

	def deregisterAll = registrations foreach (r => r.unregister)

}

object OSGIUtil {
	val FACTORY_CLASS = classOf[TFactory].getName
	val LOADER_CLASS = classOf[TLoader].getName
	val PROCESSOR_CLASS = classOf[TProcessor].getName
	val PRESENTER_CLASS = classOf[TPresenter[_]].getName

	def registeredDPUs: Array[IDataProcessingUnit] = {
		Activator.dpuDirectory.getService match {
			case null => Array()
			case dir => dir.getDPUs
		}
	}

	def registeredDPU(name: String): IDataProcessingUnit = {
		Activator.dpuDirectory.getService.getDPU(name) match {
			case None => null
			case Some(dpu) => dpu
		}
	}

	def registeredURLtoDPU(name: String): URL = {
		Activator.dpuDirectory.getService.getDPUPath(name) match {
			case None => null
			case Some(url) => url
		}
	}

	/**
	 * assumes activated knowing.core bundle!
	 */
	def getFactoryDirectory(): IFactoryDirectory = Activator.factoryDirectory.getService()
	//TODO OSGIUtil: getFactoryDirectory

	/**
	 * <p>Obtain factory service by two attempts</p>
	 * [1] get serviceReference via 'id + "Factory"'
	 * [2] if no service found, retrieve all TFactory services and check ids
	 *
	 * @param Processor id - normally getClass().getName()
	 * @returns Option - Factory to create processor
	 */
	def getFactoryService(id: String): Option[TFactory] = {
		val context = Activator.getContext

		//First try to retrieve service directly
		//ProcessorFactory should follow this convention
		val factoryId = id + "Factory"
		val reference = context.getServiceReference(factoryId)
		if (reference != null) {
			val factory = context.getService(reference).asInstanceOf[TFactory]
			return Some(factory)
		}

		//If no factory could be found directly, check a TFactory-Services
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

	def createServiceProperties(factory: TFactory): Dictionary[String, _] = {
		val properties = new Hashtable[String, Object]
		properties.put(FACTORY_ID, factory.id)
		properties.put(FACTORY_NAME, factory.name)
		properties
	}
}

