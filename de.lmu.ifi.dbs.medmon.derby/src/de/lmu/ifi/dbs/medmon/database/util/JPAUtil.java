package de.lmu.ifi.dbs.medmon.database.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * 
 * Every JPAUtil class must be registered as a ServiceTrackerCustomizer.
 * 
 * @author Nepomuk Seiler
 * @version 0.3
 * 
 */
public class JPAUtil implements ServiceTrackerCustomizer {

	// Manage the different util classes
	private static Map<String, JPAUtil> utils = new HashMap<String, JPAUtil>();

	// Util attributes
	private final String punit;
	private final Map properties;
	private EntityManagerFactoryBuilder emfBuilder;
	private EntityManagerFactory emf;

	//
	private ServiceTracker dsTracker;
	private boolean driverAvailable = false;
	
    public static final String EMBEDDED_DERBY_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String JDBC_4_VERSION = "4.0";

	/**
	 * A JPAUtil instance should be used as a {@link ServiceTrackerCustomizer}
	 * to react on PersistenProvider-Service changes.
	 * 
	 * @param punit
	 *            - The persistence unit this instance supports
	 * @param properties
	 *            - Customize the EntityManagerFactory
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
		if (emf == null)
			emf = emfBuilder.createEntityManagerFactory(properties);
		return emf;
	}
	
	public boolean isReady() {
		return driverAvailable;
	}

	/* ======================= */
	/* ==== Util Methods ==== */
	/* ======================= */

	public static boolean isAvailable() {
		if (utils == null)
			return false;
		if (utils.isEmpty())
			return false;
		return true;
	}
	
	public static boolean isAvailable(String punit) {
		boolean available = isAvailable();
		JPAUtil util = utils.get(punit);
		available = available && util.isReady();
		return available;
	}

	public static EntityManager createEntityManager() {
		// there should be a way to set a default p-unit
		// TODO public static EntityManager createEntityManager()
		return createEntityManager("medmon");
	}

	public static EntityManager createEntityManager(String punit) {
		JPAUtil util = utils.get(punit);
		return util.createEntityManagerFactory().createEntityManager();
	}

	/* ====================== */
	/* == Service Tracker === */
	/* ====================== */

	@Override
	public Object addingService(ServiceReference reference) {
		BundleContext context = reference.getBundle().getBundleContext();
		String unitName = (String) reference.getProperty(EntityManagerFactoryBuilder.JPA_UNIT_NAME);
		emfBuilder = (EntityManagerFactoryBuilder) context.getService(reference);
		if (!unitName.equals(punit))
			return emfBuilder;

		dsTracker = new ServiceTracker(context, DataSourceFactory.class.getName(), new DataSourceTrackerCustomizer());
		dsTracker.open();
		utils.put(punit, this);
		return emfBuilder;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		BundleContext context = reference.getBundle().getBundleContext();
		emfBuilder = (EntityManagerFactoryBuilder) context.getService(reference);
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		emfBuilder = null;
		if(dsTracker != null)
			dsTracker.close();
	}

	/* ====================== */
	/* == Service Tracker === */
	/* ====================== */

	private class ProviderTrackerCustomzier implements ServiceTrackerCustomizer {

		@Override
		public Object addingService(ServiceReference reference) {
			// TODO ProviderTrackerCustomzier available?
			BundleContext context = reference.getBundle().getBundleContext();
			context.getService(reference);
			return null;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {

		}

		@Override
		public void removedService(ServiceReference reference, Object service) {

		}
	}

	/* ====================== */
	/* == Service Tracker === */
	/* ====================== */

	private class DataSourceTrackerCustomizer implements ServiceTrackerCustomizer {

		@Override
		public Object addingService(ServiceReference reference) {
			// TODO DataSource available?
			BundleContext context = reference.getBundle().getBundleContext();
			Object service = context.getService(reference);
			String driver = (String) reference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS);
			String version = (String) reference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_VERSION);
			if(driver.equals(EMBEDDED_DERBY_DRIVER_NAME))
				driverAvailable = true;
			
			// We have a JDBC service
			DataSourceFactory dsf = (DataSourceFactory) service;
			return dsf;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {

		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			String driver = (String) reference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS);
			if(driver.equals(EMBEDDED_DERBY_DRIVER_NAME))
				driverAvailable = false;
		}

	}

}
