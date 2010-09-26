package de.lmu.ifi.dbs.medmon.database.install;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;

public class Database {

	
	private HashMap<String, Object> properties;

	public Database() {
		PersistenceProvider provider = new PersistenceProvider();
		
		properties = new HashMap<String, Object>();
		String home = System.getProperty("user.home");
		String sep  = System.getProperty("file.separator");
		String url  = "jdbc:derby:" + home + sep + ".medmon" + sep + "db;create=true";
		System.out.println(url);
		properties.put(PersistenceUnitProperties.JDBC_URL, url);
		
		System.out.println(new File(url).mkdirs());
		init(provider);

	}
	
	private void init(PersistenceProvider provider) {
		EntityManagerFactory emf =  provider.createEntityManagerFactory("medmon", properties);
		EntityManager em = emf.createEntityManager();
		
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public Object getProperty(String key) {
		return properties.get(key);
	}
}
