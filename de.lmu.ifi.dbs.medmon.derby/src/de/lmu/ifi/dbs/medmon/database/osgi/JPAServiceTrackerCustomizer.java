package de.lmu.ifi.dbs.medmon.database.osgi;

import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class JPAServiceTrackerCustomizer implements ServiceTrackerCustomizer {

	private static final Logger log = Logger.getLogger(JPAServiceTrackerCustomizer.class.getName());

	@Override
	public Object addingService(ServiceReference reference) {
		BundleContext context = reference.getBundle().getBundleContext();
		Object service = context.getService(reference);
		String unitName = (String) reference.getProperty(EntityManagerFactoryBuilder.JPA_UNIT_NAME);
		log.info("EntityManagerFactory for " + unitName + " added");
		return service;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		String unitName = (String) reference.getProperty(EntityManagerFactoryBuilder.JPA_UNIT_NAME);
		log.info("EntityManagerFactory for " + unitName + " modified");

	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		String unitName = (String) reference.getProperty(EntityManagerFactoryBuilder.JPA_UNIT_NAME);
		log.info("EntityManagerFactory for " + unitName + " removed");
	}

}
