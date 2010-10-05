package de.lmu.ifi.dbs.medmon.database.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.lmu.ifi.dbs.medmon.database.Activator;

public class JPAUtil {
	
	public static String PERSISTENCE_UNIT = "medmon";
	
	private static EntityManagerFactory emf;
	private static EntityManager entityManager;
	
	static {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, Activator.getDatabase().getProperties());
		entityManager = emf.createEntityManager();
	}

	public static EntityManager currentEntityManager() {
		if(entityManager.isOpen())
			return entityManager;
		return emf.createEntityManager();
	}
	
	public static EntityManager createEntityManager() {
		return emf.createEntityManager();
	}
}
