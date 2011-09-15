package de.lmu.ifi.dbs.knowing.core.util

import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration
import org.osgi.framework.InvalidSyntaxException
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.processing._
import OSGIUtil._
import de.lmu.ifi.dbs.knowing.core.internal.Activator
import de.lmu.ifi.dbs.knowing.core.service.IDPUProvider
import java.net.URL
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit

/**
 * <p>Util for (de)register DataMining-Factory-Services</p>
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 03.07.2011
 */
class OSGIUtil(context: BundleContext) {

  private var registrations: List[ServiceRegistration[_]] = Nil

  def registerLoader(factory: TFactory) = registrations = context.registerService(FACTORY_CLASS, factory, null) :: registrations

  def registerSaver(factory: TFactory) = registrations = context.registerService(FACTORY_CLASS, factory, null) :: registrations

  def registerProcessor(factory: TFactory) = registrations = context.registerService(FACTORY_CLASS, factory, null) :: registrations

  def registerPresenter(factory: TFactory) = registrations = context.registerService(FACTORY_CLASS, factory, null) :: registrations

  /* ================================== */
  /* == 							 == */
  /* ================================== */

  //TODO OSGIUtil -> register only with real class, not custom ID

  def registerLoader(factory: TFactory, clazz: String) {
    registrations = context.registerService(FACTORY_CLASS, factory, null) :: registrations
    //    registrations = context.registerService(LOADER_CLASS, factory, null) :: registrations
    //    registrations = context.registerService(clazz, factory, null) :: registrations
  }

  def registerSaver(factory: TFactory, clazz: String) {
    registrations = context.registerService(FACTORY_CLASS, factory, null) :: registrations
    //    registrations = context.registerService(LOADER_CLASS, factory, null) :: registrations
    //    registrations = context.registerService(clazz, factory, null) :: registrations
  }

  def registerProcessor(factory: TFactory, clazz: String) {
    registrations = context.registerService(FACTORY_CLASS, factory, null) :: registrations
    //    registrations = context.registerService(PROCESSOR_CLASS, factory, null) :: registrations
    //    registrations = context.registerService(clazz, factory, null) :: registrations
  }

  def registerPresenter(factory: TFactory, clazz: String) {
    registrations = context.registerService(FACTORY_CLASS, factory, null) :: registrations
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
    val services = Activator.tracker.getServices
    if(services == null)
      return Array()
    val provider = services map (_.asInstanceOf[IDPUProvider])
    //FoldLeft function
    val f = (p1: List[IDataProcessingUnit], p2: IDPUProvider) => p1 ::: p2.getDataProcessingUnits.toList
    //Actual foldLeft
    val dpus = (List[IDataProcessingUnit]() /: provider)(f)
    dpus toArray
  }

  def registeredDPU(name: String): IDataProcessingUnit = {
    val provider = Activator.tracker.getServices map (_.asInstanceOf[IDPUProvider])
    val dpus = for (p <- provider if (p.getDataProcessingUnit(name) != null)) yield p.getDataProcessingUnit(name)
    if (dpus.nonEmpty) dpus(0)
    else null
  }

  def registeredURLtoDPU(name: String): URL = {
    val provider = Activator.tracker.getServices map (_.asInstanceOf[IDPUProvider])
    val urls = for (p <- provider if (p.getURL(name) != null)) yield p.getURL(name)
    if (urls.nonEmpty) urls(0)
    else null
  }
  
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
}
