/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

/**
 * Immutable version of Properties object. Instances work transparently as
 * Properties object unless you try to change it.
 * 
 * Calling any method that can potentially change the properties throws
 * UnsupportedOperationException
 * ("Immutable Properties object - can't be changed.");
 * 
 * @author Ondřej Žižka, Nepomuk Seiler
 * @see java.util.Properties
 * @version 1.1
 */
public class ImmutableProperties extends java.util.Properties {

	private static final long serialVersionUID = -6583829605292217614L;

	/**
	 * Constructs an immutable copy of the given Properties object.
	 * 
	 * @param original
	 *            Properties object to copy.
	 */
	public ImmutableProperties(Properties original) {
		// super.defaults = original; //Doesn't work if properties.get("key", "default-value") is used
		// super.putAll( original ); //Doesn't work because put throws exception
		for (Map.Entry<Object, Object> e : original.entrySet())
			super.put(e.getKey(), e.getValue());
	}

	// -- Properties overrides --

	@Override
	public Object setProperty(String key, String val) {
		throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
	}

	@Override
	public synchronized void load(Reader arg0) throws IOException {
		throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
	}

	@Override
	public synchronized void load(InputStream arg0) throws IOException {
		throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
	}

	@Override
	public synchronized void loadFromXML(InputStream arg0) throws IOException, InvalidPropertiesFormatException {
		throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
	}

	// -- Hashtable overrides --

	@Override
	public synchronized void clear() {
		throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
	}

	@Override
	public synchronized Object put(Object arg0, Object arg1) {
		throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
	}

	@Override
	public synchronized void putAll(Map<? extends Object, ? extends Object> arg0) {
		throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
	}

	@Override
	public synchronized Object remove(Object arg0) {
		throw new UnsupportedOperationException("Immutable Properties object - can't be changed.");
	}

}// class ImmutableProperties
