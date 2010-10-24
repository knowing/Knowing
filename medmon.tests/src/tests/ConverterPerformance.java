package tests;

import java.io.IOException;
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.sendsor.accelerationSensor.converter.SDRConverter;

import junit.framework.TestCase;

public class ConverterPerformance extends TestCase {

	private EntityManager entityManager;
	private SDRConverter converter;

	protected void setUp() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("DerbyPerformance");
		entityManager = emf.createEntityManager();
		converter = new SDRConverter();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testConnectToDB() {
		assertNotNull(entityManager);
		entityManager.createQuery("SELECT p FROM Patient p");
	}

	public void testConvertToBlock() throws IOException {
		String file = "/home/muki/input.sdr";
		converter.convertToBlock(file, Calendar.HOUR_OF_DAY);
	}

	public void testParseBlockToContainer() {	
		fail("Not yet implemented");
	}

	public void testParseBlockToData() {
		fail("Not yet implemented");
	}

	public void testConvertSDRtoData() {
		fail("Not yet implemented");
	}

}
