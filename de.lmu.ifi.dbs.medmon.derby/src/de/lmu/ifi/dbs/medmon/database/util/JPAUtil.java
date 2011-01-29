package de.lmu.ifi.dbs.medmon.database.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * 
 * Every JPAUtil class must be registered as a ServiceTrackerCustomizer.
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 *
 */
public class JPAUtil implements ServiceTrackerCustomizer {

	//Manage the different util classes
	private static Map<String, JPAUtil> utils = new HashMap<String, JPAUtil>();

	//Util attributes
	private final String punit;
	private final Map properties;
	private PersistenceProvider provider;
	private EntityManagerFactory emf;

	/**
	 * A JPAUtil instance should be used as a {@link ServiceTrackerCustomizer} to
	 * react on PersistenProvider-Service changes.
	 * 
	 * @param punit	- The persistence unit this instance supports
	 * @param properties - Customize the EntityManagerFactory
	 * @return
	 */
	public static JPAUtil getServiceTrackerCustomizer(String punit, Map properties) {
		utils.put(punit, null);
		return new JPAUtil(punit, properties);
	}

	private JPAUtil(String punit, Map properties) {
		this.punit = punit;
		this.properties = properties;
	}
	
	public EntityManagerFactory createEntityManagerFactory() {
		if(emf == null)
			emf = provider.createEntityManagerFactory(punit, properties);
		return emf;
	}

	/*=======================*/
	/*==== Util Methods ==== */
	/*=======================*/
	
	public static boolean isAvailable() {
		if (utils == null)
			return false;
		if (utils.isEmpty())
			return false;
		return true;
	}

	public static EntityManager createEntityManager() {
		//there should be a way to set a default p-unit
		// TODO public static EntityManager createEntityManager()
		return createEntityManager("medmon");
	}

	public static EntityManager createEntityManager(String punit) {
		JPAUtil util = utils.get(punit);
		return util.createEntityManagerFactory().createEntityManager();
	}



	/*======================*/
	/*== Service Tracker == */
	/*======================*/
	
	@Override
	public Object addingService(ServiceReference reference) {
		BundleContext context = reference.getBundle().getBundleContext();
		provider = (PersistenceProvider) context.getService(reference);
		utils.put(punit, this);
		return provider;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		BundleContext context = reference.getBundle().getBundleContext();
		provider = (PersistenceProvider) context.getService(reference);
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		provider = null;
	}

}
