package knowing.test.processor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.dbs.knowing.core.test.EmbeddedFactoryDirectory;
import de.lmu.ifi.dbs.knowing.core.factory.TFactory;

public class EmbeddedFactoryTest {

	private EmbeddedFactoryDirectory	directory;

	@Before
	public void setUp() throws Exception {
		directory = new EmbeddedFactoryDirectory();
	}

	@After
	public void tearDown() throws Exception {
		directory = null;
	}

	@Test
	public void testGetFactories() {
		assertTrue(directory.getFactories().length == 0);

		directory.add(new EmptyQueryProcessorFactory()).add(new SerializableProcessorFactory());

		assertTrue(directory.getFactories().length == 2);
	}

	@Test
	public void testGetFactory() {
		assertTrue(directory.getFactories().length == 0);
		TFactory emptyFac = new EmptyQueryProcessorFactory();
		TFactory seriaFac = new SerializableProcessorFactory();

		directory.add(emptyFac).add(seriaFac);

		assertEquals(emptyFac, directory.getFactory(emptyFac.id()).get());
		assertEquals(seriaFac, directory.getFactory(seriaFac.id()).get());
	}

	@Test
	public void testAdd() {
		assertEquals(directory, directory.add(new EmptyQueryProcessorFactory()));
	}
}
