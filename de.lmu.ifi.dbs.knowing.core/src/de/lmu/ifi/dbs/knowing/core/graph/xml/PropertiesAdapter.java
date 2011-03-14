package de.lmu.ifi.dbs.knowing.core.graph.xml;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PropertiesAdapter extends XmlAdapter<Property[], Properties> {

	@Override
	public Properties unmarshal(Property[] v) throws Exception {
		Properties returns = new Properties();
		if(v == null)
			return returns;
		for (Property property : v) 
			returns.setProperty(property.getKey(), property.getValue());
		return returns;
	}

	@Override
	public Property[] marshal(Properties v) throws Exception {
		if(v == null)
			return new Property[0];
		ArrayList<Property> list = new ArrayList<Property>();
		Enumeration<Object> keys = v.keys();
		while(keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String property = v.getProperty(key, "");
			list.add(new Property(key, property));
		}
		return (Property[]) list.toArray(new Property[list.size()]);
	}

}
