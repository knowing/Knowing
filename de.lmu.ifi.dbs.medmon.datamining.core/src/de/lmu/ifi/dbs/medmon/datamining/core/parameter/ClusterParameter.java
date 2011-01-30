package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;

public class ClusterParameter extends AbstractProcessorParameter<ClusterUnit> {

	private String filterPath = System.getProperty("user.home");
	
	private boolean embedded;
	
	private ClusterUnit embeddable;
	private String descriptor;
	
	public ClusterParameter(String name) {
		this(name, null);
	}
	
	public ClusterParameter(String name, String descriptor) {
		super(name, CLUSTER_TYPE);
		this.descriptor = descriptor;
	}


	@Override
	public ClusterUnit[] getValues() {
		return new ClusterUnit[0];
	}

	@Override
	public void setValue(ClusterUnit value) {
		if(isValid(value)) {
			fireParameterChanged(getName(), embeddable, value);
			embeddable = value;
			embedded = true;
		}
	}
			
	@Override
	public void setValueAsString(String path) {
		setValueAsString(path, false);
	}
	
	public void setValueAsString(String key, boolean embedded) {
		fireParameterChanged(getName(), descriptor, key);
		this.descriptor = key;
		this.embedded = embedded;
	}

	@Override
	public ClusterUnit getValue() {	
		if(embeddable != null)
			return embeddable;
		if(descriptor == null)
			return null;
		try {
			JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
			Unmarshaller um = context.createUnmarshaller();
			embeddable = (ClusterUnit) um.unmarshal(new File(descriptor));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	
		return embeddable;
	}
	
	public ClusterUnit getEmbeddedValue(DataProcessingUnit dpu) {
		if(embedded && embeddable != null)
			return embeddable;
		return dpu.getClusters().get(descriptor);
	}

	@Override
	public boolean isValid(ClusterUnit value) {
		if(value == null || value.getName() == null || value.getName().isEmpty())
			return false;
		return true;
	}
	
	
	public boolean isEmbedded() {
		return embedded;
	}
	
	public String getDescriptor() {
		return descriptor;
	}

	// Should be externial //
	/**
	 * Unmarshall all .xml files that fits to {@link ClusterUnit}
	 * 
	 * @param folder
	 * @return an array of the extracted ClusterUnits
	 * @throws JAXBException
	 */
	public static ClusterUnit[] getClusterUnits(String folder) throws JAXBException {
		File file = new File(folder);
		
		//Nothing found
		if(!file.exists() || !file.isDirectory())
			return new ClusterUnit[0];
		
		String[] files = file.list(new FilenameFilter() {	
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		//TODO getClusterUnits from Folder 
		ClusterUnit[] returns = new ClusterUnit[files.length];
		for (int i = 0; i < returns.length; i++)
			returns[i] = XMLtoClusterUnit(folder + File.separator + files[i]);
				
		return returns;
	}
	
	/**
	 * Unmarshall a certain file
	 * 
	 * @param xmlFile
	 * @return the unmarshalled {@link ClusterUnit}
	 * @throws JAXBException
	 */
	public static ClusterUnit XMLtoClusterUnit(String xmlFile) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
		Unmarshaller um = context.createUnmarshaller();	
		File file = new File(xmlFile);
		return (ClusterUnit) um.unmarshal(file);		
	}
	


}
