package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;

@XmlRootElement(name = "dataProcessor")
@XmlType(propOrder = { "name", "id", "providedby" })
public class DataProcessor {

	private String name;

	private String id;

	private String providedby;

	public DataProcessor() {
	}

	public DataProcessor(String name, String id, String providedby) {
		this.name = name;
		this.id = id;
		this.providedby = providedby;
	}
	
	public DataProcessor(IDataProcessor processor) {
		this.name = processor.getName();
		this.id = processor.getID();
		this.providedby = "unkown"; //TODO get BundleID
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvidedby() {
		return providedby;
	}

	public void setProvidedby(String providedby) {
		this.providedby = providedby;
	}

	public boolean isAvailable() {
		IDataProcessor processor = FrameworkUtil.findDataProcessor(id);
		return processor != null;
	}
}
