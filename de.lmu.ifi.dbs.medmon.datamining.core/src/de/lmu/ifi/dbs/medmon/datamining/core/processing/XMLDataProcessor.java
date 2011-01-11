package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.XMLParameterWrapper;
import de.lmu.ifi.dbs.medmon.datamining.core.property.DataProcessorElement;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;

@XmlRootElement(name = "dataProcessor")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "id", "providedby", "wrappedParameters" })
public class XMLDataProcessor  {

	@XmlElement
	private String name;

	@XmlElement
	private String id;

	@XmlElement
	private String providedby;

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	private List<XMLParameterWrapper> wrappedParameters = new ArrayList<XMLParameterWrapper>();

	private transient Map<String, IProcessorParameter> parameters = new HashMap<String, IProcessorParameter>();

	private transient DataProcessorElement propertySource;

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
	public XMLDataProcessor(String name, String id, String providedby, List<XMLParameterWrapper> parameters) {
		this.name = name;
		this.id = id;
		this.providedby = providedby;
		setWrappedParameters(parameters);
		loadParameters();
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
		loadParameters(processor);
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

	public Map<String, IProcessorParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, IProcessorParameter> parameters) {
		this.parameters = parameters;
		wrappedParameters.clear();
		for (IProcessorParameter parameter : parameters.values())
			wrappedParameters.add(new XMLParameterWrapper(parameter));
	}

	public List<XMLParameterWrapper> getWrappedParameters() {
		return wrappedParameters;
	}

	protected void setWrappedParameters(List<XMLParameterWrapper> wrappedParameters) {
		this.wrappedParameters = wrappedParameters;
	}

	/**
	 * @see loadParameters(IDataProcessor processor)
	 */
	public void loadParameters() {
		IDataProcessor processor = FrameworkUtil.findDataProcessor(id);
		if (processor == null)
			return;
		loadParameters(processor);
	}

	/**
	 * Load the IDataProcessor and evaluate its parameters and set them to the
	 * given values by wrappedParameters
	 * 
	 * @param processor
	 */
	public void loadParameters(IDataProcessor processor) {
		// Set parameter values
		parameters = processor.getParameters();
		for (XMLParameterWrapper each : wrappedParameters) {
			System.out.println(each.getKey() + " -> " + each.getValue());
			IProcessorParameter iProcessorParameter = parameters.get(each.getKey());
			iProcessorParameter.setValueAsString(each.getValue());

		}

	}

	public boolean isAvailable() {
		return loadProcessor() != null;
	}
	
	public IDataProcessor loadProcessor() {
		return FrameworkUtil.findDataProcessor(id);
	}

/*	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class ) {
			if (propertySource == null) {
				propertySource = new DataProcessorElement(this);
			}
			return propertySource;
		}
		return null;
	}*/

}