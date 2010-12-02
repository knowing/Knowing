package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;

public class ClusterParameter implements IProcessorParameter<ClusterUnit> {

	private final String[] clusterFolders;
	private final String name = "Cluster";
	
	private ClusterUnit current;
	
	public ClusterParameter(String clusterFolder) {
		this(new String[] { clusterFolder });
	}
	
	public ClusterParameter(String[] clusterFolders) {
		this.clusterFolders = clusterFolders;
		ClusterUnit[] values = getValues();
		
		//Set default
		if(values.length > 0)
			current = values[0];
	}

	@Override
	public String getName() {
		return name ;
	}

	@Override
	public ClusterUnit[] getValues() {
		LinkedList<ClusterUnit> values = new LinkedList<ClusterUnit>();
		for (String folder : clusterFolders) {
			
			try {
				ClusterUnit[] clusterUnits = getClusterUnits(folder);
				for (ClusterUnit clusterUnit : clusterUnits) 
					values.add(clusterUnit);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			
		}
		ClusterUnit[] returns = new ClusterUnit[values.size()];
		return values.toArray(returns);
	}

	@Override
	public void setValue(ClusterUnit value) {
		if(isValid(value))
			current = value;
	}
	
	@Override
	public void setValueAsString(String value) {
		//TODO ClusterUnit 
		ClusterUnit clusterUnit = new ClusterUnit();
		clusterUnit.setName(value);
		setValue(clusterUnit);
	}

	@Override
	public ClusterUnit getValue() {
		return current;
	}

	@Override
	public boolean isValid(ClusterUnit value) {
		if(value == null || value.getName() == null || value.getName().isEmpty())
			return false;
		return true;
	}
	
	@Override
	public String getType() {
		return CLUSTER_TYPE;
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
		return (ClusterUnit) um.unmarshal(new File(xmlFile));		
	}
	


}
