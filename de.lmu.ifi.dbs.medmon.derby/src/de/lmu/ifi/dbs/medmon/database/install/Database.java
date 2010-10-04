package de.lmu.ifi.dbs.medmon.database.install;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import de.lmu.ifi.dbs.medmon.database.model.Patient;

public class Database {

	private HashMap<String, Object> properties;
	private EntityManagerFactory emf;
	private EntityManager entityManager;

	public Database() {
		properties = new HashMap<String, Object>();
		String home = System.getProperty("user.home");
		String sep = System.getProperty("file.separator");
		String url = "jdbc:derby:" + home + sep + ".medmon" + sep
				+ "db;create=true";
		System.out.println(url);
		properties.put(PersistenceUnitProperties.JDBC_URL, url);

		System.out.println(new File(url).mkdirs());
		connect();
		test();
	}

	private void connect() {
		emf = Persistence.createEntityManagerFactory("medmon", properties);
		entityManager = emf.createEntityManager();

	}
	
	private void test() {
		entityManager.getTransaction().begin();
		Patient patient = new Patient("Muki", "Seiler");
		entityManager.persist(patient);
		entityManager.getTransaction().commit();
		System.out.println("New patient: " + patient + " with id: " + patient.getId());
		
		entityManager.getTransaction().begin();
		Patient muki = entityManager.find(Patient.class, patient.getId());
		System.out.println("Muki: " + muki);
		entityManager.remove(muki);
		entityManager.getTransaction().commit();
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public Object getProperty(String key) {
		return properties.get(key);
	}
}
