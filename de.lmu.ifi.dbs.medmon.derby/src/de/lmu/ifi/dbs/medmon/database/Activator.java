package de.lmu.ifi.dbs.medmon.database;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.medmon.database.model.Patient;


public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.medmon.database2"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	public Activator() {
		System.out.println("Activator");
		PersistenceProvider provider = new PersistenceProvider();
		
		Map properties = new HashMap<String, Object>();
		String home = System.getProperty("user.home");
		String sep  = System.getProperty("file.separator");
		String url  = "jdbc:derby:" + home + sep + ".medmon" + sep + "db;create=true";
		System.out.println(url);
		properties.put(PersistenceUnitProperties.JDBC_URL, url);
		
		System.out.println(new File(url).mkdirs());
		EntityManagerFactory emf =  provider.createEntityManagerFactory("medmon", properties);
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Patient p = new Patient();
		p.setLastname("Seiler");
		p.setFirstname("Muki");
		em.persist(p);
		em.getTransaction().commit();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
