package de.lmu.ifi.dbs.medmon.sensor.core.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

import de.lmu.ifi.dbs.medmon.database.model.Sensor;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.sensor.core.Activator;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SensorDaemon {

	private static final Logger logger = Logger.getLogger(Activator.PLUGIN_ID);
	private static final long INTERVAL = 1000;

	private static final Thread sensorDaemon;
	private static SensorDaemon singleton;

	private static volatile boolean initialized;

	private static PropertyChangeSupport support;

	private Map<String, SensorAdapter> model;

	static {
		sensorDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!initialized && JPAUtil.isAvailable()) {
						singleton = new SensorDaemon();
						support = new PropertyChangeSupport(singleton);
						initialized = true;
						logger.info("Daemon initialized");
						System.out.println("Daemon initialized");
					} else if (initialized) {
						singleton.checkSensorsAvailable();
						logger.finest("Check Sensors");
					} 

					try {
						Thread.sleep(INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}

			}
		});

	}

	private SensorDaemon() {
		synchronize();
		initModel();
		checkSensorsAvailable();
	}

	public static SensorDaemon getInstance() {
		return singleton;
	}

	public static boolean startDaemon() {
		if (sensorDaemon.isAlive())
			return false;
		sensorDaemon.setDaemon(true);
		sensorDaemon.setName("Sensor Daemon");
		sensorDaemon.setPriority(Thread.MIN_PRIORITY);
		sensorDaemon.start();
		return false;
	}

	public static void restartDaemon() {
		sensorDaemon.interrupt();
		singleton.initModel();
		singleton.checkSensorsAvailable();
		sensorDaemon.start();

	}

	private void initModel() {
		model = Collections.synchronizedMap(new HashMap<String, SensorAdapter>());

		// Assert that the database is synchronized with extension points
		List<Sensor> entities = getSensorEntities();
		for (Sensor sensor : entities) {
			model.put(sensor.getId(), new SensorAdapter(sensor));
		}

		List<ISensor> extensions = getSensorExtensions();
		for (ISensor sensor : extensions) {
			String key = Sensor.parseId(sensor.getName(), sensor.getVersion());
			SensorAdapter adapter = model.get(key);
			adapter.setSensorExtension(sensor);
		}
	}

	private List<ISensor> getSensorExtensions() {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(ISensor.SENSOR_ID);
		final LinkedList<ISensor> extensions = new LinkedList<ISensor>();
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (!(o instanceof ISensor))
					continue;

				ISafeRunnable runnable = new ISafeRunnable() {
					@Override
					public void handleException(Throwable exception) {
						logger.severe("Exception in client");
					}

					@Override
					public void run() throws Exception {
						extensions.add((ISensor) o);
					}
				};
				SafeRunner.run(runnable);

			}
		} catch (CoreException ex) {
			ex.printStackTrace();
			logger.severe(ex.getMessage());
		}
		return extensions;
	}

	private List<Sensor> getSensorEntities() {
		EntityManager em = JPAUtil.createEntityManager();
		List<Sensor> resultList = em.createNamedQuery("Sensor.findAll", Sensor.class).getResultList();
		em.close();
		return resultList;
	}
	
	private void synchronize() {
		List<ISensor> sensors = getSensorExtensions();
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		for (ISensor<?> sensor : sensors) {
			String id = sensor.getName() + ":" + sensor.getVersion();
			Sensor dbsensor = em.find(Sensor.class , id );
			if(dbsensor == null) {
				em.persist(new Sensor(sensor.getName(), sensor.getVersion(), sensor.getType()));
			}
		}
		em.getTransaction().commit();
		em.close();
	}

	public void checkSensorsAvailable() {
		boolean changed = false;
		for (SensorAdapter adapter : model.values()) {
			// TODO search the db for new sensors
			// TODO search the extension points for new sensors
			boolean available = adapter.isAvailable();
			Sensor entity = adapter.getSensorEntity();
			if (entity == null && available) {
				changed = true;
				adapter.setAvailable(false);
				continue;
			} else {
				String path = entity.getDefaultpath();
				if (path == null || path.isEmpty()) {
					if (available) {
						changed = true;
						adapter.setAvailable(false);
						continue;
					}
				} else {
					File dir = new File(path);
					if (!dir.exists() && available) {
						changed = true;
						adapter.setAvailable(false);
						continue;
					}
					ISensor sensor = adapter.getSensorExtension();
					if (sensor.isSensor(dir)) {
						if (!available) {
							adapter.setAvailable(true);
							changed = true;
							continue;
						}
					} else {
						if (available) {
							adapter.setAvailable(false);
							changed = true;
							continue;
						}
					}
				}
			}
		}
		if (changed) {
			fireModelChanged();
		}
	}

	public Map<String, SensorAdapter> getModel() {
		return model;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	protected void fireModelChanged() {
		if (support != null)
			support.firePropertyChange("model", null, model);
	}

}
