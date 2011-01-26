package de.lmu.ifi.dbs.medmon.database.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;

import de.lmu.ifi.dbs.medmon.database.Activator;

public class JPAUtil {
	
	public static String DEFAULT_PERSISTENCE_UNIT = "medmon";

			
	public static boolean isAvailable() {
		if(Activator.getEmfTracker() == null)
			return false;
		if(Activator.getEmfTracker().getServiceReferences() == null)
			return false;
		return Activator.getEmfTracker().getServiceReferences().length > 0;
	}
			
	public static EntityManager createEntityManager() {
		return createEntityManager(DEFAULT_PERSISTENCE_UNIT);
	}
	
	public static EntityManager createEntityManager(String persistence) {
		ServiceReference[] serviceReferences = Activator.getEmfTracker().getServiceReferences();
		for (ServiceReference reference : serviceReferences) {
			BundleContext context = reference.getBundle().getBundleContext();
			String unitName = (String) reference.getProperty(EntityManagerFactoryBuilder.JPA_UNIT_NAME);
			if(unitName.equals(persistence))
				return ((EntityManagerFactory)context.getService(reference)).createEntityManager();
		}
		return null;
	}
	
		
}
