package de.lmu.ifi.dbs.knowing.core.util

import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.processing._
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import OSGIUtil._
import de.lmu.ifi.dbs.knowing.core.internal.Activator
import de.lmu.ifi.dbs.knowing.core.provider.IDPUProvider
import java.net.URL

/**
 * <p>Util for (de)register DataMining-Factory-Services</p>
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 03.07.2011
 */
class OSGIUtil(context: BundleContext) {

  private var registrations: List[ServiceRegistration] = Nil

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

  def unregisterAll = registrations foreach (r => r.unregister)

}

object OSGIUtil {
  val FACTORY_CLASS = classOf[TFactory].getName
  val LOADER_CLASS = classOf[TLoader].getName
  val PROCESSOR_CLASS = classOf[TProcessor].getName
  val PRESENTER_CLASS = classOf[TPresenter[_]].getName

  def registeredDPUs: Array[DataProcessingUnit] = {
    val services = Activator.tracker.getServices
    if(services == null)
      return Array()
    val provider = services map (_.asInstanceOf[IDPUProvider])
    //FoldLeft function
    val f = (p1: List[DataProcessingUnit], p2: IDPUProvider) => p1 ::: p2.getDataProcessingUnits.toList
    //Actual foldLeft
    val dpus = (List[DataProcessingUnit]() /: provider)(f)
    dpus toArray
  }

  def registeredDPU(name: String): DataProcessingUnit = {
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
}