package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.lmu.ifi.dbs.medmon.datamining.core.adapter.ParameterAdapter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.XMLParameterWrapper;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;

@XmlRootElement(name = "dataProcessor")
@XmlType(propOrder = { "name", "id", "providedby", "parameters" })
public class XMLDataProcessor  {

	private String name;

	private String id;

	private String providedby;

	private Map<String, IProcessorParameter> parameters = new HashMap<String, IProcessorParameter>();

	/**
	 * Default constructor for JAXB
	 */
	public XMLDataProcessor() {
	}

	/**
	 * Constructor for unmarshalled XML files and DnD support.
	 * 
	 * @param name
	 * @param id
	 * @param providedby
	 * @param parameters
	 */
	public XMLDataProcessor(String name, String id, String providedby, XMLParameterWrapper[] parameters) {
		this.name = name;
		this.id = id;
		this.providedby = providedby;
		try {
			this.parameters = new ParameterAdapter().unmarshal(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor for composing {@link XMLDataProcessor}s from services and
	 * extensionpoints.
	 * 
	 * @param processor
	 */
	public XMLDataProcessor(IDataProcessor processor) {
		this.name = processor.getName();
		this.id = processor.getId();
		this.providedby = "unkown"; // TODO get BundleID
		setParameters(processor.getParameters());
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

	@XmlJavaTypeAdapter(ParameterAdapter.class)
	@XmlElement(name = "parameters")
	public Map<String, IProcessorParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, IProcessorParameter> parameters) {
		this.parameters = parameters;
	}

	public boolean isAvailable() {
		return loadProcessor() != null;
	}
	
	public IDataProcessor loadProcessor() {
		return FrameworkUtil.findDataProcessor(id);
	}


}
