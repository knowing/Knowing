package de.lmu.ifi.dbs.medmon.database.install;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class Database {

	private final Map<String, Object> properties = new HashMap<String, Object>();
	
	public Database() {
		initProperties();
	}
	
	private void initProperties() {
		//Place DB in user.home
		String home = System.getProperty("user.home");
		String sep = System.getProperty("file.separator");
		String dir = home + sep + ".medmon" + sep + "db";
		String url = "jdbc:derby:" + dir + ";create=true";
		properties.put(PersistenceUnitProperties.JDBC_URL, url);
		
		//Create DB directory
		//new File(dir).mkdirs();
	}


	public Object putProperty(String key, Object value) {
		return properties.put(key, value);
	}
	
	public Object getProperty(String key) {
		return properties.get(key);
	}

	/**
	 * 
	 * @return unmodifiable Map
	 */
	public Map<String, Object> getProperties() {	
		return Collections.unmodifiableMap(properties);
	}
	
}
